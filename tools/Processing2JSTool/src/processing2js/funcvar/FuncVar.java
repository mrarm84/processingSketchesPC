/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.funcvar;

import processing2js.statements.VariableDefinition;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import processing2js.Debug;
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;
import processing2js.statements.Parameter;
import processing2js.statements.SyntaxNode;

/**
 *
 * @author dahjon
 */
public abstract class FuncVar extends SyntaxNode {

    protected String name;
    protected String type;
    ArrayList<Parameter> parameters = new ArrayList<>();

    public FuncVar(SyntaxNode parent, String name, String type) {
        super(parent);
        this.name = name;
        this.type = type;
    }

    public FuncVar(SyntaxNode parent) {
        super(parent);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public static SyntaxNode factory(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {

        Debug.trace("FuncVar::factory codeStr:\n " + codeStr);
        SyntaxNode fv = ClassNode.getInstanceIfYou(parent, codeStr);
        if (fv != null) {
            return fv;
        }if ((fv=VariableDefinition.getInstanceIfYou(parent, codeStr) )!= null) {
            return fv;
        }if ((fv=Function.getInstanceIfYou(parent, codeStr) )!= null) {
            Debug.trace("factory har skapat en funktion");
            return fv;
        } else {
        }if ((fv=JsVariable.getInstanceIfYou(parent, codeStr) )!= null) {
            Debug.trace("factory har skapat en funktion");
            return fv;
        } else {
            throw new SyntaxErrorException( "Unknown statement in the global level: ", codeStr);
            
            //return null;
        }
            
//        else {
//            int firstSpace = codeStr.indexOf(" ");
//            String type = Utils.consume(codeStr, firstSpace).trim();
//            Debug.trace("type = " + type);
//            int firstLeftParen = codeStr.indexOf("(");
//            int firstEquals = codeStr.indexOf("=");
//            String name;
//            if (firstEquals != -1 && firstEquals < firstLeftParen) {
//                name = Utils.consume(codeStr, firstEquals).trim();
//                return new GlobalVariable(parent, type, name, codeStr);
//            } else if (firstLeftParen != -1) {
//                name = Utils.consume(codeStr, firstLeftParen).trim();
//                //Debug.trace("FuncVar factory name = " + name);
//                return new Function(parent, type, name, codeStr);
//            }
//            return null;
//        }
    }

    @Override
    public String toString() {
        return "FuncVar{" + "name=" + name + ", type=" + type + '}';
    }



}
