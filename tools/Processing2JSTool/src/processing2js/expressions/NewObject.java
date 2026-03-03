/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.expressions;

import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import processing2js.Debug;
import processing2js.Global;
import processing2js.SBUtils;
import processing2js.SyntaxErrorException;
import processing2js.funcvar.ClassNode;
import processing2js.statements.Statement;
import processing2js.statements.SyntaxNode;

/**
 *
 * @author dahjon
 */
public class NewObject extends Reference {

    String name;
    ClassNode classDef = null;
    String initFunctionName = null;
    ArrayList<Expression> parameters = new ArrayList<>();

    public static NewObject getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
//        String begstr = SBUtils.consumeRegEx(codeStr, "new\\s+[a-zA-Z_$][a-zA-Z_$1-9]*\\s*\\(");
        String begstr = SBUtils.consumeRegEx(codeStr, "new\\s+[a-zA-Z_$][a-zA-Z_$1-9]*\\s*(<[a-zA-Z_$0-9]*>\\s*)?\\(");

        if (begstr == null) {
            return null;
        } else {
            Debug.tracePrio("begstr: "+begstr);
            return new NewObject(parent, begstr, codeStr);
        }
    }

    public NewObject(SyntaxNode parent, String begstr, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        begstr = begstr.replaceFirst("<[a-zA-Z_$0-9]*>\\s*", "");
        String[] begstrarr = begstr.split(" ", 2);
        name = begstrarr[1].trim();
        name = name.substring(0, name.length() - 1); // REmove (
        Debug.trace("NewObject name = " + name);
        SBUtils.trimLeft(codeStr);
        char nextChar = codeStr.charAt(0);
        Debug.trace("NewObject nextChar = " + nextChar);
        boolean stop = false;
        while (nextChar != ')' && !stop) {  //Åtminstone en parameter
            //codeStr.deleteCharAt(0);
            if (nextChar == ',') {
                SBUtils.deleteChartrim(codeStr);
            }
            SBUtils.trimLeft(codeStr);
            Debug.trace("FuntionCall i loop codeStr:\n" + codeStr);
            Expression param = NewObject.factory(this, codeStr);
            if (param == null) {
                stop = true;
            } else {
                parameters.add(param);
                nextChar = codeStr.charAt(0);
                Debug.trace("NewObject i loop nextChar = " + nextChar);
            }

        }
        SBUtils.trimDeleteChartrim(codeStr); //remove )
        handleDotOperator(nextChar, codeStr);
//        if (rowStatement) {
//            Debug.trace("FunctionCall syftet är att ta bort ; codeStr.charAt(0) = " + codeStr.charAt(0));
//            Utils.deleteChartrim(codeStr); //remove ;
//        }

    }

    public void secondPass() {
        final Global global = getGlobal();
        if (global.isMultiConstructorMode()) {
            classDef = global.searchClass(name);
            if (classDef != null) {
                initFunctionName = classDef.getNewInstanceFunctionName(parameters);
            }
        }
        for (int i = 0; i < parameters.size(); i++) {
            parameters.get(i).secondPass();
        }

    }

    @Override
    public String toString() {
        return "NewObject{" + "name=" + name + ", parameters=" + parameters + '}';
    }

    String getAltName() {
        if (name.equals("PVector")) {
            return "p5.Vector";
        }
        return name;
    }

    @Override
    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("NewObject: name=" + name);

        DefaultMutableTreeNode statnode = new DefaultMutableTreeNode("Parameters");
        for (int i = 0; i < parameters.size(); i++) {
            Statement param = parameters.get(i);
            statnode.add(param.getTreeNode());
        }
        node.add(statnode);
        return node;

    }

    @Override
    public String getType() {
        return name;
    }

    public String getExceptionCode() {
        String ret = "";
        if (name.equals("ArrayList")) {
            ret = "new Array()";
        }
//        else if(name.equals("PVector")){
//            return "createVector()";
//        }
        return ret;
    }

    public String getP5jsCode() {
        if (classDef != null && getGlobal().isMultiConstructorMode()) {
            return getP5jsCodeMulti();
        }
        return getP5jsCodeSingle();
    }

    public String getP5jsCodeMulti() {
        String ret = getAltName() +"." +classDef.getNewInstanceFunctionName(parameters) + "(";
        for (int i = 0; i < parameters.size(); i++) {
            Expression par = parameters.get(i);
            if (i != 0) {
                ret += ", ";
            }
            ret += par.getP5jsCode();

        }
        ret += ")";
        Debug.trace("NewObject::getP5jsCodeMulti ret = " + ret);
        return ret;
    }

    public String getP5jsCodeSingle() {

        String ret = getExceptionCode();
        if (ret.isEmpty()) {
            ret += "new " + getAltName() + "(";
            for (int i = 0; i < parameters.size(); i++) {
                Expression par = parameters.get(i);
                if (i != 0) {
                    ret += ", ";
                }
                ret += par.getP5jsCode();

            }
            ret += ")";
            Debug.trace("NewObject::getP5jsCodeSingle ret = " + ret);
        }
        return ret;
    }

}
