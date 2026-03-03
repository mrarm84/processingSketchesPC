/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.expressions;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;
import processing2js.statements.SyntaxNode;

/**
 *
 * @author dahjon
 */
public class UnaryPrefixOperator extends Expression {

    Expression firstOperand;
    String operator;

    public final static String[] UNARY_PRE_STRINGS = {"--", "++", "!", "+", "-"};

    public static UnaryPrefixOperator getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        String operator = SBUtils.getStringThatIsEgualToBeginning(codeStr, UNARY_PRE_STRINGS);

        
        if (operator == null) {
            return null;
        } else {
            codeStr.delete(0, operator.length());
            return new UnaryPrefixOperator(parent, operator, codeStr);
        }
    }

    public UnaryPrefixOperator(SyntaxNode parent, String operator, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        this.operator = operator;
        SBUtils.trimLeft(codeStr);
        this.firstOperand = Expression.factory(this, codeStr);
        if(this.firstOperand==null){
            throw new SyntaxErrorException("UnaryPrefixOperator: The operand is null. THis should not happen", codeStr);
        }
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
//        return "UnaryPrefixOperator{" + "firstOperand=" + firstOperand + ", operator=" + operator + ", type:" + getType() + '}';
        return "UnaryPrefixOperator{" + "firstOperand=" + firstOperand + ", operator=" + operator + '}';
    }

    @Override
    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("UnaryPrefixOperator: firstOperand=" + firstOperand+", type:" + getType() );

        DefaultMutableTreeNode onode = new DefaultMutableTreeNode("Operator: " + operator);
        node.add(onode);
        DefaultMutableTreeNode op1node = firstOperand.getTreeNode();
        node.add(op1node);

        return node;

    }

    public String getP5jsCode() {
        return operator+ firstOperand.getP5jsCode() ;
    }

}
