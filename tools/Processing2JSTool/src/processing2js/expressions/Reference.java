/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.expressions;

import javax.swing.tree.DefaultMutableTreeNode;
import processing2js.SyntaxErrorException;
import processing2js.statements.SyntaxNode;

/**
 *
 * @author dahjon
 */
public abstract class Reference extends Expression {

    private boolean afterDotOperator = false;
    Reference classMember = null;

    public Reference(SyntaxNode parent) {
        super(parent);
    }

    public boolean isAfterDotOperator() {
        return afterDotOperator;
    }

    public void setAfterDotOperator(boolean afterDotOperator) {
        this.afterDotOperator = afterDotOperator;
    }

    public void handleDotOperator(char nc, StringBuilder codeStr) throws SyntaxErrorException {
        if (nc == '.') {

            classMember = Expression.getMethodReference(codeStr, this);;
            classMember.setAfterDotOperator(true);
        }
    }

    public void addAfterDorOperatorNode(DefaultMutableTreeNode parentNode) {
        if (classMember != null) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("AfterDorOperator:" + ", type:" + getType() + "code: " + classMember.getP5jsCode());
            parentNode.add(node);
        }

    }
    
    Reference getAfterDotOperator(){
        return classMember;
    }

    public String getDotOperatorCode() {
        String dotOpCode = "";
        if (classMember != null) {
            String cm = classMember.getP5jsCode();
            if (cm.charAt(0) != '[') {  //Detta betyder att det är en arraylist
                dotOpCode += "." + cm;
            } else {
                dotOpCode += cm;
            }
        }
        return dotOpCode;
    }

}
