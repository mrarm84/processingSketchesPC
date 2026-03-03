/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.expressions;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import processing2js.Debug;
import processing2js.Global;
import processing2js.SBUtils;
import processing2js.SyntaxErrorException;
import processing2js.Utils;
import processing2js.funcvar.InsertFunctions;
import processing2js.statements.SyntaxNode;

/**
 *
 * @author dahjon
 */
public class ArrayCreation extends Expression {

    String type;
    ArrayList<Expression> lengthes = new ArrayList<>();

    //new int[45]
    public static ArrayCreation getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        String begstr = SBUtils.consumeRegEx(codeStr, "new\\s+[a-zA-Z_$][a-zA-Z_$1-9]*\\s*\\[");

        if (begstr == null) {
            return null;
        } else {
            return new ArrayCreation(parent, begstr, codeStr);
        }
    }

    public ArrayCreation(SyntaxNode parent, String begstr, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        Debug.trace("->ArrayCreation  codeStr: " + codeStr);

        String[] begstrarr = begstr.split(" ", 2);
        type = begstrarr[1].trim();
        type = type.substring(0, type.length() - 1); // REmove [
        char nc = '[';
        while (nc == '[') {

            //int index = SBUtils.consumeInteger(codeStr);
            Expression index = Expression.factory(this, codeStr);
            if (index != null) {
                lengthes.add(index);
            }
            SBUtils.trimDeleteChartrim(codeStr); // remove ]

            nc = codeStr.charAt(0);
            if (nc == '[') {
                SBUtils.trimDeleteChartrim(codeStr);
            }
        }
        Debug.trace("ArrayCreation type = " + type + ", lengthes = " + lengthes + ", codeStr: " + codeStr);
    }

    @Override
    public String toString() {
        return "ArrayCreation{" + "type=" + type + ", lengthes=" + lengthes + '}';
    }

    public void secondPass() {
        for (int i = 0; i < lengthes.size(); i++) {
            lengthes.get(i).secondPass();
        }

    }

    @Override
    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("ArrayCreation: type=" + type);

        DefaultMutableTreeNode onode = new DefaultMutableTreeNode("Lengthes: " + lengthes.toString());
        node.add(onode);

        return node;

    }

    public String getP5jsCode() {
        int dim = lengthes.size();
        String ret;
        Global global = getGlobal();
        boolean initiate = global.isAutoInitArrays();
        if (dim == 1) {
            if (initiate && Utils.isStringNumericType(type)) {
                ret = "processing2jsNewNumericArray(" + lengthes.get(0).getP5jsCode() + ")";
                InsertFunctions.insert(global.getExtraJSCode(), InsertFunctions.NewNumericArray);

            } else {
                ret = "new Array(" + lengthes.get(0).getP5jsCode() + ")";

            }
        } else {
            if (initiate && Utils.isStringNumericType(type)) {
                ret = "processing2jsNewNumericNDimArray([";
                InsertFunctions.insert(global.getExtraJSCode(), InsertFunctions.NewNumericNDArray);
            } else {
                ret = "processing2jsNewNDimArray([";
                InsertFunctions.insert(global.getExtraJSCode(), InsertFunctions.NewNDArray);
            }
            for (int i = 0; i < lengthes.size(); i++) {
                //               Integer l = lengthes.get(i);
                Expression l = lengthes.get(i);
                if (l != null) {
                    ret += l.getP5jsCode();
                    if (i < dim - 1) {
                        ret += ", ";
                    }
                }
//                else {
//                    JOptionPane.showMessageDialog(null, "i: " + i + ",  l:" + l);
//                }

            }
            ret += "])";
            if (global == null) {
                JOptionPane.showMessageDialog(null, "ArrayCreation global är null!!!");

            }

        }
        return ret;
    }

}
