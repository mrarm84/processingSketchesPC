/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.statements;

import processing2js.statements.Statement;
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
public class Block extends Statement {

    ArrayList<Statement> statements = new ArrayList<>();

    Block(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        Debug.trace("Block");
        SBUtils.deleteChartrim(codeStr);
        Debug.trace("Block före consumeStatementsUntilBrace codeStr = " + codeStr);
        Statement.consumeStatementsUntilBrace(this, codeStr, statements);
        SBUtils.trimDeleteChartrim(codeStr); //Remove }
    }
    Block(SyntaxNode parent, Statement stm){
        super(parent);
        statements.add(stm);
        
    }
    
    
   public void  secondPass() {
        for (int i = 0; i < statements.size(); i++) {
            statements.get(i).secondPass();
        }

    }
   
   void addStatement(int i, Statement stm){
       statements.add(i,stm);
   }
   
    @Override
    public String toString() {
        return "Block{" + " statements:\n" + statements + '}';
    }

    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Block");
        DefaultMutableTreeNode statnode = new DefaultMutableTreeNode("Statements");
        for (int i = 0; i < statements.size(); i++) {
            Statement stat = statements.get(i);
            if (stat != null) {
                statnode.add(stat.getTreeNode());
            } else {
                statnode.add(new DefaultMutableTreeNode("null"));
            }
        }
        node.add(statnode);
        return node;

    }

    @Override
    public ArrayList<VariableDefinition> getNonClassVariables() {
        Debug.trace("->Block getNonClassVariables ");

        ArrayList variables = new ArrayList();
        for (int i = 0; i < statements.size(); i++) {
            Statement stm = statements.get(i);
            if (stm instanceof VariableDefinition) {
                variables.add(stm);
                Debug.trace("Block getNonClassVariables dessa statements är variabler stm = " + stm);
            }
        }
        return variables;
    }

    public String getP5jsCode() {

        String ret = "{\n";
        String indentstr=indetionDeptString();
        for (int i = 0; i < statements.size(); i++) {

            Statement stm = statements.get(i);

            String code = "null";
            if (stm != null) {
                code = stm.getP5jsCode();
            }
            ret += indentstr + Global.INDENT + code + ";\n";

        }
        ret += indentstr + "}\n";
        return ret;
    }
}
