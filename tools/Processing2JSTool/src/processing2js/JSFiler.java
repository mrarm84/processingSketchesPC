/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author dahjon
 */
public class JSFiler {

//    final static private String BASE_OUTPUT_DIR = "D:\\xampp\\htdocs\\p5";
//    final static private String BASE_OUTPUT_DIR = "C:\\wamp64\\www\\p5";
    final static private String BASE_OUTPUT_DIR = "C:\\inetpub\\wwwroot\\p5";
    final static public String BROWSER_PATH = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
    final static public String BASE_URL = "http://localhost/p5/";
    final static public String HTML_FILE_NAME = "index.html";

    static public void saveFilesAndRun(String procFilePathStr, String p5Code) throws IOException {
        procFilePathStr = procFilePathStr.replaceAll(".pde", ".js");
        //System.out.println("p5Code = " + p5Code);
        Path procFilePath = Paths.get(procFilePathStr);
        Debug.tracePrio("procFilePath = " + procFilePath);
        String fileName = procFilePath.getFileName().toString();
        Debug.tracePrio("fileName = " + fileName);
        String dirName = fileName.substring(0, fileName.length() - 3);
        Debug.tracePrio("dirName = " + dirName);
        //sparaP5jsFil(dirName, fileName, p5Code);
        sparaP5jsFilinSketch(procFilePath, p5Code);
        final String sketchBookPath = procFilePath.getParent().toString();
//        sparaHTMLFil(BASE_OUTPUT_DIR + File.separator +dirName, fileName);
        sparaHTMLFil(sketchBookPath, fileName);
        //runInBrowser(dirName);
        runInBrowserJDServer(sketchBookPath);
    }

    public static void sparaP5jsFilinSketch(Path procFilePath, String p5Code) throws IOException {
        Files.write(procFilePath, p5Code.getBytes());

    }

    public static void sparaP5jsFil(String dirName, String fileName, String p5Code) throws IOException {
        String dirPathStr = BASE_OUTPUT_DIR + File.separator + dirName;
        Path dirPath = Paths.get(dirPathStr);
        if (!Files.exists(dirPath)) {
            Files.createDirectory(dirPath);
        }
        String p5FilePath = dirPathStr + File.separator + fileName;
        Debug.trace("p5FilePath = " + p5FilePath);
        Debug.trace("fileName = " + fileName);
        Path path = Paths.get(p5FilePath);

        Files.write(path, p5Code.getBytes());
    }

    private static void sparaHTMLFil(String dirName, String fileName) throws IOException {

        String pathStr = dirName + File.separator + HTML_FILE_NAME;
        Debug.tracePrio("->sparaHTMLFil pathStr = " + pathStr);
        String html = "<html>\n"
                + "  <head>\n"
                + "    <meta charset=\"UTF-8\">\n"
                + "    <script src=\"https://cdn.jsdelivr.net/npm/p5@1.1.9/lib/p5.js\"></script>\n"
                + "    <script src=\"/G4P5js/public_html/G4P5js.js\"></script>\n"
                + "    <script src=\""
                + fileName
                + "\"></script>\n"
                + "  </head>\n"
                + "  <body style=\"background-color:#99CCFF;\">\n"
                + "  <div id='settings'></div>\n"
                + "  </body>\n"
                + "</html>";
        Path path = Paths.get(pathStr);

        Files.write(path, html.getBytes());
    }
//
//    private static void runInBrowser(String dirName) throws IOException {
//        String filNamn = BASE_URL + dirName + "/" + HTML_FILE_NAME;
//        String filNamnURL = filNamn.replaceAll(" ", "%20");
//        String cmd = BROWSER_PATH + " " + filNamnURL;
//        Debug.trace(cmd);
//        Process p = Runtime.getRuntime().exec(cmd);
//    }

    private static void runInBrowserJDServer(String sketchBookPath) throws IOException {
        JDWebbServer jdws = JDWebbServer.getInstance();
        jdws.setSketchDir(sketchBookPath);
        String urlStr = "http://localhost:" + JDWebbServer.getPort();
        try {
            //        String cmd = BROWSER_PATH + " "+urlStr;
//        Debug.trace(cmd);
//        Process p = Runtime.getRuntime().exec(cmd);
            Desktop.getDesktop().browse(new URI(urlStr));
        } catch (URISyntaxException ex) {
            JOptionPane.showMessageDialog(null, "Somesing went wrong in the URL-syntax: " + ex.getMessage());
        }
    }
}
