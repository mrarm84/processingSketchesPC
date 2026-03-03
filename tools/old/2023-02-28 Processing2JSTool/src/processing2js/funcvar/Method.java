/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.funcvar;

import processing2js.statements.Parameter;
import processing2js.statements.Statement;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import processing2js.Debug;
import processing2js.Global;
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;
import processing2js.expressions.Expression;
import processing2js.statements.SyntaxNode;
import processing2js.statements.VariableDefinition;

/**
 *
 * @author dahjon
 */
public class Method extends MethodsAndFunctions {

    boolean isAbstract;

    public static Method getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        boolean isAbstract = SBUtils.isBeginningEqualToStringThenRemoveAndTrim(codeStr, "abstract");
        String begstr = getBegStr(codeStr);
        Debug.traceLim("Method begstr = " + begstr + "codeStr: " + codeStr);
        if (begstr == null) {
            return null;
        } else {
            return new Method(parent, begstr, codeStr, isAbstract);
        }
    }


    Method(SyntaxNode parent, String begstr, StringBuilder codeStr, boolean isAbstract) throws SyntaxErrorException {
        super(parent);
        this.isAbstract = isAbstract;
        getNameAndTypeFromBegStr(begstr);
        name = name.substring(0, name.length() - 1).trim(); // Remove ( and space in the end of method-name
        Debug.trace("Method type = " + type + ", name = " + name);
        extractParameters(codeStr);
        if (!isAbstract) {
            Debug.traceLim("Method före statementloop codeStr = " + codeStr);
            Statement.consumeStatementsUntilBrace(this, codeStr, statements);
            SBUtils.trimDeleteChartrim(codeStr); //Remove }
        }
    }


//    public void getNameAndTypeFromBegStr(String begstr) {
//        String[] begstrarr = begstr.split(" ", 2);
//        type = begstrarr[0].trim();
//        name = begstrarr[1].trim();
//    }



    public ArrayList<Parameter> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "Method{" + "type=" + type + ", name=" + name + "\nparameters:\n" + parameters + ", \nstatements:\n" + statements + '}';
    }

    public MutableTreeNode getTreeNode() {
        String abstractStr = "";
        if (isAbstract) {
            abstractStr = ", Abstract Method ";
        }
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Method Def: type=" + type + ", name=" + name + abstractStr);

        DefaultMutableTreeNode statnode = new DefaultMutableTreeNode("Statements");
        for (int i = 0; i < statements.size(); i++) {
            Statement stat = statements.get(i);
            statnode.add(stat.getTreeNode());
        }
        node.add(statnode);
        return node;

    }

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

    public String getP5jsCode() {

        String ret = "";
        if (!isAbstract) {
            ret += Global.INDENT + name + "(";

            for (int i = 0; i < parameters.size(); i++) {
                VariableDefinition par = parameters.get(i);
                if (i != 0) {
                    ret += ", ";
                }
                ret += par.getP5jsCode();

            }

            ret += "){\n";

            for (int i = 0; i < statements.size(); i++) {

                Statement stm = statements.get(i);
                ret += Global.INDENT + Global.INDENT + stm.getP5jsCode() + ";\n";

            }
            ret += Global.INDENT + "}\n\n";
        }
        return ret;
    }
}
