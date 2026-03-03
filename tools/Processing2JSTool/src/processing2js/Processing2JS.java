/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import processing.app.Base;
import processing.app.Sketch;
import processing.app.SketchCode;
import processing.app.ui.Editor;

/**
 *
 * @author dahjon
 */
public class Processing2JS extends JFrame {

    final Color BG_COLOR = new Color(19, 38, 56);
    final Color BG_COLOR2 = new Color(39, 58, 76);

    Properties prop = new Properties();
    final private String PROP_FILE_NAME = "Processing2js.prop";
    static String PROP_AUTORUN = "AUTORUN";
    static String PROP_AUTO_INIT_ARRAYS = "AUTO_INIT_ARRAYS";
    static String PROP_PORT = "PORT";

    final private String LAST_PROC_FILE = "LAST_PROC_FILE";
    JTextField txfProcessing = new JTextField(50);
    JButton chooseProcFileButton = new JButton("Choose file");
    JButton portButton;
    Font monoFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    JTextArea sourceArea = new JTextArea();
    JTextArea jsArea = new JTextArea();
    JTree tree = new JTree();
    final public static String CHARSET = "utf8";
    static JTextArea msgArea = new JTextArea();
    JCheckBox autoRunButton = new JCheckBox("Autorun");
    JCheckBox autoInitArrysRunButton = new JCheckBox("Auto initialize numeric arrays");
    JButton loadButton = new JButton();
    JButton runButton = new JButton("Convert and run");
    JRadioButton pdeModeButton = new JRadioButton("PDE");
    JRadioButton diskModeButton = new JRadioButton("Disk");
    String diskPath = null;

    String tempDir;
    Base base;
    boolean pdeMode;

    public Processing2JS() {
        this(null);
    }

    public Processing2JS(Base base) {
        setTitle("Processing2JS");
        //Debug.trace("source = '" + source+"'");
        this.base = base;

        tempDir = System.getProperty("java.io.tmpdir");
        loadProperties();

        portButton = new JButton("Port: " + JDWebbServer.getPort());
        //init(base);
        JPanel pathPanel = new JPanel();
        pathPanel.setBackground(BG_COLOR);
        TitledBorder titledPathBorder = BorderFactory.createTitledBorder("Current Processing Sketch");
        titledPathBorder.setTitleColor(Color.WHITE);

        pathPanel.setBorder(new CompoundBorder(
                new BevelBorder(BevelBorder.LOWERED),
                titledPathBorder
        ));
        JPanel runPanel = new JPanel();
        runPanel.setBackground(BG_COLOR);

        TitledBorder sourceBorder = BorderFactory.createTitledBorder("Processing Source");
        sourceArea.setBorder(sourceBorder);
        TitledBorder syntaxBorder = BorderFactory.createTitledBorder("Syntax tree");
        tree.setBorder(syntaxBorder);
        TitledBorder jsBorder = BorderFactory.createTitledBorder("Generated Javascript Code");
        jsArea.setBorder(jsBorder);
        TitledBorder msgBorder = BorderFactory.createTitledBorder("Error messages");
        msgArea.setBorder(msgBorder);

        TitledBorder titledRunBorder = BorderFactory.createTitledBorder("Run");
        titledRunBorder.setTitleColor(Color.WHITE);
        runPanel.setBorder(new CompoundBorder(
                new BevelBorder(BevelBorder.LOWERED),
                titledRunBorder
        ));
        JPanel settingsPanel = new JPanel();
        settingsPanel.setBackground(BG_COLOR);
        TitledBorder titledSettingBorder = BorderFactory.createTitledBorder("Settings");
        titledSettingBorder.setTitleColor(Color.WHITE);
        settingsPanel.setBorder(new CompoundBorder(
                new BevelBorder(BevelBorder.LOWERED),
                titledSettingBorder
        ));
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        pathPanel.add(txfProcessing);
        txfProcessing.addActionListener(this::ValjFilActionListener);
        chooseProcFileButton.addActionListener(this::ValjFilActionListener);
        pathPanel.add(chooseProcFileButton);

        if (base != null) {
            JPanel loadFromPanel = new JPanel();
            loadFromPanel.setBackground(BG_COLOR);
            TitledBorder titledloadFromBorder = BorderFactory.createTitledBorder("Load sketch from");
            titledloadFromBorder.setTitleColor(Color.WHITE);

            loadFromPanel.setBorder(new CompoundBorder(
                    new BevelBorder(BevelBorder.LOWERED),
                    titledloadFromBorder
            ));
            ButtonGroup modeGroup = new ButtonGroup();
            modeGroup.add(pdeModeButton);
            modeGroup.add(diskModeButton);
            pdeModeButton.setBackground(BG_COLOR);
            pdeModeButton.setForeground(Color.WHITE);
            pdeModeButton.addActionListener(this::modeActionListener);
            diskModeButton.addActionListener(this::modeActionListener);

            diskModeButton.setForeground(Color.WHITE);
            diskModeButton.setBackground(BG_COLOR);
            loadFromPanel.add(diskModeButton);
            loadFromPanel.add(pdeModeButton);
            pathPanel.add(loadFromPanel);
        }
        loadButton.addActionListener(this::loadActionListener);
        runButton.addActionListener(this::runActionListener);
        //autoRunButton.setBorder(new BevelBorder(10));
        JPanel settingsAndRunPanel = new JPanel();
        settingsAndRunPanel.setBackground(BG_COLOR);
        settingsPanel.add(portButton);
        portButton.addActionListener(this::portActionListerner);
        autoRunButton.setForeground(Color.WHITE);
        autoRunButton.setBackground(BG_COLOR);
        settingsPanel.add(autoRunButton);
        autoRunButton.addActionListener(this::autoRunActionListener);
        autoInitArrysRunButton.setForeground(Color.WHITE);
        autoInitArrysRunButton.setBackground(BG_COLOR);
        settingsPanel.add(autoInitArrysRunButton);
        autoInitArrysRunButton.addActionListener(this::autoInitArraysActionListener);
        runPanel.add(loadButton);
        runPanel.add(runButton);
        topPanel.add(pathPanel);
        topPanel.add(settingsAndRunPanel);
        settingsAndRunPanel.add(settingsPanel);
        settingsAndRunPanel.add(runPanel);
        add(topPanel, BorderLayout.NORTH);

        expandAllNodes(tree, 0, tree.getRowCount());
//        add(sourceAre, BorderLayout.WEST);
//        add(new JScrollPane(tree), BorderLayout.CENTER);
//        add(jsArea, BorderLayout.EAST)

//        JPanel cPanel = new JPanel();
//        cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.X_AXIS));
        //cPanel.add(new JScrollPane(sourceArea));
        JSplitPane cInnerPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tree), new JScrollPane(jsArea));
        cInnerPanel.setDividerLocation(300);

        JSplitPane cPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(sourceArea), cInnerPanel);
        cPanel.setDividerLocation(300);

        JSplitPane bigPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cPanel, new JScrollPane(msgArea));
        bigPanel.setDividerLocation(600);
        //cPanel.add(new JScrollPane(tree));
        //cPanel.add(new JScrollPane(jsArea));
        //bigPanel.add(cPanel);
        add(bigPanel);
//        add(new JScrollPane(msgArea), BorderLayout.SOUTH);
        setSize(1050, 900);
        setVisible(true);
        //laddaFilOchKonvertera();
        setPDEmode(base != null);

        Thread t = new Thread(this::init);
        t.start();

    }

    public void init() {
        StringBuilder sourceStr = new StringBuilder();
        if (Debug.isDebugOn()) {
            sourceStr = Processing2JS.codeStr;
        }
        if (base != null) {
            sourceStr = loadFromPde();

        } else {
            setDefaultCloseOperation(EXIT_ON_CLOSE);

        }
        sourceArea.setText(sourceStr.toString());

        try {
            String p5Code = convert(sourceStr.toString());
            if (autoRunButton.isSelected() && !sourceArea.getText().isEmpty()) {
                saveFilesAndRun(p5Code);
            }
        } catch (SyntaxErrorException ex) {
            appendMessage(ex.getMessage()
                    + ", codeStr:\n"
                    + ex.codeStr);

        }
    }

    public StringBuilder loadFromPde() throws HeadlessException {
        StringBuilder sourceStr;
        Editor editor = base.getActiveEditor();
        Sketch sketch = editor.getSketch();
        File fil = editor.getSketch().getMainFile();
        String absolutePath = fil.getAbsolutePath();
        if (diskPath == null) {
            diskPath = absolutePath;
        }
        txfProcessing.setText(absolutePath);
        saveProperties();
        SketchCode[] scs = sketch.getCode();
        sourceStr = new StringBuilder();
        for (int i = 0; i < scs.length; i++) {
            try {
                SketchCode sc = scs[i];

                sourceStr.append(sc.getDocumentText());
                sourceStr.append("\n");
            } //sourceStr = new StringBuilder(editor.getTextArea().getText());
            catch (BadLocationException ex) {
                JOptionPane.showMessageDialog(this, "Tried to get sketch nr: " + i + "\n" + ex.getMessage());
            }

        }
        return sourceStr;
    }

    public void setPDEmode(boolean pdeMode) {
        this.pdeMode = pdeMode;
        pdeModeButton.setSelected(pdeMode);
        diskModeButton.setSelected(!pdeMode);
        if (pdeMode) {
            Debug.tracePrio("PDE mode");
            txfProcessing.setEnabled(false);
            Editor editor = base.getActiveEditor();
            File fil = editor.getSketch().getMainFile();
            String absolutePath = fil.getAbsolutePath();
            txfProcessing.setText(absolutePath);

            chooseProcFileButton.setEnabled(false);
            loadButton.setText("Reload from PDE, convert and run");
        } else {
            Debug.tracePrio("disk mode, diskpath: " + diskPath);
            txfProcessing.setText(diskPath);
            txfProcessing.setEnabled(true);
            chooseProcFileButton.setEnabled(true);
            loadButton.setText("Reload from disk, convert and run");

        }
    }

    public static void appendMessage(String txt) {
        msgArea.append(txt);
    }

    private void ValjFilActionListener(ActionEvent ae) {
        JFileChooser valj = new JFileChooser();
        String lastPath = txfProcessing.getText();
        String startDir;
        if (lastPath.length() == 0) {
            startDir = "C:\\Users\\dahjon\\Google Drive\\processing";
        } else {
            startDir = lastPath;
        }
        Debug.trace("startDir. " + startDir);
        valj.setCurrentDirectory(new File(startDir));
        valj.setPreferredSize(new Dimension(800, 600));

        if (valj.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            setPDEmode(false);
            txfProcessing.setText(valj.getSelectedFile().getAbsolutePath());
            loadAndConvert();
        }

    }

    private void loadAndConvert() {
        saveProperties();
        String src;
        if (pdeMode) {
            src = loadFromPde().toString();
        } else {
            src = loadFile();
        }
        try {
            String p5Code = convert(src);
            saveFilesAndRun(p5Code);
            //sparaFil();
        } catch (SyntaxErrorException ex) {
            appendMessage(ex.getMessage()
                    + "codeStr:\n"
                    + ex.codeStr);
        }

    }

    private void runActionListener(ActionEvent ae) {
        saveProperties();
        try {
            String p5Code = convert(sourceArea.getText());
            saveFilesAndRun(p5Code);
        } catch (SyntaxErrorException ex) {
            appendMessage(ex.getMessage()
                    + "codeStr:\n"
                    + ex.codeStr);
        }
    }

    private void autoRunActionListener(ActionEvent ae) {
        saveProperties();
    }

    private void autoInitArraysActionListener(ActionEvent ae) {
        saveProperties();
    }

    private void loadActionListener(ActionEvent ae) {

        Thread t = new Thread(this::loadAndConvert);
        t.start();

    }

    private void modeActionListener(ActionEvent ae) {
        setPDEmode(pdeModeButton.isSelected());

    }

    private void portActionListerner(ActionEvent ae) {
        String portStr = JOptionPane.showInputDialog(this, "Enter port to use with Processing2JS webbserver", JDWebbServer.getPort());
        if (portStr != null && !portStr.isEmpty()) {
            try {
                int port = Integer.parseInt(portStr);
                JDWebbServer.getInstance().changePortAndRestart(port);
                portButton.setText("Port: " + port);
                saveProperties();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "The portnumber has to be an integer");
            }
        }
    }

    private String convert(String src) throws SyntaxErrorException {
        loadButton.setEnabled(false);
        runButton.setEnabled(false);
        StringBuilder proc = new StringBuilder(src);
//        try {
        Global global = new Global(proc, new File(txfProcessing.getText()).getParentFile(), autoInitArrysRunButton.isSelected());

        Debug.trace("------------------------------------------------------------------------");
        Debug.traceExtended("global:\n " + global);
        final DefaultMutableTreeNode treeNode = global.getTreeNode();
        SwingUtilities.invokeLater(new SetTree(treeNode));
        String jsCode = global.getP5jsCode();
        SwingUtilities.invokeLater(new SetJsTextAndEnableButtons(jsCode));

//        } catch (Exception e) {
//            appendMessage(e.getMessage());
//        }
        return jsCode;
    }

    private class SetTree implements Runnable {

        DefaultMutableTreeNode treeNode;

        public SetTree(final DefaultMutableTreeNode treeNode) {
            this.treeNode = treeNode;
        }

        @Override
        public void run() {
            TreeModel tm = new DefaultTreeModel(treeNode);
            tree.setModel(tm);
            expandAllNodes(tree, 0, tree.getRowCount());
        }
    }

    private class SetJsTextAndEnableButtons implements Runnable {

        String text;

        public SetJsTextAndEnableButtons(String c) {
            text = c;
        }

        @Override
        public void run() {
            loadButton.setEnabled(true);
            runButton.setEnabled(true);
            jsArea.setText(text);
        }
    }

    private void saveFilesAndRun(String p5Code) {
        try {
            //String p5Code = jsArea.getText();

            String procFilePathStr = txfProcessing.getText();

            JSFiler.saveFilesAndRun(procFilePathStr, p5Code);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save Javascript file: " + ex.getMessage());
        }

    }

    private ArrayList<String> getOtherPDEFiles(Path path) {
        Debug.trace("->getOtherPDEFiles");

        Path par = path.getParent();
        ArrayList<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(par)) {
            for (Path p : directoryStream) {
                String name = p.getFileName().toString();
                Debug.trace("getOtherPDEFiles name = " + name);
                if (name.substring(name.length() - 4).equalsIgnoreCase(".pde") && !name.equals(path.getFileName().toString())) {

                    fileNames.add(p.toString());
                }
            }
        } catch (IOException ex) {
        }
        return fileNames;
    }

    private String getOtherPDEFilesAsOneString(Path path) throws IOException {
        String ret = "";
        ArrayList<String> fileNames = getOtherPDEFiles(path);
        for (int i = 0; i < fileNames.size(); i++) {
            String name = fileNames.get(i);
            Debug.trace("getOtherPDEFilesAsOneString name = " + name);
            String c = new String(Files.readAllBytes(Paths.get(name)), CHARSET);
            ret += c;
        }
        return ret;
    }

    private Path dir2SketchFile() {
        Path path = Paths.get(txfProcessing.getText());
        if (Files.isDirectory(path)) {
            String name = path.getFileName().toString() + ".pde";
            path = Paths.get(path.toString() + File.separator + name);
            txfProcessing.setText(path.toString());
        }
        return path;
    }

    private String loadFile() {
        Path path = dir2SketchFile();
        Debug.trace("laddaFil path: " + path);
        try {
            String c = new String(Files.readAllBytes(path), CHARSET);
            String a = getOtherPDEFilesAsOneString(path);
            c = c + a;
            Debug.trace("Hela koden:\n" + c);
            SwingUtilities.invokeLater(new SetSourceText(c));
            Debug.trace("Efter sourceArea.setText(c)");
            return c;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not load processingfile: " + ex.getMessage());
        }
        return "";
    }

    private class SetSourceText implements Runnable {

        String text;

        public SetSourceText(String c) {
            text = c;
        }

        @Override
        public void run() {
            sourceArea.setText(text);
        }
    }

    public static void main(String[] args) {

        new Processing2JS();
    }

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    private void loadProperties() {
        try {
            final String propPath = tempDir + File.separator + PROP_FILE_NAME;
            Debug.trace("propPath = " + propPath);
            prop.load(new FileInputStream(propPath));
            autoRunButton.setSelected(Boolean.parseBoolean(prop.getProperty(PROP_AUTORUN, "true")));
            autoInitArrysRunButton.setSelected(Boolean.parseBoolean(prop.getProperty(PROP_AUTO_INIT_ARRAYS, "true")));
            JDWebbServer.setPort(Integer.parseInt(prop.getProperty(PROP_PORT, "8005")));
            diskPath = prop.getProperty(LAST_PROC_FILE);
        } catch (IOException ex) {
            System.out.println("Can't find the Processing2JS properties file.\n"
                    + "Hopefully it is because it is the first time you run the program.\n"
                    + "Error message: " + ex.getMessage());
            //JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void saveProperties() {
        try {
            final String propPath = tempDir + File.separator + PROP_FILE_NAME;

            prop.setProperty(PROP_AUTORUN, "" + autoRunButton.isSelected());
            prop.setProperty(PROP_AUTO_INIT_ARRAYS, "" + autoInitArrysRunButton.isSelected());
            prop.setProperty(PROP_PORT, "" + JDWebbServer.getPort());
            prop.setProperty(LAST_PROC_FILE, txfProcessing.getText());
            prop.store(new FileOutputStream(propPath), null);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    static public StringBuilder codeStr = new StringBuilder(""
            + "//js let x = 232;\n"
            + "void setup() {\n"
            + "   size(400, 400);\n"
            + "   //js console.log(x);\n"
            + "  println(\"hej\");\n"
            + "   line(20,20,40,40);\n"
            + "   line(new PVector(320,320),new PVector(340,340),new PVector(400,200)); \n"
            + "   line(new PVector(120.0,120.0),new PVector(140,140));\n"
            + "   line(new PVector(220,220),240.0,240.0);\n"
            + "}\n"
            + "\n"
            + "\n"
            + "void line(PVector p1,PVector p2){\n"
            + "   console.log(\"Hej x är \"+x);\n"
            + "  //js ellipse(x,x,30,30);\n"
            + "  line(p1.x, p1.y,p2.x, p2.y); \n"
            + "}\n"
            + "\n"
            + "void line(PVector p1, float x, float y){\n"
            + "  //js line(p1.x, p1.y,x, y);\n"
            + "}\n"
            + "void line(PVector p1,PVector p2,PVector p3){\n"
            + "  line(p1.x, p1.y,p2.x, p2.y);\n"
            + "  line(p2.x, p2.y,p3.x, p3.y);\n"
            + "}\n");
//            + "void setup(){\n"
//            + "  if (key == '+' || key == '=') {\n"
//            + "    if (currentGestureID >= 0) {\n"
//            + "      float th = gestureArray[currentGestureID].thickness;\n"
//            + "      gestureArray[currentGestureID].thickness = min(96, th+1);\n"
//            + "      gestureArray[currentGestureID].compile();\n"
//            + "    }\n"
//            + "  }size(400,400);\n"
//            + "if((y=3)==3){ prinln(\"hej\");}\n"
//            + "int[] values = {20,30,100,200};"
//            + "  for(int i=0; i<10; i++) {ellipse(i,i,i,i);}\n"
//            + "  for(int i: values) {ellipse(i,i,i,i);println(i);}\n"
//            + "int j = 0;\n"
//            + "  if(j==0) j=1; else j=2;\n"
//            + "}\n");

//            "int x []={3,5,7,9};\n"
//            + "int draw=34;\n"
//            + "float fv=22.3f;\n"
//            + "Image img;"
//            + "final int Start_State = 1;\n"
//            //            "int[][] x=new int[2][6];\n"
//            + "void setup(){\n"
//            + "  size(800,800);\n"
//            +"   img = loadImage(\"hej.png\");\n"
//            +"   img = loadImage(\"då.png\");\n"
//            +"   img = minGrej(\"då.png\");//preload\n"
//            + "  println(\"Knappen nedtryckt: \"+counter++);\n"
//            + "  for(int i : x){"
//            + "}\n"
//            + "  draw(10,200);\n"
//            + "}\n"
//            + "int[][] draw(){\n"
//            + "  //Hej alla barn\n"
//            + "  final int y=40;//notjs\n"
//            + "  int x=34;//notjs1\n"
//            + "  //js let z=40;\n"
//            + "  if(x<(int)400.0){\n"
//            + "    x++;\n"
//            + "    ellipse(-x,z[2],x,x);\n"
//            + "    hej[2].flytta(b[2],y,a[2],1);\n"
//            + "  }\n"
//            + " delay(200);\n"
//            + "  return 345;\n"
//            + "}\n"
//            + "int draw(int i, float a){\n"
//            + "   print(i+a);\n"
//            + "}\n"
//            + "class hej{\n"
//            + "    int y=3;\n"
//            + "    void rita(){\n"
//            + "       rect(x,y,10,10);\n"
//            + "       flytta();\n"
//            + "    }"
//            + "    void flytta(){\n"
//            + "       x+=10;\n"
//            + "    }"
//            + "  }\n"
//    );
    // int y = 3;
//            + "int i=0;\n"
//            + "void setup() {\n"
//            + "   size(400, 400);\n"
//            + "   \n"
//            + "}\n"
//            + "\n"
//            + "void draw() {\n"
//            + "   String a=LEFT;\n"
//            + "   if(a==RIGHT)\n"
//            + "   {\n"
//            + "      text(\"Hej\",20,20);\n"
//            + "   }\n"
//            + "   else {\n"
//            + "      rect(20,20,20);\n"
//            + "   }\n"
//            + "   ellipse(i,i,i,i);\n"
//            + "   i=i+1;\n"
//            + "}");
//            + "int rakna = 3;"
//            + "void setup() {\n"
//            + "  size(200,600);\n"
//            + "  fill(0);\n"
//            + "  for (int tal=2; tal < 50; tal++) {\n"
//            + "    if (arPrimtal(tal)) {\n"
//            + "      text(tal + \" är ett primtal\", 15, 12*tal);\n"
//            + "    } else {\n"
//            + "      text(tal + \" är inte ett primtal\", 15, 12*tal);\n"
//            + "    }\n"
//            + "  }\n"
//            + "  saveFrame(\"procass.png\");\n"
//            + "}\n"
//            + "\n"
//            + "\n"
//            + "boolean arPrimtal(int tal) {\n"
//            + "  for (int i=2; i < tal; i++) {\n"
//            + "    println(\"i: \" + i + \" tal%i \"+tal%i);\n"
//            + "    if (tal%i==0) {\n"
//            + "      return false;\n"
//            + "    }\n"
//            + "  }\n"
//            + "  return true;\n"
//            + "}";
    public static final String BASETYPES = "void|boolean|color|int|float|double|long|String|StringBuffer|char|byte|ArrayList<.+>|PVector";
    static String functionPatternStr = "(" + BASETYPES + ")\\s+([a-zA-Z0-9]+)\\([ ,.a-zA-Z0-9\\[\\]]*\\)";

}
