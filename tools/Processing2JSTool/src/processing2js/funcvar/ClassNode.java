/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.funcvar;

import java.security.Policy.Parameters;
import processing2js.statements.ClassVariable;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import processing2js.Debug;
import processing2js.Global;
import static processing2js.Global.compareParameters;
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;
import processing2js.expressions.Expression;
import processing2js.statements.Parameter;
import processing2js.statements.Statement;
import processing2js.statements.SyntaxNode;
import processing2js.statements.VariableDefinition;

/**
 *
 * @author dahjon
 */
public class ClassNode extends FuncVar {

    String extendsclass = null;
    String implementsclass = null;
    boolean isAbstract = false;
    private ArrayList<JsVariable> jsVariables = new ArrayList();
    private ArrayList<Constructor> constructors = new ArrayList();
    private ArrayList<ClassVariable> variables = new ArrayList();
    private ArrayList<Method> methods = new ArrayList();

    public static ClassNode getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        SBUtils.trimLeft(codeStr);
        final boolean isAbstract = SBUtils.isBeginningEqualToString(codeStr, "abstract");
        if (isAbstract || SBUtils.isBeginningEqualToString(codeStr, "class") || isAbstract) {
            if (isAbstract) {
                SBUtils.deleteChartrim(codeStr, 8);
            }
            return new ClassNode(parent, codeStr, isAbstract);
        } else {
            return null;
        }
    }

    private ClassNode(SyntaxNode parent, StringBuilder codeStr, boolean isAbstract) throws SyntaxErrorException {
        super(parent);
        this.isAbstract = isAbstract;
        Debug.trace("->Class codeStr = " + codeStr);
        SBUtils.deleteChartrim(codeStr, 5);
        String nameExtImp = SBUtils.consumeBefore(codeStr, "{").trim();
        String[] nameExptImpArr = nameExtImp.split(" ");
        name = nameExptImpArr[0];
        Debug.trace("ClassNode name = " + name);
        for (int i = 1; i < nameExptImpArr.length; i++) {
            String str = nameExptImpArr[i].trim();
            Debug.trace("ClassNode i loop str = " + str);
            if (str.equals("extends")) {
                extendsclass = nameExptImpArr[i + 1].trim();
            } else if (str.equals("implements")) {
                implementsclass = nameExptImpArr[i + 1].trim();
            }

        }
        SBUtils.trimDeleteChartrim(codeStr); //remove {

        while (codeStr.length()>0 && codeStr.charAt(0) != '}') {
            SBUtils.consumeRegEx(codeStr, "(private |public ) *");
            
            SyntaxNode node = ClassVariable.getInstanceIfYou(this, codeStr);
            if (node != null) {
                variables.add((ClassVariable) node);
                SBUtils.trimLeft(codeStr);
//                if(codeStr.charAt(0)!=';'){
//                    throw new SyntaxErrorException("Inget semikolon efter classvariabledeklaration",codeStr);
//                }

                //Den här behöver vara här eftersom assignment plockar bort ;
                if (codeStr.charAt(0) == ';') {
                    SBUtils.trimDeleteChartrim(codeStr);
                }
            } else if ((node = Method.getInstanceIfYou(this, codeStr)) != null) {
                methods.add((Method) node);
            } else if ((node = Constructor.getInstanceIfYou(this, codeStr)) != null) {
                constructors.add((Constructor) node);
            } else if ((node = JsVariable.getInstanceIfYou(this, codeStr)) != null) {
                jsVariables.add((JsVariable) node);
            }
            if(node == null){
                throw (new SyntaxErrorException("Nor variable nor method, or constructor in Class Definition", codeStr));
            }
        }
        if (constructors.size() == 0) {
            constructors.add(new Constructor(this));
        } else if (constructors.size() >= 2) {
            getGlobal().setMultiConstructorMode(true);
        }

        SBUtils.trimConsumeChartrim(codeStr); //remove }

    }

    public void secondPass() {

        if (extendsclass != null) {
            getGlobal().placeBehindParentClass(name, extendsclass);
        }
        for (int i = 0; i < constructors.size(); i++) {
            constructors.get(i).secondPass();
        }

        for (int i = 0; i < variables.size(); i++) {
            variables.get(i).secondPass();
        }
        for (int i = 0; i < methods.size(); i++) {
            methods.get(i).secondPass();
        }

    }

    public ArrayList<ClassVariable> getOnlySpecificClassVariables() {
        return variables;
    }

    public ArrayList<ClassVariable> getClassVariables() {
        if (extendsclass != null) {
            ClassNode superClass = getGlobal().searchClass(extendsclass);
            if (superClass != null) { // superClass should only be null if extendning external class
                ArrayList<ClassVariable> allVars = new ArrayList(superClass.getClassVariables());
                allVars.addAll(variables);
                return allVars;
            }
        }
        return variables;
    }

    public ClassVariable isVariableMember(String varName) {
        for (int i = 0; i < variables.size(); i++) {
            ClassVariable var = variables.get(i);
            if (var.getName().equals(varName)) {
                return var;
            }
        }
        return null;
    }

    public ArrayList<VariableDefinition> getNonClassVariables() {
        ArrayList<VariableDefinition> vars = new ArrayList(variables);
        return vars;
    }

    public boolean isSubClass() {
        return extendsclass != null;
    }

    public ArrayList<Method> getMethods() {
        return methods;
    }

    public String getExtendsclass() {
        return extendsclass;
    }

    public String getNewInstanceFunctionName(ArrayList<Expression> parameters) {
        final Constructor con = getConstructor(parameters);
        String ret = con.getNewInstanceFunctionName();
        System.out.println("ClassNode::getNewInstanceFunctionName ret = " + ret);
        return ret;
    }

    public Constructor getConstructor(ArrayList<Expression> parameters) {
        Constructor retVal = null;
        int curStatus = 0;
        for (int i = 0; i < constructors.size(); i++) {
            Constructor f = constructors.get(i);
            String name = f.getName();
            int status = Global.compareParameters(parameters, f);
            if (curStatus < status) {
                retVal = f;
                curStatus = status;
            }
        }
        return retVal;
    }

    public MutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Class Def:  name=" + name + " extendsclass:  " + extendsclass);
        if (constructors.size() == 1) {
            node.add(constructors.get(0).getTreeNode());
        } else {
            DefaultMutableTreeNode classnode = new DefaultMutableTreeNode("Constructors");
            for (int i = 0; i < constructors.size(); i++) {
                Constructor cv = constructors.get(i);
                classnode.add(cv.getTreeNode());
            }
            node.add(classnode);

        }
        DefaultMutableTreeNode varnode = new DefaultMutableTreeNode("Variables");
        for (int i = 0; i < variables.size(); i++) {
            ClassVariable var = variables.get(i);
            varnode.add(var.getTreeNode());
        }
        node.add(varnode);
        DefaultMutableTreeNode mnode = new DefaultMutableTreeNode("Methods");
        for (int i = 0; i < methods.size(); i++) {
            Method m = methods.get(i);
            mnode.add(m.getTreeNode());
        }
        node.add(mnode);
        return node;

    }

    private String getExtraMultiContructorModeCode() {

        String ret = "";
        if (getGlobal().isMultiConstructorMode()) {
            ret += Global.INDENT + "constructor(){\n";
            if (extendsclass != null) {
                ret += Global.INDENT + Global.INDENT + "super();\n";

            }
            ret += Constructor.getInitiationCode(this, null);
            ret += Global.INDENT + "}\n\n";
        }
        return ret;

    }

    public String getP5jsCode() {

        String ret = "class " + name;
        if (extendsclass != null) {
            ret += " extends " + extendsclass;
        }
        ret += "{\n";
//        for (int i = 0; i < variables.size(); i++) {
//            ClassVariable var = variables.get(i);
////            if (i != 0) {
////                ret += ", ";
////            }
//            ret += Global.INDENT+var.getP5jsCode();
//
//        }

        ret += getExtraMultiContructorModeCode();

        for (int j = 0; j < constructors.size(); j++) {
            ret += constructors.get(j).getP5jsCode();
        }
        for (int i = 0; i < methods.size(); i++) {
            Method fun = methods.get(i);
            ret += fun.getP5jsCode();

        }
        //ret+=extraJSCode;
        ret += "}\n\n";
        return ret;
    }

}
