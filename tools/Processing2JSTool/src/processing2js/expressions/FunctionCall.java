/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.expressions;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import processing2js.Debug;
import processing2js.Global;
import processing2js.SyntaxErrorException;
import processing2js.statements.Statement;
import processing2js.SBUtils;
import processing2js.Utils;
import processing2js.funcvar.ClassNode;
import processing2js.funcvar.FuncVar;
import processing2js.funcvar.Function;
import processing2js.funcvar.InsertFunctions;
import processing2js.funcvar.Method;
import processing2js.statements.SyntaxNode;

/**
 *
 * @author dahjon
 */
public class FunctionCall extends Reference {

    private ArrayList<Expression> parameters = new ArrayList();

    String name;
    Reference classMember = null;
    boolean isMethod = false;
    FuncVar funcDef = null;
    String altName = null;

    //String paramStr;
    static String[][] P5JS_FUNCTIONS = {
        {"size", "createCanvas"},
        {"println", "console.log"},
        {"Debug.trace", "print"},
        {"System.out.print", "print"},
        {"mousePressed", "touchStarted"},
        {"mouseDragged", "touchMoved"},
        {"mouseReleased", "touchEnded"},
        {"pushMatrix", "push"},
        {"popMatrix", "pop"},
        {"Integer.parseInt", "Number"},
        {"Double.pareDouble", "Number"},
        {"Integer.valueOf", "Number"},
        {"Double.valueOf", "Number"},
        {"saveFrame", "save"},
        {"JOptionPane_showInputDialog", "prompt"},
        {"printArray", "console.log"},
        {"requestImage", "loadImage"},
        {"lightSpecular", "specularColor"},
        {"specular", "specularMaterial"},
        {"heading2D", "heading"}, //  {"new PVector", "createVector"}
    };

    public static String getp5jsName(String orgName) {
        String ret = Utils.match2dArray(P5JS_FUNCTIONS, orgName);
        return ret;
    }

    public String getName() {
        return name;
    }

    public String getAltName() {
        if (altName != null) {
            return altName;
        }
        return name;

    }

    public static FunctionCall getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        String firstIdentifier = SBUtils.consumeRegEx(codeStr, "[a-zA-Z_$][a-zA-Z_$0-9]*\\s*\\(");
        //Debug.trace("FunctionCall firstIdentifier = " + firstIdentifier + "codeStr: " + codeStr);
        if (firstIdentifier == null) {
            return null;
        } else {

            Debug.trace("FunctionCal::getInstanceIfYou:: firstIdentifier = '" + firstIdentifier + "'");
            firstIdentifier = firstIdentifier.substring(0, firstIdentifier.length() - 1).trim();
            Debug.trace("FunctionCal::getInstanceIfYou:: firstIdentifier = '" + firstIdentifier + "'");
            SBUtils.trimLeft(codeStr);
            return new FunctionCall(parent, firstIdentifier, codeStr);
        }
    }

    //String identifierPattern = "[_a-zA-Z][_a-zA-Z0-9];";
    public FunctionCall(SyntaxNode parent, String firstIdentifier, StringBuilder codeStr) throws SyntaxErrorException {
        this(parent, firstIdentifier, codeStr, false);
    }

    public FunctionCall(SyntaxNode parent, String firstIdentifier, StringBuilder codeStr, boolean rowStatement) throws SyntaxErrorException {
        super(parent);
        Debug.trace("FunctionCall parent = " + parent);
//        name = FuncVar.consume(codeStr, "(");
        name = firstIdentifier.trim();
        Debug.trace("FuntionCall name = " + name);
        Debug.trace("FuntionCall codeStr:\n" + codeStr);
        //codeStr.deleteCharAt(0); // remove (
//        paramStr = Utils.consumeBefore(codeStr, ")");
//        Debug.trace("\nparamStr = " + paramStr);
//        Debug.trace("FuntionCall codeStr:\n " + codeStr);
//        Utils.consumeBefore(codeStr, ";");
//        codeStr.deleteCharAt(0);
//        Utils.trimLeft(codeStr);
//        Debug.trace("FuntionCall på slutet codeStr:\n " + codeStr);
        SBUtils.trimLeft(codeStr);
        char nextChar = codeStr.charAt(0);
        Debug.trace("FunctionCall nextChar = " + nextChar);
        boolean stop = false;
        while (nextChar != ')' && !stop) {  //Åtminstone en parameter
            //codeStr.deleteCharAt(0);
            if (nextChar == ',') {
                SBUtils.deleteChartrim(codeStr);
            }
            SBUtils.trimLeft(codeStr);
            Debug.trace("FuntionCall i loop codeStr:\n" + codeStr);
            Expression param = Expression.factory(this, codeStr);
            if (param == null) {
                stop = true;
                //throw new SyntaxErrorException("FunctionCall: Parameter är null", codeStr);
            } else {
                parameters.add(param);
                nextChar = codeStr.charAt(0);
                Debug.trace("FunctionCall i loop nextChar = " + nextChar);
            }

        }
        SBUtils.trimDeleteChartrim(codeStr); //remove )
        char nc = codeStr.charAt(0);

        handleDotOperator(nc, codeStr);
        nc = codeStr.charAt(0);

        if (nc == ';') {
            Debug.trace("FunctionCall syftet är att ta bort ; codeStr.charAt(0) = " + codeStr.charAt(0));
            SBUtils.deleteChartrim(codeStr); //remove ;
        }
        if (name.equals("size")) {
            if (parent instanceof Function) {
                Function funcParent = (Function) parent;
                if (funcParent.getName().equals("setup")) {
                    if (parameters.size() >= 3) {
                        if (parameters.get(2) instanceof VariableReference) {
                            VariableReference rendVar = (VariableReference) parameters.get(2);
                            //final String arg3 = parameters.get(2).getP5jsCode();

                            //JOptionPane.showMessageDialog(null, "P3D är arg3: "+arg3);
                            if (rendVar.getName().equals("P3D")) {
                                getGlobal().setP3D(true);
                                //JOptionPane.showMessageDialog(null, "P3D");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Tredje parametern i size är inte av typen VariableReference!!!");
                        }
                    }
                }
            }

        }
    }

    public FunctionCall(SyntaxNode parent, String name) {
        super(parent);
        this.name = name;
    }

    public void secondPass() {
        determeneIfIsMethodAndFindFuncDef();

        for (int i = 0; i < parameters.size(); i++) {
            parameters.get(i).secondPass();
        }

    }

    public ArrayList<Expression> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        String parStr = "";
        for (int i = 0; i < parameters.size(); i++) {
            Expression par = parameters.get(i);
            parStr += " " + par.getType();

        }
        return "FunctionCall{" + "name='" + name + "', parameters:\n'" + parStr + "'}";
//        return "FunctionCall{" + "name='" + name + "', parameters:\n'" + parameters + "'}";
    }

    @Override
    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Function Call: name=" + name + ", type = " + getType() + ", altName = " + altName);
        addAfterDorOperatorNode(node);
        DefaultMutableTreeNode statnode = new DefaultMutableTreeNode("Parameters");
        for (int i = 0; i < parameters.size(); i++) {
            Statement param = parameters.get(i);
            statnode.add(param.getTreeNode());
        }
        node.add(statnode);
        return node;

    }

    public boolean isMethod() {

        return isMethod;
    }

    public void determeneIfIsMethodAndFindFuncDef() {
        Debug.trace("->FunctionCall parent: " + getParent());
        boolean foundClassParent = false;
        SyntaxNode paren = getParent();
        while (!foundClassParent && paren != null) {
            if (paren instanceof ClassNode) {
                foundClassParent = true;
            } else {
                paren = paren.getParent();
                //Debug.trace("FunctionCall inte hittad paren = " + paren);
            }
        }

        if (foundClassParent) {
            Debug.trace("FunctionCall determeneIfIsMethod Förälder är klass");
            ClassNode par = (ClassNode) paren;
            ArrayList<Method> methods = par.getMethods();
            for (int i = 0; i < methods.size(); i++) {
                Method met = methods.get(i);
                Debug.trace("met.getName() = " + met.getName() + ", name: " + name);
                if (met.getName().equals(name)) {
                    isMethod = true;
                    funcDef = met;

                }

            }
        }
        if (funcDef == null) {
            final Function func = getGlobal().searchFunction(name, parameters);
            funcDef = func;
            if (func != null) {
                altName = func.getAltName();
            }
//            else {
//                altName = getp5jsName(name);
//            }

        }
        //return false;
    }

    @Override
    public String getType() {
        if (funcDef != null) {
            return funcDef.getType();
        } else {
            return "unknown";
        }
    }

    public void insertFunctions() {
        if (name.equals("delay") && parameters.size() == 1 && parameters.get(0).getType().equals("int")) {

            InsertFunctions.insert(getGlobal().getExtraJSCode(), InsertFunctions.delay);

        } else if (name.equals("fullScreen") && parameters.size() == 0) {

            InsertFunctions.insert(getGlobal().getExtraJSCode(), InsertFunctions.fullScreen);

        }

    }
//
//    public String findCurrentName() {
//        Global global = getGlobal();
//        String curName = name;
//        ArrayList<Function> foundFunctions = global.searchFunction(name, parameters.size());
////        if(foundFunctions.isEmpty()){
////            JOptionPane.showMessageDialog(null, "findCurrentName name:"+name + ", parameters.size()"+ parameters.size());
////        }
//        if (foundFunctions.size() > 0) {
//            Function curFunct = foundFunctions.get(0);
//            curName = curFunct.getAltName();
//            if (curName == null) {
//                curName = name;
//            }
//        }
//        return curName;
//    }

    private String getExeceptionCode() {
        String ret = "";
        if (getParent() instanceof VariableReference) {
            VariableReference var = (VariableReference) getParent();
            //System.out.println("getExeceptionCode var = " + var);
            if (var.getType().length() >= 9 && var.getType().substring(0, 9).equals("ArrayList")) {
                if (name.equals("add")) {
                    if (parameters.size() == 1) {
                        ret = "push(" + parameters.get(0).getP5jsCode() + ")";
                    } else if (parameters.size() == 2) {
                        ret = "splice(" + parameters.get(0).getP5jsCode() + ",0," + parameters.get(1).getP5jsCode() + ")";
                    }
                } else if (name.equals("get")) {
                    ret = "[" + parameters.get(0).getP5jsCode() + "]";
                } else if (name.equals("size")) {
                    ret = "length";
                } else if (name.equals("remove")) {
                    final Expression par = parameters.get(0);
                    if (Utils.isStringNumericType(par.getType())) {
                        ret = "splice(" + par.getP5jsCode() + ",1)[0]";
                    } else {
                        ret = "splice(" + var.getAltName() + ".findIndex(p5jssi => p5jssi===" + par.getP5jsCode() + ")" + ",1)[0]";

                    }

                } else if (name.equals("removeRange")) {
                    String p1code = parameters.get(0).getP5jsCode();
                    ret = "splice(" + p1code + "," + parameters.get(1).getP5jsCode() + "-" + p1code + ")";
                } else if (name.equals("contains")) {
                    ret = "includes(" + parameters.get(0).getP5jsCode() + ")";
                } else if (name.equals("isEmpty")) {
                    ret = "length == 0";
                } else if (name.equals("clone") || name.equals("toArray")) {
                    ret = "slice()";
                } else if (name.equals("addAll")) {
                    ret = "concat(" + parameters.get(0).getP5jsCode() + ")";
                } else if (name.equals("clear")) {
                    ret = "length = 0";
                }
            } else if (var.getType().equals("String")) {
                if (name.equals("length")) {
                    ret = "length";
                }
            } else if (var.getType().equals("PVector")) {
                if (name.equals("heading2D")) {
                    ret = "heading";
                }

//            } else if (name.equals("beginDraw") || name.equals("endDraw") && parameters.size() == 0) {
//                ret = "// beginDraw() and endDraw() is not supportet in p5.js, and or often not needed";
            }
        } else if (name.equals("hint") && parameters.size() == 1) {
            ret = "// hint is not supportet in p5.js";
        } else if (name.equals("sphereDetail") && parameters.size() == 1) {
            ret = "// sphereDetail() is not supportet in p5.js, you could instead supply extra parameters to sphere()";
//        } else if (name.equals("loadShape") && parameters.size() == 1) {
//            if((parameters.get(0) instanceof Literal) && ((Literal) parameters.get(0)).getValue().endsWith("obj\"")){
//                ret = "loadModel";
//                
//            }
//            else {
//                ret = "loadImage";
//            }
//            ret += "("+parameters.get(0).getP5jsCode()+")";
        } else if (name.equals("createFont") && parameters.size() >= 1 && parameters.size() <= 4 && parameters.get(0).getType().equals("String")) {
            final Expression par = parameters.get(0);
            String parStr =  par.getP5jsCode() ;
            if (par instanceof Literal) {
                parStr = Utils.fixDataPath(getGlobal().getSketchDir(), parStr);
            }

            ret = "loadFont(" +parStr+ ")";
            if (parameters.size() >= 2) {
                ret += ";\n" + Global.INDENT + "textSize(" + parameters.get(1).getP5jsCode() + ")";
            }
        } else if ((name.equals("float") || name.equals("int")) && parameters.size() == 1 && parameters.get(0).getType().equals("char")) {
            ret = parameters.get(0).getP5jsCode() + ".charCodeAt(0)";
        } else if (name.equals("createImage")
                && parameters.size() == 3
                && Utils.isStringNumericType(parameters.get(0).getType())
                && Utils.isStringNumericType(parameters.get(1).getType())
                && parameters.get(2).getType().equals("int")) {
            ret = "createImage(" + parameters.get(0).getP5jsCode() + ", " + parameters.get(0).getP5jsCode() + ")";

        } else if (name.equals("get") && parameters.size() == 2) {
            ret = "color(get(" + parameters.get(0).getP5jsCode() + "," + parameters.get(1).getP5jsCode() + "))";
        }
//            else if (name.equals("translate") && parameters.size() >= 2) {
//            ret = "translate( (" + parameters.get(0).getP5jsCode() + ") -width/2,(" + parameters.get(1).getP5jsCode() + ")-height/2)";
//        }
        return ret;
    }

    public String getExtraCommand() {
        String ret = "";

        if (name.equals("size") && getGlobal().isP3D()) {
            if (parent instanceof Function) {
                Function funcParent = (Function) parent;
                if (funcParent.getName().equals("setup")) {
                    ret = ";\n" + Global.INDENT + "translate(-width/2, -height/2)";

                }
            }
        }

        return ret;
    }

    public String getP5jsCode() {
        insertFunctions();
        String ret = getExeceptionCode();
        if (ret.isEmpty()) {
            //String curName = name;
            if (isMethod()) {
                if (!(parent instanceof VariableReference)) {

                    ret += "this.";
                }
            }
            ret += getAltName() + "(";

//            ret += getp5jsName(curName) + "(";
            for (int i = 0; i < parameters.size(); i++) {
                Expression par = parameters.get(i);
                if (i != 0) {
                    ret += ", ";
                }
                String parStr = par.getP5jsCode();
                if (i == 0 && name.equals("loadImage") || name.equals("requestImage") || name.equals("loadFont")) {
                    if (par instanceof Literal) {
                        parStr = Utils.fixDataPath(getGlobal().getSketchDir(), parStr);
                    }
                }
                ret += parStr;

            }
            ret += ")";
        }

        String dotOpCode = getDotOperatorCode();
        ret += dotOpCode;
        ret += getExtraCommand();
        Debug.trace("FunctionCall::getP5jsCode ret = " + ret);
        return ret;
    }

}
