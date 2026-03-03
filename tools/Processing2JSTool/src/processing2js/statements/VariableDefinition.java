/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.statements;

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
import processing2js.expressions.Expression;
import javax.swing.tree.DefaultMutableTreeNode;
import processing2js.Debug;
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;
import processing2js.Utils;
import processing2js.expressions.Literal;

/**
 *
 * @author dahjon
 */
public class VariableDefinition extends Statement {

    public static final String SKETCH_VARIABLE_PREFIX = "sketch";

    public static final String[] SYSTEM_GLOBAL = {
        "black",
        "blue",
        "brown",
        "coral",
        "crimson",
        "cyan",
        "darkblue",
        "darkgray",
        "green",
        "grey",
        "orange",
        "pink",
        "red",
        "violet",
        "white",
        "yellow",
        //functions
        "delay",
        //Processing functions
        "alpha",
        "blue",
        "brightness",
        "color",
        "green",
        "hue",
        "lerpColor",
        "lightness",
        "red",
        "saturation",
        //"p5.Color",
        "background",
        "clear",
        "colorMode",
        "fill",
        "noFill",
        "noStroke",
        "stroke",
        "arc",
        "ellipse",
        "circle",
        "line",
        "point",
        "quad",
        "rect",
        "square",
        "triangle",
        "ellipseMode",
        "noSmooth",
        "rectMode",
        "smooth",
        "strokeCap",
        "strokeJoin",
        "strokeWeight",
        //Curves,
        "bezier",
        "bezierDetail",
        "bezierPoint",
        "bezierTangent",
        "curve",
        "curveDetail",
        "curveTightness",
        "curvePoint",
        "curveTangent",
        "beginContour",
        "beginShape",
        "bezierVertex",
        "curveVertex",
        "endContour",
        "endShape",
        "quadraticVertex",
        "vertex",
        // 3D Primitives
        "plane",
        "box",
        "sphere",
        "cylinder",
        "cone",
        "ellipsoid",
        "torus",
        // Models,
        "loadModel",
        // javascript stuff
        "model",
        "location",
        "window",
        "history",
        "navigator",
        "alert",
        "document",
        "yield",
        "native",
        "arguments",
        "let",
        "eval",
        "escape",
        "blur",
        "scroll",
        "open",
        "constructor",
        "frames"

    };

    public String NODE_NAME = "VariableDefinition";

    String type;
    String name;
    private String altName;

    Expression startValue = null;
    int arrayDimensions = 0;
    //ArrayList<Integer> arraylengths = new ArrayList();

    public static VariableDefinition getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        String begstr = getBegStr(codeStr);
        if (begstr == null) {
            return null;
        } else {
            return new VariableDefinition(parent, begstr, codeStr);
        }
    }

    boolean isNumeric() {
        return Utils.isStringNumericType(type);
    }

    public boolean isArray() {
        return arrayDimensions != 0;
    }
    public static String getBegStr(StringBuilder codeStr) {

        return getBegStr(codeStr, ";=,");
    }
//ArrayList<maskdel>
//ArrayList<Float>    

    public static String getBegStr(StringBuilder codeStr, String endChars) {
        //String begstr  = Utils.consumeRegEx(codeStr, "[a-zA-Z_$][a-zA-Z_$0-9]*\\s[a-zA-Z_$][a-zA-Z_$0-9]*+\\s*[;=]");
//        String begstr = SBUtils.consumeRegEx(codeStr, "(final )?\\s*[a-zA-Z_$][a-zA-Z_$0-9]*\\s*[\\[\\]]*\\s+[a-zA-Z_$][a-zA-Z_$0-9]*\\s*[\\[\\]]*\\s*[" + endChar + "=,]");

        //The three following lines handles the unusual float[]nodeX

        if(SBUtils.isBeginningEqualToRegEx(codeStr, "(final )?\\s*[a-zA-Z_$][a-zA-Z_$0-9]*\\[\\][a-zA-Z_$]")){
            int bracketInd = codeStr.indexOf("]");
            codeStr.insert(bracketInd+1, " ");
            //JOptionPane.showMessageDialog(null, "Ny sträng efter extra mellanslag:\n"+codeStr);
        }

        String begstr = SBUtils.consumeRegEx(codeStr, "(final )?\\s*[a-zA-Z_$][a-zA-Z_$0-9]*(\\s*<[a-zA-Z_$][a-zA-Z_$0-9]*>)?\\s*[\\[\\]]*\\s+[a-zA-Z_$][a-zA-Z_$0-9]*\\s*[\\[\\]]*\\s*[" + endChars + "]");
        //Debug.trace("VariableDefinition begstr = " + begstr + "codeStr: " + codeStr);
        return begstr;
    }
    public static void main(String[] args) {
        String[] arr = {
            "float[]nodeX;",
            "float[] nodeY;",
            "nodeStartX[i];"};
        for (int i = 0; i < arr.length; i++) {
            String str = arr[i];
            String begstr = getBegStr(new StringBuilder(str));
            //System.out.println("begstr = " + begstr);
        }
            
    }
    public VariableDefinition getNewInstance(SyntaxNode parent, String begstr, StringBuilder codeStr) throws SyntaxErrorException {
        return new VariableDefinition(parent, begstr, codeStr);
    }

    public VariableDefinition(SyntaxNode parent) {
        super(parent);
    }

    VariableDefinition(SyntaxNode parent, String begstr, StringBuilder codeStr) throws SyntaxErrorException {
        super(parent);
        begstr = begstr.replaceFirst("\\s*<[a-zA-Z_$][a-zA-Z_$0-9]*>", "");
        Debug.trace("VariableDefinition codeStr = " + SBUtils.debugStr(codeStr));
        getTypeAndNamefromBegStr(begstr);
//        while (type.equals("final")||type.equals("private")||type.equals("public")) {
        if (type.equals("final")) {
            begstr = name;
            getTypeAndNamefromBegStr(begstr);

        }
        SBUtils.trimLeft(codeStr);
        name = name.substring(0, name.length() - 1).trim(); // REmove =
//        if (name.indexOf("[]") > -1) {
//            JOptionPane.showMessageDialog(null, "name.lastIndexOf(\"[]\"): " + name.lastIndexOf("[]") + " name.length(): "+name.length() + ", name: "+name);
//
//        }
        while (name.length() > 2 && name.lastIndexOf("[]") == (name.length() - 2)) {
//            JOptionPane.showMessageDialog(null, "name.lastIndexOf(\"[]\"): " + name.lastIndexOf("[]"));
            name = name.substring(0, name.length() - 2);
            type += "[]";
        }
        Debug.trace("VariableDefinition type = " + type + ", name = " + name);
        char lastchar = begstr.charAt(begstr.length() - 1);
        if (lastchar == '=') {
            startValue = Expression.factory(this, codeStr);
            if (codeStr.charAt(0) == ',') {
                SBUtils.trimDeleteChartrim(codeStr);
                codeStr.insert(0, type + " ");

            }
            //JOptionPane.showMessageDialog(null, "VariableDefinition efter = ,och  Expression.factory\ncodeStr: '"+codeStr);
        } else if (lastchar == ',') {
            codeStr.insert(0, type + " ");
        }
        //Utils.trimDeleteChartrim(codeStr); // remove ;
        if (Utils.searchArray(SYSTEM_GLOBAL, name)) { //This has to be here and not in second pass because is is used in sceondPass in other classes
            altName = SKETCH_VARIABLE_PREFIX + name;
        }
    }

    void setStartValue(Expression expr) {
        startValue = expr;
    }

    public void secondPass() {
        if (startValue != null) {
            startValue.secondPass();
        }

    }
//    public void getTypeAndNamefromBegStr(String begstr) {
//
//        String[] begstrarr = begstr.split(" ", 2);
//
//        //The five following lines handles the unusual float[]nodeX
//        final String firstStr = begstrarr[0].trim();
//        int lastBracket = firstStr.lastIndexOf(']');
//        if (lastBracket > 0 && lastBracket != firstStr.length() - 1) {
//            type = firstStr.substring(0, lastBracket + 1);
//            name = firstStr.substring(lastBracket + 1);
//            System.out.println("getTypeAndNamefromBegStr: begstrarr = " + Arrays.toString(begstrarr) + ", type: '" + type + "', name: '" + name + "'");
//        } else {
//            type = begstrarr[0].trim();
//            name = begstrarr[1].trim();
//            if (name.length() > 2 && name.substring(0, 2).equals("[]")) {
//                type += "[]";
//                name = name.substring(2).trim();
//            }
//        }
//    }
    
    public void getTypeAndNamefromBegStr(String begstr) {
        String[] begstrarr = begstr.split(" ", 2);
            type = begstrarr[0].trim();
            name = begstrarr[1].trim();
            if (name.length() > 2 && name.substring(0, 2).equals("[]")) {
                type += "[]";
                name = name.substring(2).trim();
            }
        }

//    GlobalVariable(SyntaxNode parent,String type, String name, StringBuilder codeStr) {
//        super(parent,name, type);
//        char nc = Utils.trimConsumeChartrim(codeStr);
//        if (nc == '=') {
//            startValue = Expression.factory(this,codeStr);
//        }
//        Utils.trimDeleteChartrim(codeStr); // remove ;
//
////        int end = codeStr.indexOf(";");
////        rest = Utils.consume(codeStr, end);
////        Utils.trimLeft(codeStr);
////        codeStr.deleteCharAt(0); // remove ;
////        Utils.trimLeft(codeStr);
////        Debug.trace("Variable name=" + name + ", type=" + type+ ", rest=" + rest);
//    }
    public Expression getStartValue() {
        return startValue;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getNodeName() {
        return NODE_NAME;
    }

    public String getAltName() {

        if (altName != null) {
            return altName;
        }
        return name;
    }

    @Override
    public String toString() {
        return NODE_NAME + "{" + "name=" + name + ", type=" + type + ", startValue=" + startValue + '}';
    }

    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(getNodeName() + ": name=" + name + ", type='" + type + "' getAltName(): " + getAltName());

        if (startValue != null) {
            DefaultMutableTreeNode exprnode = startValue.getTreeNode();
            node.add(exprnode);
        }
        return node;

    }

    public String getP5jsCode() {
        String assignCode = "";
        if (startValue != null && (startValue instanceof Literal)) {
            assignCode = " = " + startValue.getP5jsCode();
        } else if (isNumeric()) {
            assignCode = " = 0";
        }
        return "let " + getAltName() + assignCode + ";\n";
    }

}
