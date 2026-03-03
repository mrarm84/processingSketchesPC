/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.expressions;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;
import processing2js.Utils;
import processing2js.statements.SyntaxNode;

/**
 *
 * @author dahjon
 */
public class Cast extends Expression {

    String typeToCastTo;
    Expression expr;

    public static Cast getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        String typeToCastTo = SBUtils.consumeRegEx(codeStr, "\\([a-zA-Z_$][a-zA-Z0-9_$]*\\)");
        if (typeToCastTo == null) {
            //return null;
        } else {
            Expression expr;
            expr = Expression.factory(parent, codeStr);
            if (expr != null) {
                return new Cast(parent, typeToCastTo.substring(1, typeToCastTo.length() - 1).trim(), expr, codeStr);
            } else {
                codeStr.insert(0, typeToCastTo);
            }
        }
        return null;
    }

    @Override
    public String getType() {
        return typeToCastTo;
    }

    public Cast(SyntaxNode parent, String typeToCastTo, Expression expr, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);

        this.typeToCastTo = typeToCastTo;
        this.expr = expr;
        SBUtils.trimLeft(codeStr);
    }
    public void secondPass() {
        expr.secondPass();

    }
    @Override
    public String toString() {
        return "Cast{" + "typeToCastTo=" + typeToCastTo + ", expr=" + expr + ", type:" + getType() + '}';
    }

    @Override
    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Cast: typeToCastTo=" + typeToCastTo);

        DefaultMutableTreeNode exprnode = expr.getTreeNode();
        node.add(exprnode);
        return node;

    }

    public String getP5jsCode() {
        if (Utils.isStringNumericType(typeToCastTo)) {
            return typeToCastTo + "("+ expr.getP5jsCode()+")";
            
        } else {
            return expr.getP5jsCode();
        }
    }

}
