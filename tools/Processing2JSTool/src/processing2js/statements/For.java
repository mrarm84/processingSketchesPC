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
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;

/**
 *
 * @author dahjon
 */
public class For extends Statement {

    Expression expr;
    Statement initstm;
    Statement exestm;
    Statement incstm;

    public static For getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        if (SBUtils.consumeRegEx(codeStr, "for\\s*\\(") != null) {
            return new For(parent, codeStr);
        } else {
            return null;
        }
    }

    For(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        //Debug.trace("For");
        //SBUtils.deleteChartrim(codeStr); //remove (  varför har det inte alltid skitit sig?

        Debug.trace("For före inistm Statement.factory codeStr = " + codeStr);
        initstm = Statement.factory(this, codeStr);
        Debug.trace("For efter inistm Statement.factory codeStr = " + codeStr);

        if (codeStr.charAt(0) == ',') {
            throw new SyntaxErrorException("Komma saknas efter första satsen i for ", codeStr);
        }

        SBUtils.trimDeleteChartrim(codeStr);//remove ,

        Debug.trace("For före expr Statement.factory codeStr = " + SBUtils.debugStr(codeStr));
        if (codeStr.charAt(0) == ';') {
            throw new SyntaxErrorException("Här ska det inte vara någon ; ", codeStr);
        }
        expr = Expression.factory(this, codeStr);
        if (codeStr.charAt(0) == ',') {
            throw new SyntaxErrorException("Komma saknas efter jämförselsen i for ", codeStr);
        }
        Debug.trace("For före trim ; codeStr = " + SBUtils.debugStr(codeStr));

        SBUtils.trimLeft(codeStr); //remove ;
        if (codeStr.charAt(0) == ';') {
            SBUtils.trimDeleteChartrim(codeStr); //remove ;
        }
        Debug.trace("For före incstm Statement.factory codeStr = " + SBUtils.debugStr(codeStr));
        incstm = Statement.factory(this, codeStr);
        Debug.trace("For efter incstm  incstm= " + incstm);
        Debug.trace("For efter incstm Statement.factory codeStr = " + SBUtils.debugStr(codeStr));
        SBUtils.trimDeleteChartrim(codeStr); //remove )

        if (codeStr.charAt(0) == ')') {
            throw new SyntaxErrorException("Här ska det inte vara någon )", codeStr);
        }
        Debug.trace("For före exestm Statement.factory codeStr = " + SBUtils.debugStr(codeStr));
        exestm = Statement.factory(this, codeStr);
        SBUtils.trimLeft(codeStr);

    }

    public void secondPass() {
        expr.secondPass();
        initstm.secondPass();
        exestm.secondPass();
        incstm.secondPass();

    }

    @Override
    public ArrayList<VariableDefinition> getNonClassVariables() {
        Debug.trace("->For getNonClassVariables ");

        if (initstm instanceof VariableDefinition) {
            ArrayList variables = new ArrayList();
            variables.add(initstm);
            if (exestm instanceof VariableDefinition) {
                variables.add(exestm);
            }

            return variables;
        }
        return null;
    }

    @Override
    public String toString() {
        return "For{" + " initstm:\n" + initstm + " expr:" + expr + " incstm:" + incstm + " \nexestm:\n" + exestm + '}';
    }

    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("For Statement");
        DefaultMutableTreeNode initstatnode = new DefaultMutableTreeNode("Init statement");
        initstatnode.add(initstm.getTreeNode());
        node.add(initstatnode);

        DefaultMutableTreeNode exprnode = expr.getTreeNode();
        node.add(exprnode);

        DefaultMutableTreeNode incstatnode = new DefaultMutableTreeNode("Inc stament");
        incstatnode.add(incstm.getTreeNode());
        node.add(incstatnode);

        DefaultMutableTreeNode exestatnode = new DefaultMutableTreeNode("Execute stament");
        exestatnode.add(exestm.getTreeNode());
        node.add(exestatnode);

        return node;

    }

    public String getP5jsCode() {

        String ret = "for(" + initstm.getP5jsCode() + ";" + expr.getP5jsCode() + ";" + incstm.getP5jsCode() + ") " + exestm.getP5jsCode();

        return ret;
    }
}
