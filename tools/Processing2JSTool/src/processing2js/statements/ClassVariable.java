/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.statements;

import javax.swing.tree.DefaultMutableTreeNode;
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;
import processing2js.Utils;
import processing2js.expressions.Expression;
import processing2js.statements.SyntaxNode;
import processing2js.statements.VariableDefinition;
//int hej;

/**
 *
 * @author dahjon
 */
public class ClassVariable extends VariableDefinition {

    public static VariableDefinition getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        String begstr = getBegStr(codeStr);
        if (begstr == null) {
            return null;
        } else {
            return new ClassVariable(parent, begstr, codeStr);
        }
    }

    public ClassVariable(SyntaxNode parent, String begstr, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent, begstr, codeStr);
    }

//public class ClassVariable extends FuncVar{
//    String type;
//    private String name;
//
//    Expression startValue = null;
//    
//    public static ClassVariable getInstanceIfYou(SyntaxNode parent,StringBuilder codeStr) throws SyntaxErrorException{
//        String begstr  = Utils.consumeRegEx(codeStr, "[a-zA-Z_$][a-zA-Z_$0-9]*\\s[a-zA-Z_$][a-zA-Z_$0-9]*+\\s*[;=]");
//        Debug.trace("ClassVariable begstr = " + begstr + "codeStr: " + codeStr);
//        if(begstr==null){
//            return null;
//        }
//        else {
//            return new ClassVariable(parent, begstr,codeStr);
//        }
//    }
//
//    ClassVariable(SyntaxNode parent, String begstr, StringBuilder codeStr) throws SyntaxErrorException {
//        super(parent);
//        String[] begstrarr=begstr.split(" ",2);
//        type = begstrarr[0].trim();
//        name = begstrarr[1].trim();
//        name=name.substring(0,name.length()-1); // REmove =
//        Debug.trace("ClassVariable type = " + type+", name = " + name);
//        if (begstr.charAt(begstr.length()-1) == '=') {
//            startValue = Expression.factory(this,codeStr);
//        }
//        Utils.trimDeleteChartrim(codeStr); // remove ;
//    }
//
//    @Override
//    public String toString() {
//        return "Class Variable{" + "name='" + name + "', type=" + type + ", startValue=" + startValue + '}';
//    }
//
//    public DefaultMutableTreeNode getTreeNode() {
//        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Class Variable Def: type=" + type + ", name='" + name+"'");
//
//        DefaultMutableTreeNode exprnode = startValue.getTreeNode();
//        node.add(exprnode);
//        return node;
//
//    }
    public String getInitCode(boolean initInConstr) {
        if (startValue != null) {
            String assignCode = "";
            assignCode = " = " + startValue.getP5jsCode();
            return "this." + name + assignCode + ";\n";
        } else if (!initInConstr && Utils.isStringNumericType(type)) {
            return "this." + name + " = 0" + ";\n";

        } else {
            return "";
        }
    }

    public String getP5jsCode() {
        return "";
    }

    public String getName() {
        return name;
    }

}
