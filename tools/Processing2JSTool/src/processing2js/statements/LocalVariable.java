package processing2js.statements;

import processing2js.expressions.Expression;
import javax.swing.tree.DefaultMutableTreeNode;
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;
import processing2js.statements.Statement;
import processing2js.statements.SyntaxNode;

/**
 *
 * @author dahjon
 */
public class LocalVariable extends VariableDefinition {
    public final String NODE_NAME="LocalVariable";

    public LocalVariable(SyntaxNode parent, String begstr, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent, begstr, codeStr);
    }
    public static VariableDefinition getInstanceIfYou(SyntaxNode parent,StringBuilder codeStr) throws SyntaxErrorException{
        String begstr  = getBegStr(codeStr);
        if(begstr==null){
            return null;
        }
        else {
            return new LocalVariable(parent, begstr,codeStr);
        }
    }
    public String getNodeName(){
        return NODE_NAME;
    }
//public class LocalVariable extends Statement {
//
//    String type;
//    String name;
//
//    Expression startValue = null;
//
//    LocalVariable(SyntaxNode parent, String type, String name, StringBuilder codeStr) throws SyntaxErrorException {
//        super(parent);
//        this.type = type;
//        this.name = name;
//
//        char nc = Utils.trimConsumeChartrim(codeStr);
//        if (nc == '=') {
//            startValue = Expression.factory(this,codeStr);
//        }
//        //Utils.trimDeleteChartrim(codeStr); // remove ;
//
//    }
//
//    @Override
//    public String toString() {
//        return "Local Variable{" + "name=" + name + ", type=" + type + ", startValue=" + startValue + '}';
//    }
//
    
//    @Override
//    public DefaultMutableTreeNode getTreeNode() {
//        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Local Variable Def: type=" + type + ", name=" + name);
//
//        
//        DefaultMutableTreeNode exprnode = startValue.getTreeNode();
//        node.add(exprnode);
//        return node;
//
//    }

    @Override
    public String getAltName() {
        return name;
    }

    
    @Override
    public String getP5jsCode() {
        String assignCode = "";
        if (startValue != null) {
            assignCode = " = " + startValue.getP5jsCode();
        }
        return "let " + name + assignCode;
    }

}
