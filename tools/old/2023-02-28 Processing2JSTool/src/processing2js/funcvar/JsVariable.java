/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.funcvar;

import processing2js.statements.*;
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
public class JsVariable extends FuncVar {

    String codeLine;

    public static JsVariable getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        if (SBUtils.consumeRegEx(codeStr, "//js ") != null) {
            return new JsVariable(parent, codeStr);
        } else {
            return null;
        }
    }

    JsVariable(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        codeLine = SBUtils.consumeBefore(codeStr,"\n");
    }
    @Override
    public void secondPass() {
    }

    @Override
    public String toString() {
        return "JsVariable{:\n" + codeLine + '}';
    }

    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("JsVariable:"+codeLine);
        return node;
    }

    public String getP5jsCode() {
        
        return codeLine+"\n";
    }
}
