/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.expressions;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import processing2js.Debug;
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;
import processing2js.statements.SyntaxNode;

/**
 *
 * @author dahjon
 */
public class UnaryPostfixOperator extends Expression {
//a++

    VariableReference firstOperand;
    String operator;

    public static UnaryPostfixOperator getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        String exprStr = SBUtils.consumeRegEx(codeStr, "[a-zA-Z_$][a-zA-Z_$0-9]*\\s*(\\[[a-zA-Z_$][a-zA-Z_$0-9]*\\])*((\\+\\+)|(\\-\\-))");
        if (exprStr == null) {
            return null;
        } else {
            if (exprStr.length() < 3) {
                throw new SyntaxErrorException("UnaryPostfixOperator::getInstanceIfYou ExprStr för kort: exprStr:'" + exprStr + "' ", codeStr);
            }

            exprStr = exprStr.trim();
            SBUtils.trimLeft(codeStr);
            return new UnaryPostfixOperator(parent, exprStr, codeStr);
        }
    }

    public UnaryPostfixOperator(SyntaxNode parent, String exprStr, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        if (exprStr.length() < 3) {
            throw new SyntaxErrorException("UnaryPostfixOperator: ExprStr för kort: exprStr:'" + exprStr + "' ", codeStr);
        }
        this.operator = exprStr.substring(exprStr.length() - 2, exprStr.length());
        String opStr = exprStr.substring(0, exprStr.length() - 2);
        Debug.trace("UnaryPostfixOperator opStr = " + opStr);
        this.firstOperand = new VariableReference(this, opStr, codeStr);
//        this.firstOperand = new VariableReference(this,exprStr, codeStr);
//        this.operator = SBUtils.consume(codeStr, 2);
    }
    public void secondPass() {
        firstOperand.secondPass();

    }
    @Override
    public String getType() {
        return firstOperand.getType();
    }

    @Override
    public String toString() {
        return "UnaryPostfixOperator{" + "firstOperand=" + firstOperand + ", operator=" + operator + '}';
    }

    @Override
    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("UnaryPostfixOperator: firstOperand=" + firstOperand + ", type:" + getType());

        DefaultMutableTreeNode op1node = firstOperand.getTreeNode();
        node.add(op1node);
        DefaultMutableTreeNode onode = new DefaultMutableTreeNode("Operator: " + operator);
        node.add(onode);
        return node;

    }

    public String getP5jsCode() {
        return firstOperand.getP5jsCode() + operator;
    }

}
