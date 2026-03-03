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
public class JsCodeLine extends Statement {

    String codeLine;

    public static JsCodeLine getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        if (SBUtils.consumeRegEx(codeStr, "//js ") != null) {
            return new JsCodeLine(parent, codeStr);
        } else {
            return null;
        }
    }

    JsCodeLine(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        //int eol  = codeStr.indexOf("\n");
        codeLine = SBUtils.consumeBefore(codeStr,"\n");
    }
    @Override
    public void secondPass() {
    }

    @Override
    public String toString() {
        return "JsCodeLine{:\n" + codeLine + '}';
    }

    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("JsCodeLine:"+codeLine);
        return node;
    }

    public String getP5jsCode() {
        
        return codeLine+"\n";
    }
}
