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
public class Case extends Statement {

    Expression expr;
    Statement ifstm;

    public static Case getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        if (SBUtils.consumeRegEx(codeStr, "case\\s") != null) {
            return new Case(parent, codeStr);
        } else {
            return null;
        }
    }

    Case(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        Debug.trace("Case");
        //SBUtils.deleteChartrim(codeSt//r);
        SBUtils.trimLeft(codeStr);
        Debug.trace("Före Expression.factory codeStr:" + SBUtils.debugStr(codeStr));
        expr = Expression.factory(this, codeStr, true);
        SBUtils.trimLeft(codeStr);
        if (codeStr.charAt(0) != ':') {
            throw new SyntaxErrorException("Case Här ska det alltid vara ett ':'  expr:  "+expr.getP5jsCode(), codeStr);
        }
        Debug.trace("Case före trimDeleteChartrim ) codeStr = " + SBUtils.debugStr(codeStr));
        SBUtils.trimDeleteChartrim(codeStr); //remove :
        Debug.trace("Case före Statement.factory codeStr = " + SBUtils.debugStr(codeStr));

        ifstm = Statement.factory(this, codeStr);
        Debug.trace("Case efter Statement.factory codeStr = " + SBUtils.debugStr(codeStr));
        
        SBUtils.trimLeft(codeStr);
    }
    public void secondPass() {
        expr.secondPass();
        ifstm.secondPass();

    }
    @Override
    public String toString() {
        return "Case{" + " stm:\n" + ifstm + '}';
    }

    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Case");

        DefaultMutableTreeNode exprnode = expr.getTreeNode();
        node.add(exprnode);
        if (ifstm != null) {
            DefaultMutableTreeNode ifstatnode = new DefaultMutableTreeNode("Sats efter case");
            ifstatnode.add(ifstm.getTreeNode());
            node.add(ifstatnode);
        }
        return node;

    }

    public String getP5jsCode() {

        String ret = "case " + expr.getP5jsCode() + ":";
        if (ifstm != null) {
            ret += ifstm.getP5jsCode();
        }

        return ret;
    }
}
