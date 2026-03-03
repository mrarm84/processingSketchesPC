/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.statements;

import processing2js.expressions.Expression;
import java.util.ArrayList;
import javax.swing.JOptionPane;
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
public class IfStatement extends Statement {

    Expression expr;
    Statement ifstm;
    Statement elsestm = null;

    public static IfStatement getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        if (SBUtils.consumeRegEx(codeStr, "if\\s*\\(") != null) {
            return new IfStatement(parent, codeStr);
        } else {
            return null;
        }
    }

    IfStatement(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        Debug.trace("IfStatement");
        //SBUtils.deleteChartrim(codeStr);
        expr = Expression.factory(this, codeStr);
        if (codeStr.charAt(0) != ')') {
            throw new SyntaxErrorException("IfStatement Här ska det alltid vara ett )    ", codeStr);
        }
        Debug.trace("IfStatement före trimDeleteChartrim ) codeStr = " + codeStr);
        SBUtils.trimDeleteChartrim(codeStr); //remove )
        Debug.trace("IfStatement före Statement.factory codeStr = " + codeStr);

        ifstm = Statement.factory(this, codeStr);
        SBUtils.trimLeft(codeStr);
        char next = codeStr.charAt(0);
        if (next == ';') {
            //JOptionPane.showMessageDialog(null, "nästa tecken är ; codeStr:\n"+codeStr);
            SBUtils.deleteChartrim(codeStr);
        }
        if (SBUtils.isBeginningEqualToString(codeStr, "else")) {

            SBUtils.deleteChartrim(codeStr, 4); // remove else
            elsestm = Statement.factory(this, codeStr);
            SBUtils.trimLeft(codeStr);
        }
    }

    public void secondPass() {
        expr.secondPass();
        ifstm.secondPass();
        if (elsestm != null) {
            elsestm.secondPass();
        }

    }

    @Override
    public String toString() {
        return "IfStatement{" + " stm:\n" + ifstm + '}';
    }

    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("IfStatement");

        DefaultMutableTreeNode exprnode = expr.getTreeNode();
        node.add(exprnode);
        DefaultMutableTreeNode ifstatnode = new DefaultMutableTreeNode("Statement if");
        ifstatnode.add(ifstm.getTreeNode());
        node.add(ifstatnode);
        if (elsestm != null) {
            DefaultMutableTreeNode elsestatnode = new DefaultMutableTreeNode("Statement else");
            elsestatnode.add(elsestm.getTreeNode());
            node.add(elsestatnode);
        }
        return node;

    }

    public String getP5jsCode() {

        String ret = "if(" + expr.getP5jsCode() + ") " + ifstm.getP5jsCode();
        if (elsestm != null) {
        if (!(ifstm instanceof Block)) {
            ret += ";\n";
        }
            
            ret += Global.INDENT + "else " + elsestm.getP5jsCode();
        }

        return ret;
    }
}
