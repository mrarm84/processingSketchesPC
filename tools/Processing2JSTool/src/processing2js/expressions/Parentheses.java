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
public class Parentheses extends Reference {

    Expression expr;

    public static Parentheses getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        boolean yes = SBUtils.isBeginningEqualToString(codeStr, "(");

        if (yes) {
            Debug.trace("Detta är en parentes!! Parentheses" + codeStr);
            SBUtils.deleteChartrim(codeStr);
            Expression expr = Expression.factory(parent, codeStr);
            if (expr != null) {
                SBUtils.trimLeft(codeStr);
                if (codeStr.charAt(0) != ')') {
                    throw new SyntaxErrorException("Parentheses: Slutparentes saknas", codeStr);
                }
                SBUtils.deleteChartrim(codeStr);

                
                return new Parentheses(parent, expr, codeStr);
            } else {
                codeStr.insert(0, '(');
            }
        }
        return null;

    }

    public Parentheses(SyntaxNode parent, Expression expr, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        char nc = codeStr.charAt(0);
        this.expr = expr;
        handleDotOperator(nc, codeStr);
    }
    public void secondPass() {
        expr.secondPass();

    }    
    @Override
    public String getType() {
        return expr.getType();
    }

    @Override
    public String toString() {
        return "Parentheses{" + "expr=" + expr + ", type:" + getType() + '}';
    }



    @Override
    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Parentheses:"+", type:" + getType());
        addAfterDorOperatorNode(node);
        
        DefaultMutableTreeNode exprnode = expr.getTreeNode();
        node.add(exprnode);
        
        return node;

    }

    public String getP5jsCode() {
        return "(" + expr.getP5jsCode() + ")" + getDotOperatorCode();
    }

}
