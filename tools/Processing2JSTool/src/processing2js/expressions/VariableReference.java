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
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;
import processing2js.Utils;
import static processing2js.expressions.Literal.consumeNumberString;
import processing2js.funcvar.ClassNode;
import processing2js.funcvar.Constructor;
import processing2js.funcvar.Method;
import processing2js.statements.Parameter;
import processing2js.statements.ClassVariable;
import processing2js.statements.SyntaxNode;
import processing2js.statements.VariableDefinition;

/**
 *
 * @author dahjon
 */
public class VariableReference extends Reference {

    private String name;
    private ArrayList<Expression> indexes = new ArrayList<>(0);
    private VariableDefinition varDef;
    final public int UNKNOWN_SORT = 0;
    final public int LOCAL_SORT = 1;
    final public int CLASS_SORT = 2;
    final public int GLOBAL_SORT = 3;
    public int variableSort = UNKNOWN_SORT;

//    static public final String[][] P5JS_VARIABLES = {
//        {"P3D", "WEBGL"},
//        {"keyPressed", "keyIsPressed"},
//        {"ESC", "ESCAPE"},
//        {"LEFT", "LEFT_ARROW"},
//        {"RIGHT", "RIGHT_ARROW"},
//        {"UP", "UP_ARROW"},
//        {"DOWN", "DOWN_ARROW"},
//        {"THIRD_PI", "(PI/3)"},
//        {"mousePressed", "mouseIsPressed"},
//        {"PVector", "p5.Vector"}
//    };
    public VariableReference(VariableReference vr) {
        super(vr.getParent());
        name = vr.getName();
        indexes = new ArrayList<>(vr.getIndexes());
        classMember = vr.getClassMember();
        varDef = vr.getVarDef();
        variableSort = vr.variableSort;
    }

    public static Expression getInstanceIfYou(StringBuilder codeStr, SyntaxNode parent) throws SyntaxErrorException {
        char nextChar = codeStr.charAt(0);
        Expression expr = null;
        if (Utils.isValidIdentifierNamechar(nextChar, true)) {
            String identifier = SBUtils.consumeIdentifierName(codeStr);
            Debug.trace("VariableReference::getInstanceIfYou identifier = " + identifier);
            SBUtils.trimLeft(codeStr);
            nextChar = codeStr.charAt(0);
//                if (nextChar == '(') {
//                    expr = new FunctionCall(parent, firstIdentifier, codeStr);
//                } else {
            expr = new VariableReference(parent, identifier, codeStr);

//                }
        }
        return expr;
    }

    public VariableReference(SyntaxNode parent, String name, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        this.name = name;
        SBUtils.trimLeft(codeStr);
        char nc = codeStr.charAt(0);

        while (nc == '[') {
            SBUtils.deleteChartrim(codeStr);
            Expression index = Expression.factory(this, codeStr);
            if (index == null) {
                throw (new SyntaxErrorException("Unvalid array index in variable refence ", codeStr));
            }
            indexes.add(index);
            SBUtils.trimDeleteChartrim(codeStr);
            nc = codeStr.charAt(0);
        } //behövs egentligen inte eftersom . blir en del av namnet
        handleDotOperator(nc, codeStr);
        determineifIsLocalVariableAndThenFindVarDef();
    }

//    public void handleDotOperator(char nc, StringBuilder codeStr) throws SyntaxErrorException {
//        // fast inte vid arrayer
//        //Debug.trace("VariableReference för classMember nc = " + nc);
//        if (nc == '.') {
//
//            classMember = Expression.getMethodReference(codeStr, this);;
//            classMember.setAfterDotOperator(true);
//
//        }
//    }
    public VariableReference(SyntaxNode parent, String name) {
        super(parent);
        this.name = name;
        variableSort = LOCAL_SORT;
    }

    public void addIndex(Expression e) {
        indexes.add(e);
    }

    public void secondPass() {
        determineIfIsClassOrGlobalVariableAndThenFindVarDef();
        if (classMember != null) {
            classMember.secondPass();
        }
        for (int i = 0; i < indexes.size(); i++) {
//            if(indexes.get(i)==null){
//                JOptionPane.showConfirmDialog(null, "Variablereference index "+i+ "är null name: "+name+ "parent: "+parent);
//                
//            }
            indexes.get(i).secondPass();
        }
    }

    public String getType() {
        if (varDef != null) {
            return varDef.getType();
        } else {
            return "unknown";
        }
    }

//    public String getp5jsName() {
//        String ret = name;
//        System.out.println("getp5jsName name = " + name);
//        if (isGlobalVariable()) {
//            ret = getGlobal().variableAltName(name);
//
//            if (name.equals(ret)) {
//                ret = Utils.match2dArray(P5JS_VARIABLES, name);
//            }
//        }
//        System.out.println("getp5jsName  sist ret = " + ret);
//        return ret;
//    }
//    public VariableReference(StringBuilder codeStr) {
//
//
//    }
    @Override
    public String toString() {
        return "VariableReference{" + "name=" + name + ", getType()=" + getType() + ", indexes=" + indexes + ", classMember=" + classMember + '}';
    }

    public boolean isClassVariable() {
//        if (variableSort == UNKNOWN_SORT) {
//            determineIfIsClassOrGlobalVariable();
//        }
        return variableSort == CLASS_SORT;
    }

    public void determineIfIsClassVariable() {

        Debug.trace("->determineIfIsClassVariable parent: " + getParent());
        if (variableSort != LOCAL_SORT) {
            SyntaxNode paren = getClassParent();

            if (paren != null) {
                Debug.trace("VariableReference Förälder är klass");
                ClassNode par = (ClassNode) paren;
                ArrayList<ClassVariable> vars = par.getClassVariables();
                for (int i = 0; i < vars.size(); i++) {
                    VariableDefinition var = vars.get(i);
                    Debug.trace("var.getName() = " + var.getName() + ", name: " + name);
                    if (var.getName().equals(name)) {
                        //return true;
                        varDef = var;
                        variableSort = CLASS_SORT;
                    }

                }
                if (variableSort != CLASS_SORT) {
                    if (par.getExtendsclass() != null) {
                        String extStr = par.getExtendsclass();
                        //System.out.println("letar efter name '" + name + "' i föräldraklassen '" + extStr + "'");
                        final ClassVariable cv = getGlobal().isVariableMemberOfClass(extStr, name);
                        if (cv != null) {
                            varDef = cv;
                            variableSort = CLASS_SORT;
                        }
                    }
                }
            }
        }
        //return false;
    }

    public boolean isLocalVariable() {
        return variableSort == LOCAL_SORT;

    }

    private void determineifIsLocalVariableAndThenFindVarDef() {
        SyntaxNode node = this;
        VariableDefinition var = null;
        //boolean found = false;
        while (var == null && node != null && !(node instanceof ClassNode) && !(node instanceof Global)) {
            Debug.trace("VariableReference determineifIsLocalVariable node=" + node);
            ArrayList<VariableDefinition> nonClassVariables = node.getNonClassVariables();
            if (nonClassVariables != null) {
                var = Utils.searchVariableArrayList(nonClassVariables, name);
            }
            if (node instanceof Method && var == null) {
                Method m = (Method) node;
                var = Utils.searchParameterArrayList(m.getParameters(), name);
            } else if (node instanceof Constructor && var == null) {
                Constructor m = (Constructor) node;
                var = Utils.searchParameterArrayList(m.getParameters(), name);
            }
//
//                if(node instanceof Method){
//                Method m = (Method)node;
//                ArrayList<Parameter> params = m.getParameters();
//                for (int i = 0; i < params.size(); i++) {
//                    Parameter par = params.get(i);
//                    if(par.getName().equals(name)){
//                        found=true;
//                    }
//                }
//            }
//            if(node instanceof Constructor){
//                Constructor m = (Constructor)node;
//                ArrayList<Parameter> params = m.getParameters();
//                for (int i = 0; i < params.size(); i++) {
//                    Parameter par = params.get(i);
//                    if(par.getName().equals(name)){
//                        found=true;
//                    }
//                }
//            }

            node = node.getParent();
            //Debug.trace("isLocalVariable inte hittad paren = " + node);
        }
        if (var != null) {
            variableSort = LOCAL_SORT;
        }
        varDef = var;
        //return found;
    }
//      public boolean deftermineifIsLocalVariable(){
//        SyntaxNode node = this;
//        boolean found = false;
//        while (!found&&!(node instanceof ClassNode) && node != null) {
//            found = Utils.searchVariableArrayList(node.getNonClassVariables(), name);
//            if(node instanceof Method){
//                Method m = (Method)node;
//                ArrayList<Parameter> params = m.getParameters();
//                for (int i = 0; i < params.size(); i++) {
//                    Parameter par = params.get(i);
//                    if(par.getName().equals(name)){
//                        found=true;
//                    }
//                }
//            }
//            if(node instanceof Constructor){
//                Constructor m = (Constructor)node;
//                ArrayList<Parameter> params = m.getParameters();
//                for (int i = 0; i < params.size(); i++) {
//                    Parameter par = params.get(i);
//                    if(par.getName().equals(name)){
//                        found=true;
//                    }
//                }
//            }
//            
//            
//            node = node.getParent();
//            //Debug.trace("isLocalVariable inte hittad paren = " + node);
//        }
//        return found;
//    }
//    
    //Befövs inte??

    public boolean isGlobalVariable() {
//        if (variableSort == UNKNOWN_SORT) {
//            determineIfIsClassOrGlobalVariable();
//        }
        return variableSort == GLOBAL_SORT;
    }

    public String getVariableSortString() {
        if (variableSort == UNKNOWN_SORT) {
            determineIfIsClassOrGlobalVariableAndThenFindVarDef();
        }
        switch (variableSort) {
            case LOCAL_SORT:
                return "local";
            case GLOBAL_SORT:
                return "global";
            case CLASS_SORT:
                return "class";
        }
        return "unknown";
    }

    public void determineIfIsClassOrGlobalVariableAndThenFindVarDef() {
        determineIfIsClassVariable();
        determineIfIsGlobalVariable();

    }

    public void determineIfIsGlobalVariable() {
        if (variableSort != LOCAL_SORT && variableSort != CLASS_SORT) {
            variableSort = GLOBAL_SORT;
            SyntaxNode global = getGlobal();
            varDef = Utils.searchVariableArrayList(global.getNonClassVariables(), name);
        }
    }

//    public boolean isParentVariable() {
//
//        SyntaxNode paren = getClassParent();
//
//        if (paren != null) {
//            Debug.trace("VariableReference::isParentVariable Förälder är klass");
//            ClassNode par = (ClassNode) paren;
//
//            if (variableSort != CLASS_SORT) {
//                //Kolla om föräldern har deklarerat den
//                if (par.getExtendsclass() != null) {
//                    String extStr = par.getExtendsclass();
//                    System.out.println("letar efter name '" + name + "' i föräldraklassen '" + extStr + "'");
//                    if (getGlobal().isVariableMemberOfClass(extStr, name)) {
//                        return true;
//                    }
//                }
//            }
//
//        }
//        return false;
//
//    }
    public SyntaxNode getClassParent() {
        SyntaxNode paren = getParent();
        while (!(paren instanceof ClassNode) && paren != null) {
            paren = paren.getParent();
            //Debug.trace("isClassVariable inte hittad paren = " + paren);
        }
        if (!(paren instanceof ClassNode)) {
            paren = null;
        }
        return paren;
    }

    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("VariableReference: name=" + name + ", type: " + getType() + ", sort: " + getVariableSortString() + ", getAltName(): " + getAltName());

        if (classMember != null) {
            DefaultMutableTreeNode classnode = new DefaultMutableTreeNode("Class Reference");
            DefaultMutableTreeNode exprnode = classMember.getTreeNode();
            classnode.add(exprnode);
            node.add(classnode);
        }
        if (!indexes.isEmpty()) {
            DefaultMutableTreeNode arrnode = new DefaultMutableTreeNode("Array indexes");
            for (int i = 0; i < indexes.size(); i++) {
                Expression exp = indexes.get(i);
                DefaultMutableTreeNode exprnode = exp.getTreeNode();
                arrnode.add(exprnode);

            }
            node.add(arrnode);
        }
        return node;

    }

    public String getAltName() {
        if (varDef != null) {
            return varDef.getAltName();
        } else {
            return name;
        }
    }

    public String getP5jsCode() {
        String ret = "";

        if (getType().equals("PGraphics")
                && (classMember != null)
                && (classMember instanceof FunctionCall)
                && ((((FunctionCall) classMember).getName().equals("beginDraw"))
                || ((FunctionCall) classMember).getName().equals("endDraw"))) {
            ret = "// beginDraw() and endDraw() is not supportet in p5.js, and or often not needed";

        } else if (name.equals("PFont")
                && (classMember != null)
                && (classMember instanceof FunctionCall)
                && (((FunctionCall) classMember).getName().equals("list"))) {
            ret = "\"Pfont.list() has no eqvivalent in P5.js\"";
        } else if (name == "pixels") {
            ret = "set(";
            for (int i = 0; i < indexes.size(); i++) {
                if (i != 0) {
                    ret += ", ";
                }
                String index = indexes.get(i).getP5jsCode();
                ret += index;

            }
            ret += ")";

        } else {
            boolean isClassvar = false;
            if (isClassVariable()) {
                isClassvar = true;
            }
            if (isClassvar && !isAfterDotOperator()) {
                ret += "this." + name;
            } else if (isClassvar || isAfterDotOperator()) {
                ret += name;
            } else {
                ret += getAltName();
            }
            Debug.trace("VAriableRefernce getP5jsCode: name: " + name + ", isClassvar: " + isClassvar + ", isAfterDotOperator(): " + isAfterDotOperator() + ", ret: " + ret + ", parent: " + parent.toString());

            if (!indexes.isEmpty()) {
                for (int i = 0; i < indexes.size(); i++) {
                    Debug.trace("Variable Reference getP5jsCode indexes.get(" + i + "):" + indexes.get(i) + " name: " + name);
                    String index = indexes.get(i).getP5jsCode();
                    ret += "[" + index + "]";

                }
            }
            if (classMember != null) {
                String cm = classMember.getP5jsCode();
                if (cm.charAt(0) != '[') {  //Detta betyder att det är en arraylist
                    ret += "." + cm;
                } else {
                    ret += cm;
                }

            }
        }
        return ret;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Expression> getIndexes() {
        return indexes;
    }

    public Reference getClassMember() {
        return classMember;
    }

    public void setClassMember(Reference classMember) {
        this.classMember = classMember;
    }

    public VariableDefinition getVarDef() {
        return varDef;
    }

    public boolean hasDotOperator() {
        return classMember != null;
    }
}
