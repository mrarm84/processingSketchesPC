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
public class Switch extends Statement {

    Expression expr;
    Statement stm;
   public static Switch getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        if (SBUtils.consumeRegEx(codeStr, "switch\\s*\\(")!=null) {
            return new Switch(parent,  codeStr);
        } else {
            return null;
        }
    }
    Switch(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        Debug.trace("Switch");
        //SBUtils.deleteChartrim(codeStr);
        expr = Expression.factory(this,codeStr);
        Debug.trace("Switch före trim ) codeStr = " + codeStr);
        SBUtils.trimDeleteChartrim(codeStr); //remove )
        Debug.trace("Switch före Statement.factory codeStr = " + codeStr);

        stm = Statement.factory(this,codeStr);
        SBUtils.trimLeft(codeStr);

    }
    public void secondPass() {
        expr.secondPass();
        stm.secondPass();
    }
    @Override
    public String toString() {
        return "Switch{" + " stm:\n" + stm + '}';
    }

    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Switch");

        DefaultMutableTreeNode exprnode = expr.getTreeNode();
        node.add(exprnode);
        DefaultMutableTreeNode statnode = stm.getTreeNode();
        node.add(statnode);
        return node;

    }

    public String getP5jsCode() {

        String ret = "switch(" + expr.getP5jsCode() + ")" + stm.getP5jsCode();

        return ret;
    }
}
