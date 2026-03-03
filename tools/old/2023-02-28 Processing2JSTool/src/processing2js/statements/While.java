/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.statements;

import processing2js.expressions.Expression;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import processing2js.Debug;
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;

/**
 *
 * @author dahjon
 */
public class While extends Statement {

    Expression expr;
    Statement stm;
   public static While getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        if (SBUtils.consumeRegEx(codeStr, "while\\s*\\(")!=null) {
            return new While(parent,  codeStr);
        } else {
            return null;
        }
    }
    While(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        Debug.trace("While");
        //SBUtils.deleteChartrim(codeStr);
        expr = Expression.factory(this,codeStr);
        Debug.trace("While före trim ) codeStr = " + codeStr);
        SBUtils.trimDeleteChartrim(codeStr); //remove )
        Debug.trace("While före Statement.factory codeStr = " + codeStr);

        stm = Statement.factory(this,codeStr);
        SBUtils.trimLeft(codeStr);

    }
    public void secondPass() {
        expr.secondPass();
        stm.secondPass();

    }
    @Override
    public String toString() {
        return "While{" + " stm:\n" + stm + '}';
    }

    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("While");

        DefaultMutableTreeNode exprnode = expr.getTreeNode();
        node.add(exprnode);
        DefaultMutableTreeNode statnode = stm.getTreeNode();
        node.add(statnode);
        return node;

    }

    public String getP5jsCode() {

        String ret = "while(" + expr.getP5jsCode() + ")" + stm.getP5jsCode();

        return ret;
    }
}
