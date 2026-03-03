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
public class Default extends Statement {

    Statement ifstm;

    public static Default getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        if (SBUtils.consumeRegEx(codeStr, "default\\s*:") != null) {
            return new Default(parent, codeStr);
        } else {
            return null;
        }
    }

    Default(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        Debug.trace("Default");
        SBUtils.trimLeft(codeStr);
        ifstm = Statement.factory(this, codeStr);
        SBUtils.trimLeft(codeStr);
    }

    public void secondPass() {
        if (ifstm != null) {
            ifstm.secondPass();
        }
    }

    @Override
    public String toString() {
        return "Default{" + " stm:\n" + ifstm + '}';
    }

    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Default stm:" + ifstm);
        if (ifstm != null) {
            DefaultMutableTreeNode ifstatnode = new DefaultMutableTreeNode("Sats efter default");
            ifstatnode.add(ifstm.getTreeNode());
            node.add(ifstatnode);
        }
        return node;
    }

    public String getP5jsCode() {
        String ret = "default:";
        if (ifstm != null) {
            ret += ifstm.getP5jsCode();
        }
        return ret;
    }
}
