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
import processing2js.Global;
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;

/**
 *
 * @author dahjon
 */
public class Return extends Statement {

    Expression expr;

    public static Return getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        if (SBUtils.consumeRegEx(codeStr, "return\\s") != null) {
            return new Return(parent, codeStr);
        } else {
            return null;
        }
    }

    Return(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        Debug.trace("Return");
        //SBUtils.deleteChartrim(codeSt//r);
        SBUtils.trimLeft(codeStr);
        expr = Expression.factory(this, codeStr);
        SBUtils.trimLeft(codeStr);
//        if (codeStr.charAt(0) != ')' ) {
//            Debug.trace("Tar bort ) före trimDeleteChartrim ) codeStr = " + codeStr);
//            SBUtils.trimDeleteChartrim(codeStr); //remove )
//        }
        Debug.trace("Return före Statement.factory codeStr = " + codeStr);

    }
    public void secondPass() {
        expr.secondPass();

    }
    @Override
    public String toString() {
        return "Return{" + "expr=" + expr + '}';
    }

    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Return");

        if (expr != null) {
            DefaultMutableTreeNode exprnode = expr.getTreeNode();
            node.add(exprnode);
        }
        return node;

    }

    public String getP5jsCode() {

        String ret = "return ";
        if (expr != null) {
            ret += expr.getP5jsCode();
        }

        return ret;
    }
}
