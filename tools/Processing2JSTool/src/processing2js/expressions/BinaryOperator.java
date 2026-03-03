/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.expressions;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import processing2js.Debug;
import processing2js.Global;
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;
import processing2js.funcvar.InsertFunctions;
import processing2js.statements.SyntaxNode;

/**
 *
 * @author dahjon
 */
public class BinaryOperator extends Expression {

    public final static String[] BINARY_END_STRINGS = {"?", ":", "+", "-", "*", "/", "<=", ">=", "<", ">", "==", "!=", "&&", "&", "||", "|", "%"};
    private Expression firstOperand;

    String operator;
    private Expression secondOperand;

    public static String isOperatorNext(StringBuilder codeStr) {
        if(SBUtils.beginsWith(codeStr, "//")){
            return null;
        }
        String retval = SBUtils.getStringThatIsEgualToBeginning(codeStr, BINARY_END_STRINGS);
        return retval;
    }

    public BinaryOperator(SyntaxNode parent, Expression firstOperand, String operator, Expression secondOperand, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        this.firstOperand = firstOperand;
        if (firstOperand == null) {
            Debug.trace("!!!!!!!!!!!!!!!!!!!!!!!\nfirstOperand är null detta ska inte hända     secondOperand = " + secondOperand + "\n!!!!!!!!!!!!!!!!!");
            throw new SyntaxErrorException("BinaryOperator first operand is null secondOperand: " + secondOperand + "operator: " + operator, codeStr);
        }
        this.operator = operator;
        this.secondOperand = secondOperand;
        if (secondOperand == null) {
            Debug.trace("!!!!!!!!!!!!!!!!!!!!!!!\nfirstOperand är null detta ska inte hända     secondOperand = " + secondOperand + "\n!!!!!!!!!!!!!!!!!");
            throw new SyntaxErrorException("BinaryOperator second operand is null secondOperand: " + secondOperand + "operator: " + operator, codeStr);
        }
    }
//        public BinaryOperator(String firstOperand, char operator, String secondOperand) {
//        this.firstOperand = firstOperand;
//        this.operator = operator;
//        this.secondOperand = secondOperand;
//    }

//    BinaryOperator(String firstIdentifier, StringBuilder codeStr) {
//        firstOperand = firstIdentifier;
//        operator = codeStr.charAt(0);
//        codeStr.deleteCharAt(0);
//        secondOperand = Utils.consumeBefore(codeStr, ";");
//    }
//
    public void secondPass() {
        firstOperand.secondPass();
        secondOperand.secondPass();

    }

    /**
     *
     * @param @return higher means higher prioity
     */
    public int getPriority() {
        for (int i = 0; i < BINARY_END_STRINGS.length; i++) {
            String exp = BINARY_END_STRINGS[i];
            if (exp.equals(operator)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String getType() {
        if (firstOperand.getType().equals("String") || secondOperand.getType().equals("String")) {
            return "String";
        } else if (firstOperand.getType().equals("double") || secondOperand.getType().equals("double")) {
            return "double";
        } else if (firstOperand.getType().equals("float") || secondOperand.getType().equals("float")) {
            return "float";
        } else if (firstOperand.getType().equals("long") || secondOperand.getType().equals("long")) {
            return "float";

        }
        return "int";
    }

    @Override
    public String toString() {
        return "BinaryOperator{" + "firstOperand=" + firstOperand + ", operator=" + operator
                + ", secondOperand=" + secondOperand + ", type:" + getType() + '}';
    }

    @Override
    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("BinaryOperator");//: firstOperand=" + firstOperand+", type:" + getType() );

        DefaultMutableTreeNode op1node = firstOperand.getTreeNode();
        node.add(op1node);
        DefaultMutableTreeNode onode = new DefaultMutableTreeNode("Operator: " + operator);
        node.add(onode);
        DefaultMutableTreeNode op2node = secondOperand.getTreeNode();
        node.add(op2node);
        return node;

    }

    public String getExceptionCode() {
        boolean isGet = firstOperand.isFunctionWithName("get") || firstOperand.isFunctionWithName("get");
        final String ot1 = firstOperand.getType();
        final String ot2 = secondOperand.getType();
        isGet = isGet || ot1.equals("color") || ot2.equals("color");

        if (isGet) {

            return firstOperand.getP5jsCode() + ".toString()" + operator + secondOperand.getP5jsCode() + ".toString()";
//            InsertFunctions.insert(global.getExtraJSCode(), InsertFunctions.CompareColors);
//            return "processing2jsCompareGet(" + firstOperand.getP5jsCode() + ", " + secondOperand.getP5jsCode() + ")";
        }
        if (firstOperand.isVariableReferenceWithName("CODED") || secondOperand.isVariableReferenceWithName("CODED")) {
            return "true";
        }
        if (!(operator.equals("==") || operator.equals("!="))
                && (ot1.equals("char") || ot2.equals("char"))
                && !((ot1.equals("String") || ot2.equals("String")) && operator.equals("+"))) {
            String ret = firstOperand.getP5jsCode();
            if (ot1.equals("char")) {
                //System.out.println("firstOperand är char!!");
                ret += ".charCodeAt(0)";
            } else {
                //System.out.println("firstOperand är inte char!!");

            }
            ret += " " + operator + " " + secondOperand.getP5jsCode();
            if (secondOperand.getType().equals("char")) {
                ret += ".charCodeAt(0)";
            }
            return ret;

        }
        return null;
    }

    public String getP5jsCode() {
        Debug.trace("BinaryOperator::getP5jsCode this = " + this);
        String colorException = getExceptionCode();
        if (colorException == null) {
            return firstOperand.getP5jsCode() + " " + operator + " " + secondOperand.getP5jsCode();
        } else {
            return colorException;
        }
    }

    public Expression getFirstOperand() {
        return firstOperand;
    }

    public void setFirstOperand(Expression firstOperand) {
        this.firstOperand = firstOperand;
    }

    public Expression getSecondOperand() {
        return secondOperand;
    }

    public void setSecondOperand(Expression secondOperand) {
        this.secondOperand = secondOperand;
    }
}
