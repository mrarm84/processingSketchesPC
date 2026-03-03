/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.funcvar;

import processing2js.statements.Parameter;
import processing2js.statements.ClassVariable;
import processing2js.statements.Statement;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import processing2js.Debug;
import processing2js.Global;
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;
import processing2js.expressions.Expression;
import processing2js.expressions.FunctionCall;
import processing2js.statements.SyntaxNode;
import processing2js.statements.VariableDefinition;

/**
 *
 * @author dahjon
 */
public class Constructor extends Function {

//    ArrayList<VariableDefinition> parameters = new ArrayList<>();
//    ArrayList<Statement> statements = new ArrayList<>();
    FunctionCall superCall = null;

    public static Constructor getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        String begstr = SBUtils.consumeRegEx(codeStr, "[a-zA-Z_$][a-zA-Z_$0-9]*+\\s*\\(");
        Debug.traceLim("Method begstr = " + begstr + "codeStr: " + codeStr);
        if (begstr == null) {
            return null;
        } else {
            return new Constructor(parent, begstr, codeStr);
        }
    }

    public Constructor(SyntaxNode parent) {
        super(parent);
        name = "";
        statements = new ArrayList<>();
    }

    Constructor(SyntaxNode parent, String begstr, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        name = begstr.substring(0, begstr.length() - 1).trim();// REmove (

        Debug.trace("Constructor name = " + name);
        SBUtils.trimLeft(codeStr);
        char nextChar = codeStr.charAt(0);
        Debug.trace("Method nextChar = " + nextChar);
        if (nextChar != ')') {  //Åtminstone en parameter
            nextChar = ',';
            while (nextChar == ',') {
                if (codeStr.charAt(0) == ',') {
                    codeStr.deleteCharAt(0);
                }
                SBUtils.trimLeft(codeStr);
                Parameter param = new Parameter(this, codeStr);
                parameters.add(param);
                nextChar = codeStr.charAt(0);

            }
        }
        codeStr.deleteCharAt(0); //Remove (
        SBUtils.trimLeft(codeStr);
        codeStr.deleteCharAt(0);//Remove {
        SBUtils.trimLeft(codeStr);
        Debug.trace("Method före statementloop codeStr = " + codeStr);
        Statement.consumeStatementsUntilBrace(this, codeStr, statements);
        SBUtils.trimDeleteChartrim(codeStr); //Remove }

        ClassNode cn = (ClassNode) parent;
        if (cn.isSubClass()) {
            Statement firststm = statements.get(0);
            if (firststm instanceof FunctionCall && ((FunctionCall) firststm).getName().equals("super")) {
                superCall = (FunctionCall) firststm;
                statements.remove(0);
            }
        }
    }

    public void secondPass() {
        for (int i = 0; i < parameters.size(); i++) {
            parameters.get(i).secondPass();
        }
        for (int i = 0; i < statements.size(); i++) {
            statements.get(i).secondPass();
        }

    }

    @Override
    public String toString() {
        return "Constructor{" + "name=" + name + "\nparameters:\n" + parameters + ", \nstatements:\n" + statements + '}';
    }

//    public static void main(String[] args) {
//        StringBuilder codeStr = new StringBuilder(""
//                + "void draw(int i, int k) {\n"
//                + "   ellipse(i,i,i,i);\n"
//                + "   i=i+1;\n"
//                + "}");
////            + "() {\n"
////            + "   size(400, 400);\n"
////            + "   \n"
////            + "}\n");
//        Constructor f = getInstanceIfYou(null, codeStr);
//        Debug.trace("");
//        Debug.trace(f.toString());
//    }
    public String getNewInstanceFunctionName() {
        String ifName = "newInstance";
        return FunctionHeaderParameterHeader(ifName);
    }

    public String getInitFunctionName() {
        String ifName = "initInstance";
        return FunctionHeaderParameterHeader(ifName);
    }

    public String FunctionHeaderParameterHeader(String name) {
        String ifName = name;
        for (int i = 0; i < parameters.size(); i++) {
            String par = parameters.get(i).getType();
            ifName += par;
        }
        return ifName;
    }

    public MutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Constructor Def: name=" + name + ", NewInstance: " + getNewInstanceFunctionName());

        DefaultMutableTreeNode statnode = new DefaultMutableTreeNode("Statements");
        for (int i = 0; i < statements.size(); i++) {
            Statement stat = statements.get(i);
            statnode.add(stat.getTreeNode());
        }
        node.add(statnode);
        return node;

    }

    static boolean initInConstructor(String varName, Constructor son) {
        if (son != null) {
            for (int i = 0; i < son.statements.size(); i++) {
                Statement stat = son.statements.get(i);
                if (stat.isAssignmentOf(varName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getInitiationCode(ClassNode parent, Constructor son) {
        String ret = "";
        ArrayList<ClassVariable> variables = parent.getOnlySpecificClassVariables();
        for (int i = 0; i < variables.size(); i++) {
            ClassVariable var = variables.get(i);
            String initCode = var.getInitCode(initInConstructor(var.getName(), son));
            if (initCode.length() > 0) {
                ret += Global.INDENT + Global.INDENT + initCode;
            }
        }
        return ret;
    }

    public String getP5jsCode() {
        final boolean multiMode = getGlobal().isMultiConstructorMode();
        String ret;
        if (multiMode) {
            ret = getP5jsCodeMulti();
        } else {
            ret = getP5jsCodeSingle();
        }
        return ret;
    }

    public String getP5jsCodeMulti() {
        String ret = Global.INDENT + "static " + getNewInstanceFunctionName() + "(";
        System.out.println("0Constructor::getP5jsCodeMulti ret = " + ret);
        ret += getParameterCode();

        System.out.println("1Constructor::getP5jsCodeMulti ret = " + ret);

        ClassNode cn = (ClassNode) parent;
        ret += Global.INDENT + Global.INDENT + "let o = new " + cn.getName() + "();\n";
        System.out.println("2Constructor::getP5jsCodeMulti ret = " + ret);
        ret += Global.INDENT + Global.INDENT + "o." + getInitFunctionName() + "(" + getParameterCode(true);
        ret += Global.INDENT + Global.INDENT + "return o;\n";
        System.out.println("3Constructor::getP5jsCodeMulti ret = " + ret);
        ret += Global.INDENT + "}\n";
        System.out.println("4Constructor::getP5jsCodeMulti ret = " + ret);

        ret += Global.INDENT + getInitFunctionName() + "(";
        System.out.println("5Constructor::getP5jsCodeMulti ret = " + ret);
        ret += getParameterCode();
        if (superCall != null) {
            String superName = "initInstance";
            for (int i = 0; i < superCall.getParameters().size(); i++) {
                Expression par = superCall.getParameters().get(i);
                superName += par.getType();

            }
            ret += Global.INDENT + Global.INDENT + "super." + superName + "(";
            for (int i = 0; i < superCall.getParameters().size(); i++) {
                Expression par = superCall.getParameters().get(i);
                if (i != 0) {
                    ret += ", ";
                }
                String parStr = par.getP5jsCode();
                ret += parStr;
                System.out.println("6Constructor::getP5jsCodeMulti ret = " + ret + ", parStr = " + parStr);

            }
            System.out.println("7Constructor::getP5jsCodeMulti ret = " + ret);
            ret += ");\n";

        }
        System.out.println("8Constructor::getP5jsCodeMulti ret = " + ret);

        ret += getStatementCode();
        System.out.println("9Constructor::getP5jsCodeMulti ret = " + ret);

        return ret;
    }

    public String getP5jsCodeSingle() {

        String ret = "";

        ret += Global.INDENT + "constructor(";

        ret += getParameterCode();

        ClassNode cn = (ClassNode) parent;
        if (superCall == null && cn.isSubClass()) {
            // This has to be created herre to get MultiConstructor to work
            superCall = new FunctionCall(this, "super");
        }
        if (superCall != null) {
            ret += Global.INDENT + Global.INDENT + superCall.getP5jsCode() + ";\n";
        }

        ret += getInitiationCode((ClassNode) getParent(), this);
        ret += getStatementCode();
        return ret;
    }

    public String getParameterCode() {
        return getParameterCode(false);

    }

    public String getParameterCode(boolean call) {
        String ret = "";
        for (int i = 0; i < parameters.size(); i++) {
            VariableDefinition par = parameters.get(i);
            if (i != 0) {
                ret += ", ";
            }
            ret += par.getP5jsCode();

        }
        if (call) {
            ret += ");\n";
        } else {
            ret += "){\n";
        }
        return ret;
    }

    public String getStatementCode() {
        String ret = "";
        for (int i = 0; i < statements.size(); i++) {

            Statement stm = statements.get(i);
            ret += Global.INDENT + Global.INDENT + stm.getP5jsCode() + ";\n";

        }
        ret += Global.INDENT + "}\n\n";
        return ret;
    }
}
