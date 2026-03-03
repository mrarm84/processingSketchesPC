/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.funcvar;

import processing2js.statements.Parameter;
import processing2js.statements.VariableDefinition;
import processing2js.statements.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import processing.core.PApplet;
import processing2js.Debug;
import processing2js.Global;
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;
import processing2js.Utils;
import processing2js.expressions.Expression;
import processing2js.expressions.FunctionCall;
import processing2js.expressions.Literal;
import processing2js.statements.Block;
import processing2js.statements.SyntaxNode;

/**
 *
 * @author dahjon
 *
 * This class is currently not used!!!!
 */
public class BuiltInFunction extends Function {


    //private Parameter[] parameters;
    String altName = null;

    public static final String[][][] BUILT_IN_FUNCTIONS = {
        {{"void", "size", "createCanvas"}, {"int", "int"}},
        {{"void", "size", "createCanvas"}, {"int", "int", "String"}},
        {{"void", "println", "console.log"}, {"*"}},
        {{"void", "noLoop", ""}, {}},
        {{"void", "JOptionPane_showInputDialog", "prompt"}, {"String"}},
        {{"void", "ellipse"}, {"float", "float"}},
        {{"void", "ellipse"}, {"float", "float", "float"}},
        {{"void", "rect"}, {"float", "float"}},
        {{"void", "rect"}, {"float", "float", "float"}},
        {{"void", "rect"}, {"float", "float", "float", "float"}},
        {{"void", "rect"}, {"float", "float", "float", "float", "float", "float"}},
        {{"void", "line"}, {"float", "float", "float", "float"}},
        {{"void", "line"}, {"int", "int", "int", "int"}},
        {{"color", "get"}, {"int", "int"}},
        {{"color", "color"}, {"float", "float", "float"}},
        {{"color", "color"}, {"float", "float", "float", "int"}},
        {{"color", "color"}, {"color"}},
        {{"color", "color"}, {"int"}},
        {{"color", "color"}, {"int", "int"}},
        {{"void", "background"}, {"float", "float", "float"}},
        {{"void", "background"}, {"float", "float", "float", "int"}},
        {{"void", "background"}, {"color"}},
        {{"void", "background"}, {"int"}},
        {{"void", "background"}, {"int", "int"}},
        {{"void", "stroke"}, {"float", "float", "float"}},
        {{"void", "stroke"}, {"float", "float", "float", "int"}},
        {{"void", "stroke"}, {"color"}},
        {{"void", "stroke"}, {"int"}},
        {{"void", "stroke"}, {"int", "int"}},
        {{"void", "noStroke"}, {}},
        {{"void", "fill"}, {"float", "float", "float"}},
        {{"void", "fill"}, {"float", "float", "float", "int"}},
        {{"void", "fill"}, {"color"}},
        {{"void", "fill"}, {"int"}},
        {{"void", "fill"}, {"int", "int"}},
        {{"void", "noFill"}, {}},
        {{"PImage", "loadImage"}, {"String"}},
        {{"void", "keyPressed"}},
        {{"void", "loadPixels"}},
        {{"void", "updatePixels"}},
        {{"void", "set"}, {"int", "int", "color"}},
        {{"void", "set"}, {"int", "int", "PImage"}},
        {{"void", "image"}, {"PImage", "float", "float"}},
        {{"void", "image"}, {"PImage", "float", "float", "float", "float"}},
        {{"void", "hint"}}, //        {{"void",""}},
    //        {{"void",""}},
    //        {"void","point" },
    //        {"void","quad" },
    //        {"void","square"},
    //        {"void","triangle" },
    //        {"boolean","keyPressed" },
    //        {"boolean","","" },
    //        {"boolean","","" },
    //        {"boolean","","" },
    };

    public static final String[] OVERRIDABLE_FUNCTIONS_ETC = {
        "setup", "draw", "keyPressed", "keyPressed", "keyReleased", "keyTyped",
        "Mouse", "mouseClicked", "mouseDragged", "mouseMoved", "mousePressed", "mouseReleased", "mouseWheel", "println"};

    public static void addBuiltInFunctions(SyntaxNode parent, ArrayList<Function> list) {
//        for (int i = 0; i < BUILT_IN_FUNCTIONS.length; i++) {
//            list.add(new BuiltInFunction(parent, BUILT_IN_FUNCTIONS[i][0], new ArrayList()));
//        }
        Class myClass;
        try {
            myClass = Class.forName("processing.core.PApplet");
            java.lang.reflect.Method[] m = myClass.getMethods();
            for (int i = 0; i < m.length; i++) {
                java.lang.reflect.Method method = m[i];
                //System.out.println(method);
                final String name = method.getName();
                if (!Utils.searchArray(OVERRIDABLE_FUNCTIONS_ETC, name)) {
                    //System.out.println(name);
                    String type = method.getReturnType().getName();
                    type = fixTypes(type);
                    final Class<?>[] parameterTypes = method.getParameterTypes();
                    final BuiltInFunction f = new BuiltInFunction(parent, name, type, parameterTypes);
                    list.add(f);
                } else {

                    //System.out.println("Hoppar över: " + name);
                }
//                for (int j = 0; j < parameterTypes.length; j++) {
//                    System.out.println("           " + method.getParameterTypes()[j]);
//                    
//                }
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("Hittat inte PApplet");
        }
        list.add(new BuiltInFunction(parent, "println", "void", new String[]{"*"}));
        list.add(new BuiltInFunction(parent, "JOptionPane_showInputDialog", "void", new String[]{"*"}));

    }

    public BuiltInFunction(SyntaxNode parent, String name, String type, final Class<?>[] parameterTypes) {
        super(parent, name, type);
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            String parType = parameterType.getName();
            parType = fixTypes(parType);
            parameters.add(new Parameter(parent, parType));
            //System.out.println("           " + type);

        }
        altName = FunctionCall.getp5jsName(name);         

    }
    
   public BuiltInFunction(SyntaxNode parent, String name, String type, String[] parameterTypes) {
        super(parent, name, type);
        for (int i = 0; i < parameterTypes.length; i++) {
            String parType = parameterTypes[i];
            parameters.add(new Parameter(parent, parType));

        }
        altName = FunctionCall.getp5jsName(name);         

    }    

    @Override
    public void secondPass() {
    }

    public String getAltName() {
        return altName;
    }

//    public ArrayList<Parameter> getParameters() {
//        return parameters;
//    }
    public boolean hasAltName() {
        return altName != null;
    }

    @Override
    public String toString() {
        String parString = getParameterString();
        return "BuiltInFunction{" + "type=" + type + ", name=" + name + ", altName=" + altName + "parameters:" + parString + '}';
    }

    public String getParameterString() {
        String parString = "";
        for (int i = 0; i < parameters.size(); i++) {
            Parameter par = parameters.get(i);
            parString += " " + par.getType();
            //System.out.println(parString);
        }
        return parString;
    }

    public MutableTreeNode getTreeNode() {
        String altNameStr = "";
        if(altName!=null && !altName.equals(name)){
            altNameStr = ", has altName: "+altName;
        }
        final String BuiltInFunctionNodeStr = "BuiltInFunction Def: type=" + type + " name=" + name + ", parameters:" + getParameterString() + altNameStr;
        //System.out.println("BuiltInFunctionNodeStr = " + BuiltInFunctionNodeStr);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(BuiltInFunctionNodeStr);

        return node;

    }

    public String getP5jsCode(Global global) {

        return "";
    }

    public static String fixTypesBuiltIn(String str) {
        if (str.length() >= 2 && str.charAt(0) == '[') {
            switch (str.charAt(1)) {
                case 'C':
                    return "Character";
                case 'I':
                    return "Integer";
                case 'F':
                    return "Float";
                case 'B':
                    return "Byte";
            }
        }
        return str;
    }

    public static String fixTypes(String str) {
        str = fixTypesBuiltIn(str);
        String[] strArr = str.split("\\.");
        return strArr[strArr.length - 1];
    }

    public static void main(String[] args)
            throws ClassNotFoundException {
        // returns the Class object for this class 
        Class myClass = Class.forName("processing.core.PApplet");

        //System.out.println("Class represented by myClass: "   + myClass.toString());

        // Get the methods of myClass 
        // using getMethods() method 
        System.out.println("Methods of myClass: ");

        java.lang.reflect.Method[] m = myClass.getMethods();
        for (int i = 0; i < m.length; i++) {
            java.lang.reflect.Method method = m[i];
            System.out.println(method);
            System.out.println(method.getName());
            final String type = method.getReturnType().getName();
            System.out.println("type = " + type);
            for (int j = 0; j < method.getParameterTypes().length; j++) {
                System.out.println("           " + method.getParameterTypes()[j].getName());
            }
        }

    }
}
