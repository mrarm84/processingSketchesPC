/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
import processing2js.Debug;
import processing2js.SyntaxErrorException;
import processing2js.statements.Statement;
import processing2js.SBUtils;
import processing2js.Utils;
import processing2js.statements.SyntaxNode;
import processing2js.statements.VariableDefinition;

/**
 *
 * @author dahjon
 */
abstract public class Expression extends Statement {

    public final static String[] EXPRESSION_END_STRINGS = {};

    public Expression(SyntaxNode parent) {
        super(parent);
    }

    static public Expression factory(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        return factory(parent, codeStr, false);
    }

    static public Expression factory(SyntaxNode parent, StringBuilder codeStr, boolean fromCase) throws SyntaxErrorException {
        SBUtils.trimLeft(codeStr);
        Expression expr = Cast.getInstanceIfYou(parent, codeStr);
        if (expr != null) {
//            return expr;
        } else if ((expr = Parentheses.getInstanceIfYou(parent, codeStr)) != null) {
        } else if ((expr = UnaryPostfixOperator.getInstanceIfYou(parent, codeStr)) != null) {
        } else if ((expr = UnaryPrefixOperator.getInstanceIfYou(parent, codeStr)) != null) {
//            return expr;
        } else if ((expr = ArrayCreation.getInstanceIfYou(parent, codeStr)) != null) {
//            return expr;
        } else if ((expr = ArrayInitiation.getInstanceIfYou(parent, codeStr)) != null) {
//            return expr;
        } else if ((expr = Literal.getInstanceIfYou(parent, codeStr)) != null) {
        } else if ((expr = NewObject.getInstanceIfYou(parent, codeStr)) != null) {
        } else if ((expr = FunctionCall.getInstanceIfYou(parent, codeStr)) != null) {
//            return expr;
//        } else {
//            char nextChar = codeStr.charAt(0);
//            Debug.trace("Expression::factory nextChar = " + nextChar);
//            if (Character.isDigit(nextChar) || nextChar == '.' || nextChar == '"') {
//                expr = new Literal(parent, codeStr);
        } else {
            expr = VariableReference.getInstanceIfYou(codeStr, parent);

        }
        SBUtils.trimLeft(codeStr);
        //nextChar = codeStr.charAt(0);

//        if(expr==null){
//            throw new SyntaxErrorException("Expr är null i Expression::factory codeStr: ",codeStr);
//        }
//        if(nextChar=='+' ||nextChar=='-' ||nextChar=='*' ||nextChar=='/'||nextChar=='<'||nextChar=='>'){
        if (expr != null && !fromCase) {
            String operator = BinaryOperator.isOperatorNext(codeStr);
            if (operator != null) {
                //char operator = nextChar;
                Debug.trace("Statement::factory operator = " + operator);
                SBUtils.deleteChartrim(codeStr, operator.length());
                Expression secondOperator = Expression.factory(parent, codeStr);//Det här är fel!!!!
                BinaryOperator newBO = new BinaryOperator(parent, expr, operator, secondOperator, codeStr);
                expr = newBO;
                if (secondOperator instanceof BinaryOperator) {
                    BinaryOperator secondOpBo = (BinaryOperator) secondOperator;
//                    JOptionPane.showMessageDialog(null, "secondOpBo.getPriority(operator) > newBO.getPriority(operator): "
//                            + secondOpBo.getPriority() + ">" + newBO.getPriority()
//                            + "\nsecondOpBo:" + secondOpBo + "\newBO: " + newBO);
                    if (secondOpBo.getPriority() > newBO.getPriority()) {

                        newBO.setSecondOperand(secondOpBo.getFirstOperand());
                        secondOpBo.setFirstOperand(newBO);
                        expr = secondOpBo;

                    }
                }
            }
            SBUtils.trimLeft(codeStr);
        }
        //SBUtils.trimLeft(codeStr);
        if (expr instanceof VariableReference) {
            expr = Assignment.getInstanceIfAssignemnt(codeStr, parent, (VariableReference) expr);

        }
        return expr;
    }

    public static Reference getMethodReference(StringBuilder codeStr, SyntaxNode parent) throws SyntaxErrorException {
        char nc;
        Reference cm = null;
        SBUtils.deleteChartrim(codeStr); // ta bort .
        String identifier = SBUtils.consumeIdentifierName(codeStr);
        SBUtils.trimLeft(codeStr);
        nc = codeStr.charAt(0);
        //JOptionPane.showMessageDialog(null, "VariableReference: identifier: " + identifier + " nc: " + nc);
        if (nc == '(') {
            SBUtils.deleteChartrim(codeStr);
            Debug.trace("getMethodReference det verkar som att vi har  ett FucntionCall= " + cm);
            cm = new FunctionCall(parent, identifier, codeStr);
            //Debug.trace("VariableReference namn + " + name1 + " har FucntionCall= " + cm);
        } else {
            //JOptionPane.showMessageDialog(null,name+ " skapar variabel VariableReference: identifier: " + identifier + " nc: " + nc);

            cm = new VariableReference(parent, identifier, codeStr);
        }
        return cm;
    }

    public boolean isVariableReferenceWithName(String searchName){
        if(this instanceof VariableReference){
            VariableReference vr = (VariableReference) this;
            if(vr.getName().equals(searchName)){
                return true;
            }
        }
        return false;
    }
    
    public String getType() {
        return "unknown";
    }

    public abstract String getP5jsCode();
}
