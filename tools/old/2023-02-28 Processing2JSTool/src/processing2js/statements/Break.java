/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.statements;

import javax.swing.tree.DefaultMutableTreeNode;
import processing2js.Debug;
import processing2js.SBUtils;
import processing2js.SyntaxErrorException;

/**
 *
 * @author dahjon
 */
public class Break extends Statement {

    public static Break getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        if (SBUtils.consumeRegEx(codeStr, "break\\s*\\;") != null) {
            Debug.trace("Break getInstanceIfYou codeStr:" + SBUtils.debugStr(codeStr));
            return new Break(parent);
        } else {
            return null;
        }
    }

    public Break(SyntaxNode parent) {
        super(parent);
    }

    public void secondPass() {

    }

    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Break");

        return node;

    }

    @Override
    public String toString() {
        return "Break";
    }

    @Override
    public String getP5jsCode() {
        return "break";
    }

}
