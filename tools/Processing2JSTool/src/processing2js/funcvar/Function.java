/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.funcvar;

import processing2js.statements.Parameter;
import processing2js.statements.VariableDefinition;
import processing2js.statements.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import processing2js.Debug;
import processing2js.Global;
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;
import processing2js.Utils;
import processing2js.expressions.Expression;
import processing2js.expressions.FunctionCall;
import processing2js.expressions.Literal;
import processing2js.statements.Block;
import processing2js.statements.SyntaxNode;

/**
 *
 * @author dahjon
 */
public class Function extends MethodsAndFunctions {

    String altName = null;

    public static Function getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
          
        String begstr = getBegStr(codeStr);
        Debug.trace("Function begstr = " + begstr + ", codeStr: " + codeStr);
        if (begstr == null) {
            return null;
        } else {
            return new Function(parent, begstr, codeStr);
        }
    }


    Function(SyntaxNode parent, String begstr, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        getNameAndTypeFromBegStr(begstr);
        if (type.equals("public")) {
            begstr = name;
            getNameAndTypeFromBegStr(begstr);

        }
        name = name.substring(0, name.length() - 1).trim(); // REmove (
        Debug.trace("Function type = " + type + ", name = " + name);
        extractParameters(codeStr);
        Debug.trace("Function före statementloop codeStr = " + codeStr);
        Statement.consumeStatementsUntilBrace(this, codeStr, statements); // Call by value on statements
        SBUtils.trimDeleteChartrim(codeStr); //Remove }
        Global global = getGlobal();
        if (global.functionExists(name) || global.variableWithAltNameExists(name) || Utils.searchArray(VariableDefinition.SYSTEM_GLOBAL, name)) {
            altName = name;
            for (int i = 0; i < parameters.size(); i++) {
                Parameter par = parameters.get(i);
                altName += par.getType();
            }
            if (parameters.size() == 0) {
                altName += "func";
            }
        }
//        moveStatementsToPreload(global);
    }

    Function(SyntaxNode parent){
        super(parent);
    }
    
//    private void moveStatementsToPreload(Global global) {
//        if (name.equals("setup")) {
//            for (int i = 0; i < statements.size(); i++) {
//                Statement stm = statements.get(i);
//                if (stm instanceof Assignment) {
//                    Expression expr = ((Assignment) stm).getAssignExtr();
//                    if (expr instanceof FunctionCall) {
//                        final String fn = ((FunctionCall) expr).getName();
//                        if (fn.equals("loadImage") || fn.equals("loadFont") || fn.equals("new SoundFile")) {
//                            global.addToPreload(stm);
//                            statements.remove(i);
//                            i--;
//
//                        }
//                    }
//                }
//            }
//        }
//    }
    public Statement popLastStatement() {
        Debug.trace("->Function::popLastStatement: statements.size(): " + statements.size());
        final Statement stm = statements.remove(statements.size() - 1);
        Debug.trace("Function::popLastStatement: efter remove statements.size():" + statements.size() + ", stm:" + stm);
        return stm;
    }

    public Statement getLastStatement() {
        //Debug.trace("->Function::getLastStatement: statements.size(): " + statements.size());
        if (statements.isEmpty()) {
            return null;
        } else {
            final Statement stm = statements.get(statements.size() - 1);
            //Debug.trace("Function::getLastStatement: efter remove statements.size():" + statements.size() + ", stm:" + stm);
            return stm;
        }
    }

    public Function(SyntaxNode parent, String name, String type) {
        super(parent, name, type);

    }

    public void addStatement(Statement stm) {
        statements.add(stm);
    }


    public String getAltName() {
        return altName;
    }

    public ArrayList<Parameter> getParameters() {
        return parameters;
    }

    public boolean hasAltName() {
        return altName != null;
    }
//    Function(SyntaxNode parent, String type, String name, StringBuilder codeStr) {
//        super(parent,name, type);
//        Debug.trace("Function type = " + type + ", name = " + name);
//        Utils.deleteChartrim(codeStr);
//        char nextChar = codeStr.charAt(0);
//        Debug.trace("Function nextChar = " + nextChar);
//        if (nextChar != ')') {  //Åtminstone en parameter
//            while (nextChar == ',') {
//                codeStr.deleteCharAt(0);
//                Utils.trimLeft(codeStr);
//                Parameter param = new Parameter(codeStr);
//                parameters.add(param);
//                nextChar = codeStr.charAt(0);
//
//            }
//        }
//        codeStr.deleteCharAt(0); //Remove (
//        Utils.trimLeft(codeStr);
//        codeStr.deleteCharAt(0);//Remove {
//        Utils.trimLeft(codeStr);
//        Debug.trace("Function före statementloop codeStr = " + codeStr);
//        statements = Statement.consumeStatementsUntilBrace(this,codeStr);
//        Utils.trimDeleteChartrim(codeStr); //Remove }
//    }

    @Override
    public String toString() {
        return "Function{" + "type=" + type + ", name=" + name + ", altName=" + altName + "\nparameters:\n" + parameters + ", \nstatements:\n" + statements + '}';
    }

//    public static void main(String[] args) {
//        StringBuilder codeStr = new StringBuilder(""
//                + "() {\n"
//                + "   ellipse(i,i,i,i);\n"
//                + "   i=i+1;\n"
//                + "}");
////            + "() {\n"
////            + "   size(400, 400);\n"
////            + "   \n"
////            + "}\n");
//        Function f = new Function(null,"void", "draw", codeStr);
//        Debug.trace("");
//        Debug.trace(f.toString());
//    }
    @Override
    public ArrayList<VariableDefinition> getNonClassVariables() {
        ArrayList variables = new ArrayList();
        if (statements != null) {
            for (int i = 0; i < statements.size(); i++) {
                Statement stm = statements.get(i);
                if (stm instanceof VariableDefinition) {
                    variables.add(stm);
                }
            }
        }
        return variables;
    }

    public MutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Function Def: type=" + type + " name=" + name + " altName=" + altName);
//        DefaultMutableTreeNode varnode = new DefaultMutableTreeNode("Variables");
//        for (int i = 0; i < variables.size(); i++) {
//            Variable var = variables.get(i);
//            varnode.add(new DefaultMutableTreeNode(var.toString()));
//        }
//        node.add(varnode);
        DefaultMutableTreeNode statnode = new DefaultMutableTreeNode("Statements");
        for (int i = 0; i < statements.size(); i++) {
            Statement stat = statements.get(i);
            if (stat != null) {
                statnode.add(stat.getTreeNode());
            }
        }
        node.add(statnode);
        return node;

    }

    public String getInitiationCode(Global global) {
        String ret = "";
        ArrayList<VariableDefinition> variables = global.getNonClassVariables();
        for (int i = 0; i < variables.size(); i++) {
            VariableDefinition var = variables.get(i);
            Expression init = var.getStartValue();
            if (init != null) {
                if (!(init instanceof Literal)) {
                    Debug.trace("lägger till startValue init = " + init);
                    ret += Global.INDENT + var.getAltName() + " = " + init.getP5jsCode() + ";\n";
                }
            }

        }
        return ret;
    }

    public String getP5jsCode(Global global) {

        String ret = "";

        String n = name;
        if (altName != null) {
            n = altName;
        }
        ret += "function " + n + "(";

        for (int i = 0; i < parameters.size(); i++) {
            Parameter par = parameters.get(i);
            if (i != 0) {
                ret += ", ";
            }
            ret += par.getP5jsCode();

        }

        ret += "){\n";
        if ((name.equals("setup") && !getGlobal().functionExists("preload"))
                || name.equals("preload")) {
            Debug.trace("Detta är preload eller setup stoppar in initieringskod");
            ret += getInitiationCode(global);
        } else if (name.equals("draw")) {
            if (getGlobal().isP3D()) {
                ret += Global.INDENT + "translate(-width/2, -height/2);\n";
            }
        }

        for (int i = 0; i < statements.size(); i++) {

            Statement stm = statements.get(i);
            //          if (stm != null) {
            ret += Global.INDENT + stm.getP5jsCode();
            if (!(stm instanceof Block)) {
                Debug.trace("Är inte block stm = " + stm);
                ret += ";\n";
            } else {
                Debug.trace("Är block stm = " + stm);

            }
//        }
//            else {
//                Debug.trace("Function::getP5JSCode stm är null stm: "+stm + " function name:"+name + " i: "+i + ", statements.size(): "+statements.size());
//            }

        }
        ret += "}\n\n";
        Debug.trace("Lämnar nu Function med namn: "+name);
        return ret;
    }
}
