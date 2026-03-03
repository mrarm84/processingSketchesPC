/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.statements;

import java.util.ArrayList;
import processing2js.Debug;
import processing2js.SBUtils;
import processing2js.statements.SyntaxNode;
import processing2js.statements.VariableDefinition;

/**
 *
 * @author dahjon
 */
public class BuiltInVariable extends VariableDefinition {

    private String altName = null;

    final static String[][] BUILT_IN_VARIABLES = {
        {"int", "width"},
        {"int", "height"},
        {"int", "mouseX"},
        {"int", "mouseY"},
        {"int", "pmouseX"},
        {"int", "pmouseY"},
        {"int", "mouseButton"},
        {"boolean ", "mousePressed", "mouseIsPressed"},
        {"char ", "key"},
        {"int", "keyCode"},
        {"boolean ", "keyPressed", "keyIsPressed"},
        {"boolean ", "focused "},
        {"float ", "frameRate"},
        {"int", "frameCount"},
        {"int", "ESC", "ESCAPE"},
        {"int", "LEFT", "LEFT_ARROW"},
        {"int", "RIGHT", "RIGHT_ARROW"},
        {"int", "UP", "UP_ARROW"},
        {"int", "DOWN", "DOWN_ARROW"},
        {"float", "THIRD_PI", "(PI/3)"},
        {"float", "PI"},
        {"float", "HALF_PI"},
        {"float", "QUARTER_PI "},
        {"float", "TWO_PI "},
        {"String", "P3D", "WEBGL"},
        {"String", "P2D"},
        {"int", "BACKSPACE"},
        {"int", "TAB"},
        {"int", "ENTER"},
        {"int", "RETURN"},
        {"int", "CONTROL"},
        {"int", "ALT"},
        {"int", "RGB"},
        {"int", "ARGB"},
        {"int", "ALPHA"},
         
        {"PVector","PVector", "p5.Vector"},//This is actially a class
    };

    public static void addBuiltInVariables(SyntaxNode parent, ArrayList<VariableDefinition> list) {
        for (int i = 0; i < BUILT_IN_VARIABLES.length; i++) {
            list.add(new BuiltInVariable(parent, BUILT_IN_VARIABLES[i]));
        }
    }

    public BuiltInVariable(SyntaxNode parent, String[] arr) {
        super(parent);
        type = arr[0].trim();
        name = arr[1].trim();
        if (arr.length >= 3) {
            altName = arr[2];
        }
        NODE_NAME = "BuiltInVariable";
    }

    public BuiltInVariable(SyntaxNode parent, StringBuilder codeStr) {
        super(parent);
    }

    public String getP5jsCode() {
        return "";
    }

    public String getName() {
        return name;
    }

    public String getAltName() {
        if (altName == null) {
            return name;
        } else {
            return altName;
        }
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "BuiltInVariable{" + "name=" + name + ", type=" + type + "altName=" + altName + '}';
    }

}
