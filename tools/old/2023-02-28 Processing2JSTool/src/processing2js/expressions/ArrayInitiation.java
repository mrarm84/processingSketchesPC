/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.expressions;

import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import processing2js.Debug;
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;
import processing2js.statements.Statement;
import processing2js.statements.SyntaxNode;

/**
 *
 * @author dahjon
 */
public class ArrayInitiation extends Expression{
        ArrayList<Expression> expressions = new ArrayList<>();
    
    public static ArrayInitiation getInstanceIfYou(SyntaxNode parent,StringBuilder codeStr) throws SyntaxErrorException{
        char nc = codeStr.charAt(0);
        if(nc=='{'){
            return new ArrayInitiation(parent, codeStr);
        }
        else {
            return null;
        }
    }

    public ArrayInitiation(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        SBUtils.deleteChartrim(codeStr);
        char nextChar = codeStr.charAt(0);
        while (nextChar != '}') {
            SBUtils.trimLeft(codeStr);
            Expression expr = Expression.factory(parent, codeStr);
            Debug.trace("ArrayInitiation expr:\n " + expr);
            expressions.add(expr);
            SBUtils.trimLeft(codeStr);
            Debug.trace("ArrayInitiation innan deleteChartrim borde vara ,  codeStr.charAt(0) = " + codeStr.charAt(0));
            if (codeStr.charAt(0) == ',') {
                SBUtils.deleteChartrim(codeStr);
                Debug.trace("ArrayInitiation Tar bort komma");
            }
            nextChar = codeStr.charAt(0);
            Debug.trace("ArrayInitiation sist i loop nextChar = " + nextChar);
        }
        SBUtils.deleteChartrim(codeStr);
        
    }
    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("ArrayInitiation");
        for (int i = 0; i < expressions.size(); i++) {
            Expression exp = expressions.get(i);
            node.add(exp.getTreeNode());
        }
        return node;

    }
    public void  secondPass() {
        for (int i = 0; i < expressions.size(); i++) {
            expressions.get(i).secondPass();
        }

    }

    
    @Override
    public String toString() {
        return "ArrayInitiation{" + "expressions=" + expressions + '}';
    }

    @Override
    public String getP5jsCode() {
        String ret="[";
        for (int i = 0; i < expressions.size(); i++) {
            Expression expr = expressions.get(i);
            ret+=expr.getP5jsCode();
            if(i<expressions.size()-1){
                ret+=", ";
            }
        }
        ret+="]";
        return ret;
    }
    
}
