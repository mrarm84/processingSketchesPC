/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.statements;

import processing2js.expressions.Expression;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import processing2js.Debug;
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;
import processing2js.expressions.VariableReference;
import processing2js.statements.VariableDefinition;

/**
 *
 * @author dahjon
 */
public class Foreach extends Statement {

    LocalVariable loopObj;
    Expression arrayOrList;
    boolean isArray = true;
    Statement exestm;
    boolean NORMAL_FOR_CODE = true;

    public static Statement getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        if (SBUtils.consumeRegEx(codeStr, "for\\s*\\(") != null) {
            String begstr = VariableDefinition.getBegStr(codeStr, ":");
            if (begstr != null) {
                //              JOptionPane.showMessageDialog(null, "Detta är foreach: "+codeStr);
                return new Foreach(parent, begstr, codeStr);
            } else {
//                return new For(parent, codeStr);
                codeStr.insert(0, "for(");
//                JOptionPane.showMessageDialog(null, "Detta är for: "+codeStr);
            }
        }
        return null;
    }

    Foreach(SyntaxNode parent, String begstr, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);

        Debug.trace("For före loopObj Statement.factory codeStr = " + codeStr);
        loopObj = new LocalVariable(parent, begstr, codeStr);

        //SBUtils.trimDeleteChartrim(codeStr);//remove :
        Debug.trace("For före expr Statement.factory codeStr = " + codeStr);
        arrayOrList = Expression.factory(this, codeStr);

        SBUtils.trimDeleteChartrim(codeStr); //remove )

        if (codeStr.charAt(0) == ')') {
            throw new SyntaxErrorException("Här ska det inte vara någon )", codeStr);
        }
        Debug.trace("For före exestm Statement.factory codeStr = " + codeStr);
        exestm = Statement.factory(this, codeStr);
        SBUtils.trimLeft(codeStr);

    }

    public void secondPass() {
        loopObj.secondPass();
        arrayOrList.secondPass();
        if (NORMAL_FOR_CODE) {
            Block block;
            if (exestm instanceof Block) {
                block = (Block) exestm;
            }
            else {
                block = new Block(parent, exestm);
                exestm = block;
            }
            if (arrayOrList instanceof VariableReference) {
                VariableReference vr = new VariableReference((VariableReference) arrayOrList);
                VariableDefinition arrayDef = vr.getVarDef();
                if (arrayDef.isArray()) {
                    isArray = true;
                }
//                  else {
//                        Reference classMember;
//                        classMember = new FunctionCall(vr, "get");
//                        vr.setClassMember(classMember);
//
//                    }
                vr.addIndex(new VariableReference(vr, "P2JSi"));
                loopObj.setStartValue(vr);
            }
            block.addStatement(0, loopObj);

        }

        exestm.secondPass();

    }

    @Override
    public ArrayList<VariableDefinition> getNonClassVariables() {
        Debug.trace("->Foreach getNonClassVariables ");

        ArrayList variables = new ArrayList();
        variables.add(loopObj);
        if (exestm instanceof VariableDefinition) {
            variables.add(exestm);
        }
        return variables;
    }

    @Override
    public String toString() {
        return "For{" + " initstm:\n" + loopObj + " expr:" + arrayOrList + " \nexestm:\n" + exestm + '}';
    }

    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Foreach Statement");
        DefaultMutableTreeNode initstatnode;
        initstatnode = new DefaultMutableTreeNode("Loop object variable");
        initstatnode.add(loopObj.getTreeNode());
        node.add(initstatnode);

        DefaultMutableTreeNode exprnode = new DefaultMutableTreeNode("Array");
        exprnode.add(arrayOrList.getTreeNode());
        node.add(exprnode);

        DefaultMutableTreeNode exestatnode = new DefaultMutableTreeNode("Execute stament");
        exestatnode.add(exestm.getTreeNode());
        node.add(exestatnode);
        return node;

    }

    public String getP5jsCode() {

        String ret;
//        ret = "for (" + loopObj.getP5jsCode() + " in " + expr.getP5jsCode() + ") " + exestm.getP5jsCode();

        if (NORMAL_FOR_CODE) {
            ret = "for (let P2JSi = 0; P2JSi < " + arrayOrList.getP5jsCode() + ".length; P2JSi++)" + exestm.getP5jsCode();
        } else {
            ret = arrayOrList.getP5jsCode() + ".forEach(function(" + loopObj.getAltName() + ")" + exestm.getP5jsCode() + ";);";
        }

        return ret;
    }
}
