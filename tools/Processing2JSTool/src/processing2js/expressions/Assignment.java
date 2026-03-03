/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.expressions;

import processing2js.expressions.Expression;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.tree.DefaultMutableTreeNode;
import processing2js.Debug;
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;
import processing2js.expressions.VariableReference;
import processing2js.statements.Statement;
import processing2js.statements.SyntaxNode;

/**
 *
 * @author dahjon
 */
public class Assignment extends Expression {

    private VariableReference variableToAssign;


    private Expression assignExtr;
    String operator;

    final static String FUNCTION_CALL_END_STRING = "(";
    //final static String BINARY_OPERATOR_END_STRING = "+";

    public final static String[] ASSIGNMENT_OPERATOR_STRINGS = {"=", "+=", "-=", "*=", "/=", "|=", "&="};

//    Assignment(SyntaxNode parent, String variableToAssign, String operator, StringBuilder codeStr) throws SyntaxErrorException {
//        super(parent);
//        this.variableToAssign = variableToAssign;
//        this.operator = operator;
//        SBUtils.trimLeft(codeStr);
//        SBUtils.deleteChartrim(codeStr, operator.length()); //Remove operator
//        assignExtr = Expression.factory(this, codeStr);
//        //Utils.trimDeleteChartrim(codeStr); //Remove ;
//        Debug.trace("Assignment::Assignment variableToAssign = " + variableToAssign + ", assignExtr = " + assignExtr);
//    }
    public static Expression getInstanceIfAssignemnt(StringBuilder codeStr, SyntaxNode parent, VariableReference stm) throws SyntaxErrorException {
        String operator = SBUtils.getStringThatIsEgualToBeginning(codeStr, Assignment.ASSIGNMENT_OPERATOR_STRINGS);
//                    if (codeStr.charAt(0) == '=') {
        if (operator != null) {

            return new Assignment(parent, stm, operator, codeStr);
        } else {
            return stm;
        }
    }

    public Assignment(SyntaxNode parent, Statement variableToAssign, String operator, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        this.variableToAssign = (VariableReference) variableToAssign;
        this.operator = operator;
        SBUtils.trimLeft(codeStr);
        SBUtils.deleteChartrim(codeStr, operator.length()); //Remove operator
        assignExtr = Expression.factory(this, codeStr);
        //Utils.trimDeleteChartrim(codeStr); //Remove ;
        Debug.trace("Assignment::Assignment variableToAssign = " + variableToAssign + ", assignExtr = " + assignExtr);
    }

    public void secondPass() {
        variableToAssign.secondPass();
        assignExtr.secondPass();

    }
//    Assignment(String variableToAssign, StringBuilder codeStr) {
//        this.variableToAssign = variableToAssign;
//        ArrayList<String> endStrings = new ArrayList<>();
//        endStrings.add(FUNCTION_CALL_END_STRING);
//        endStrings.addAll(Arrays.asList(BinaryOperator.BINARY_END_STRINGS));
//        String firstIdentifier = Utils.consumeBefore(codeStr, endStrings);
//        Debug.trace("Assignment::Assignment firstIdentifier = " + firstIdentifier);
//        Debug.trace("Assignment::Assignment codeStr:\n" + codeStr);
////        codeStr = new StringBuilder(codeStr.toString().trim());
//        String endStr = Utils.findFirstString(codeStr, endStrings);
//        Debug.trace("Assignment::Assignment endStr = " + endStr);
//        if (endStr != null) {
//            switch (endStr) {
//                case FUNCTION_CALL_END_STRING:
//                    assignExtr = new FunctionCall(firstIdentifier, codeStr);
//                    break;
//                case "+":
//                case "-":
//                case "*":
//                case "/":
//                    assignExtr = new BinaryOperator(firstIdentifier, codeStr);
//                    break;
//
//                default:
//                    throw new AssertionError();
//            }
//        }
//        Utils.trimLeft(codeStr);
//        codeStr.deleteCharAt(0); // remove ;
//        Utils.trimLeft(codeStr);
//
//    }

    @Override
    public String toString() {
        return "Assignment{" + "variableToAssign='" + variableToAssign + "' operator='" + operator + "', assignExtr=" + assignExtr + '}';
    }
    public VariableReference getVariableToAssign() {
        return variableToAssign;
    }
    @Override
    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Assignment:  operator: " + operator);
        DefaultMutableTreeNode assnode = variableToAssign.getTreeNode();
        node.add(assnode);
        DefaultMutableTreeNode exprnode = assignExtr.getTreeNode();
        node.add(exprnode);
        return node;

    }

    public String getP5jsCode() {
        String ret = getExceptionCode();
        if (ret == null) {
            ret = variableToAssign.getP5jsCode() + operator + assignExtr.getP5jsCode();
        }
        return ret;
    }

    public Expression getAssignExtr() {
        return assignExtr;
    }

    private String getExceptionCode() {
        if (variableToAssign.getName().equals("pixels")) {
            String ret = "set(";
            ArrayList<Expression> indexes = variableToAssign.getIndexes();

            String index = indexes.get(0).getP5jsCode();
            ret += "(" + index + ")%width, ";
            ret += "(" + index + ")/width, ";
            ret += assignExtr.getP5jsCode();
//                ret += ", ";
            ret += ")";
            return ret;

        }
        else if (variableToAssign.getType().equals("PImage") 
                && variableToAssign.hasDotOperator() 
                && variableToAssign.getAfterDotOperator().isVariableReferenceWithName("pixels"))    {
            String ret = variableToAssign.getAltName()+".set(";
            final VariableReference vr = (VariableReference)variableToAssign.getAfterDotOperator();
            ArrayList<Expression> indexes = vr.getIndexes();

            String index = indexes.get(0).getP5jsCode();
            ret += "(" + index + ")%width, ";
            ret += "(" + index + ")/width, ";
            ret += assignExtr.getP5jsCode();
//                ret += ", ";
            ret += ")";
            return ret;

        }

        return null;
    }
}
