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
public class Comment extends Statement {

    Statement ifstm;
    String comment;

    public static Comment getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        if (SBUtils.consumeRegEx(codeStr, "//") != null) {
            return new Comment(parent, codeStr);
        } else {
            return null;
        }
    }

    Comment(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        //int eol  = codeStr.indexOf("\n");
        comment = SBUtils.consumeBefore(codeStr,"\n");
    }
    public void secondPass() {


    }
    @Override
    public String toString() {
        return "Comment{:\n" + comment + '}';
    }

    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Comment:"+comment);
        return node;
    }

    public String getP5jsCode() {
        String ret = "//"+comment;
        return ret;
    }
}
