/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js;

import java.io.File;
import processing2js.funcvar.FuncVar;
import processing2js.statements.VariableDefinition;
import processing2js.funcvar.Function;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import processing2js.expressions.Expression;
import processing2js.funcvar.BuiltInFunction;
import processing2js.funcvar.ClassNode;
import processing2js.funcvar.JsVariable;
import processing2js.funcvar.MethodsAndFunctions;
import processing2js.statements.BuiltInVariable;
import processing2js.statements.ClassVariable;
import processing2js.statements.Parameter;
import processing2js.statements.Statement;
import processing2js.statements.SyntaxNode;

/**
 *
 * @author dahjon
 */
public class Global extends SyntaxNode {

    ArrayList<VariableDefinition> variables = new ArrayList<>();

    ArrayList<Function> functions = new ArrayList<>();
    ArrayList<ClassNode> classes = new ArrayList<>();
    ArrayList<JsVariable> jsVariables = new ArrayList<>();
    static final public String INDENT = "    ";
    private Global instance = null;
    private StringBuilder extraJSCode = new StringBuilder();
    private File sketchDir;
    private boolean P3D = false;
    private boolean globalReady = false;
    private boolean multiConstructorMode;
    private boolean autoInitArrays = false;

    public boolean isAutoInitArrays() {
        return autoInitArrays;
    }

    public boolean isMultiConstructorMode() {
        return multiConstructorMode;
    }

    public void setMultiConstructorMode(boolean multiConstructorMode) {
        this.multiConstructorMode = multiConstructorMode;
    }

    public boolean isGlobalReady() {
        return globalReady;
    }

    public Global(StringBuilder codeStr, File sketchDir, boolean autoInitArrays) throws SyntaxErrorException {
        super(null);
        Debug.tracePrio("------------------------------");
        Debug.tracePrio("Först i Global");
        Debug.tracePrio("------------------------------");
        this.sketchDir = sketchDir;
        this.autoInitArrays = autoInitArrays;
        Utils.removeNotJsLine(codeStr);
        Utils.removeMultiLineComments(codeStr);
        Utils.removeSingleLineComments(codeStr, "js", "preload");
        //JOptionPane.showMessageDialog(null, "Efter commentsREmove codeStr:\n"+codeStr);
        Utils.removeImport(codeStr);
        SBUtils.replaceAll(codeStr, "JOptionPane.showInputDialog\\(", "JOptionPane_showInputDialog(");
        SBUtils.replaceAll(codeStr, "fullScreen\\s*(\\s*P2D\\s*)\\(", "fullScreen()");
        insertSetupMethodIfNeeded(codeStr);
        BuiltInVariable.addBuiltInVariables(this, variables);
        BuiltInFunction.addBuiltInFunctions(parent, functions);
        while (codeStr.length() > 0) {
            Debug.trace("I loopen i Global konstruktorn codeStr: " + SBUtils.debugStr(codeStr));
            SBUtils.consumeRegEx(codeStr, "(private |public ) *");
            
            SyntaxNode fv = FuncVar.factory(this, codeStr);
            if (fv instanceof VariableDefinition) {
                variables.add((VariableDefinition) fv);
            } else if (fv instanceof Function) {
                functions.add((Function) fv);

            } else if (fv instanceof JsVariable) {
                jsVariables.add((JsVariable) fv);

            } else {
                classes.add((ClassNode) fv);
            }
            Debug.trace("fv:\n " + fv);
            if (codeStr.length() > 0 && codeStr.charAt(0) == ';') {
                SBUtils.deleteChartrim(codeStr);
            }

        }
        Debug.trace("Innan vi kör secondPass:");
        secondPass();
        globalReady = true;
    }

    public void secondPass() {

        for (int i = 0; i < variables.size(); i++) {
            variables.get(i).secondPass();
        }
        for (int i = 0; i < functions.size(); i++) {
            functions.get(i).secondPass();

        }
        for (int i = 0; i < classes.size(); i++) {
            classes.get(i).secondPass();

        }

    }

    public boolean functionExists(String searchStr) {
        boolean found = false;
        for (int i = 0; !found && i < functions.size(); i++) {
            String name = functions.get(i).getName();
            if (name.equals(searchStr)) {
                found = true;
            }
        }
        return found;
    }

    public boolean variableExists(String searchStr) {
        boolean found = false;
        for (int i = 0; !found && i < variables.size(); i++) {
            String name = variables.get(i).getName();
            if (name.equals(searchStr)) {
                found = true;
            }
        }
        return found;
    }

    public boolean variableWithAltNameExists(String searchStr) {
        boolean found = false;
        for (int i = 0; !found && i < variables.size(); i++) {
            String name = variables.get(i).getAltName();
            if (name.equals(searchStr)) {
                found = true;
            }
        }
        return found;
    }
//    public boolean variableExists(String searchStr) {
//        boolean found = false;
//        for (int i = 0; !found && i < variables.size(); i++) {
//            if (variables.get(i) instanceof VariableDefinition) {
//                String name = ((VariableDefinition) variables.get(i)).getName();
//                if (name.equals(searchStr)) {
//                    found = true;
//                }
//            }
//        }
//        return found;
//    }

    public String variableAltName(String searchStr) {
        for (int i = 0; i < variables.size(); i++) {
            VariableDefinition v = variables.get(i);
            if (v.getName().equals(searchStr)) {
                return v.getAltName();
            }
        }
        return searchStr;
    }

    public ArrayList<Function> searchFunction(String searchStr, int nrOfArgs) {
        ArrayList<Function> foundFunctions = new ArrayList(1);
        for (int i = 0; i < functions.size(); i++) {
            Function f = functions.get(i);
            String name = f.getName();
            if (name.equals(searchStr)) {
                if (nrOfArgs == f.getParameters().size()) {
                    foundFunctions.add(f);
                }
            }
        }
        return foundFunctions;
    }

    public Function searchFunction(String searchStr, ArrayList<Expression> parameters) {
        Function retVal = null;
        int curStatus = 0;
        for (int i = 0; i < functions.size(); i++) {
            Function f = functions.get(i);

            String name = f.getName();
            if (name.equals(searchStr)) {
                if (searchStr.equals("size")) {
                    //System.out.println("searchFunction size f = " + f);
                }
                int status = compareParameters(parameters, f);
                if (curStatus < status) {
                    retVal = f;
                    curStatus = status;
                }
            }
        }
        return retVal;
    }

    public static int compareParameters(ArrayList<Expression> parameters, MethodsAndFunctions f) {
        //int status = NAME;
        //final int NUMPAR = 1;
        final int UNKNOWN = 2;
        final int NUMBER = 3;
        final int EQUAL = 4;
        int status = 0;
        //                if (retVal == null) {
//                    retVal = f;
//                }
        if (!f.getParameters().isEmpty() && f.getParameters().get(0).getType().equals("*")) {
            return EQUAL;
        }
        if (parameters.size() == f.getParameters().size()) {
            if (parameters.size() == 0) {
                return EQUAL;
            }
            //status = NUMPAR;
            for (int j = 0; j < parameters.size(); j++) {
                Expression searchPar = parameters.get(j);
                Parameter listPar = f.getParameters().get(j);
                String searchType = searchPar.getType();
                String listType = listPar.getType();
                if (listType.equals(searchType)) {
                    if (j == 0) {
                        status = EQUAL;
                    }
                } else if ((listType.equals("float") || listType.equals("double"))
                        && Utils.isStringNumericType(searchType)) {
                    if (j == 0) {
                        status = NUMBER;
                    } else if (status > NUMBER) {
                        status = NUMBER;
                    }
                } else if (searchType.equals("unknown")) {
                    if (j == 0) {
                        status = UNKNOWN;
                    } else if (status > UNKNOWN) {
                        status = UNKNOWN;
                    }
                } else {
//                            status = NUMPAR;
                    status = 0;
                }
            }

        }
        return status;
    }

    public ClassNode searchClass(String searchStr) {

        for (int i = 0; i < classes.size(); i++) {
            ClassNode c = classes.get(i);
            String name = c.getName();
            if (name.equals(searchStr)) {
                return c;
            }
        }
        return null;
    }

    public void addToPreload(Statement stm) {
        String PRELOAD = "preload";

        final ArrayList<Function> pfs = searchFunction(PRELOAD, 0);
        Function pf;
        if (pfs.isEmpty()) {
            pf = new Function(this, PRELOAD, "void");
            functions.add(pf);

        } else {
            pf = pfs.get(0);
        }
        pf.addStatement(stm);
        //JOptionPane.showMessageDialog(null, pf);
    }

    public StringBuilder getExtraJSCode() {
        return extraJSCode;
    }

    public ArrayList<VariableDefinition> getNonClassVariables() {
        return getGlobalVariables();
    }

    public ArrayList<VariableDefinition> getGlobalVariables() {
        return variables;
    }

    @Override
    public String toString() {
        return "Global{" + "variables=" + variables + ", funtions=" + functions + '}';
    }

    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Global");
        DefaultMutableTreeNode varnode = new DefaultMutableTreeNode("Variables");
        for (int i = 0; i < variables.size(); i++) {
            VariableDefinition var = variables.get(i);
            if (!(var instanceof BuiltInVariable)) {
                varnode.add(var.getTreeNode());
            }
        }
        node.add(varnode);
        DefaultMutableTreeNode funnode = new DefaultMutableTreeNode("Functions");
        for (int i = 0; i < functions.size(); i++) {
            Function fun = functions.get(i);
            if (!(fun instanceof BuiltInFunction)) {
                funnode.add(fun.getTreeNode());
            }
        }
        node.add(funnode);
        DefaultMutableTreeNode classnode = new DefaultMutableTreeNode("Classes");
        for (int i = 0; i < classes.size(); i++) {
            ClassNode c = classes.get(i);
            classnode.add(c.getTreeNode());
        }
        node.add(classnode);
        return node;
    }

    public String getP5jsCode() {

        String ret = "";
        for (int i = 0; i < variables.size(); i++) {
            VariableDefinition var = variables.get(i);
            ret += var.getP5jsCode();
        }
        
        for (int i = 0; i < jsVariables.size(); i++) {
            JsVariable var = jsVariables.get(i);
            ret += var.getP5jsCode();
        }

        for (int i = 0; i < functions.size(); i++) {
            Function fun = functions.get(i);
            ret += fun.getP5jsCode(this);

        }

        for (int i = 0; i < classes.size(); i++) {
            ClassNode c = classes.get(i);
            ret += c.getP5jsCode();

        }

        ret += extraJSCode;
        ret = removeExtraSemikolon(ret);
        return ret;
    }

    private static String removeExtraSemikolon(String jscode) {
        String ret = jscode.replaceAll("}\\s*;", "}");
        return ret;
    }

    private static void insertSetupMethodIfNeeded(StringBuilder procKod) {
        //String patternStr = "function \\s+setup\\s*\\(";
        String patternStr = "void\\s+setup\\s*\\(";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher m = pattern.matcher(procKod);
        if (!m.find()) {
            SBUtils.replaceAll(procKod, "\n", "\n   ");
            procKod.insert(0, "void setup(){\n   ");
            procKod.append("\n}\n");
        }

    }

    public ClassVariable isVariableMemberOfClass(String className, String variable) {
        //System.out.println("->isVariableMemberOfClass classes.size() = " + classes.size());
        for (int i = 0; i < classes.size(); i++) {
            ClassNode c = classes.get(i);
            //System.out.println("i = " + i + " c = " + c + " c.getName() = " + c.getName());
            //System.out.println();
            if (c != null && c.getName() != null && c.getName().equals(className)) {
                ClassVariable cv = c.isVariableMember(variable);
                if (cv != null) {
                    return cv;
                } else {
                    if (c.getExtendsclass() != null) {
                        return isVariableMemberOfClass(c.getExtendsclass(), variable);
                    }
                }
            }

        }
        return null;
    }

    public void placeBehindParentClass(String child, String parent) {
        int childNr = 0;
        int parentNr = 0;
        for (int i = 0; i < classes.size(); i++) {
            ClassNode cn = classes.get(i);
            final String searchName = cn.getName();
            if (searchName.equals(child)) {
                childNr = i;
            } else if (cn.getName().equals(parent)) {
                parentNr = i;
            }
        }
        if (parentNr > childNr) {
            classes.get(parentNr).secondPass();
            classes.add(parentNr + 1, classes.get(childNr));
            classes.remove(childNr);
        }
    }

    public File getSketchDir() {
        return sketchDir;
    }

    public boolean isP3D() {
        return P3D;
    }

    public void setP3D(boolean P3D) {
        this.P3D = P3D;
    }
}
