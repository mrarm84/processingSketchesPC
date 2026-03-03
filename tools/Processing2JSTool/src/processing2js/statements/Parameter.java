/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.statements;

import processing2js.Debug;
import processing2js.SBUtils;
import processing2js.statements.SyntaxNode;
import processing2js.statements.VariableDefinition;

/**
 *
 * @author dahjon
 */
public class Parameter extends VariableDefinition{


    public Parameter(SyntaxNode parent,StringBuilder codeStr) {
        super(parent);
        type = SBUtils.consumeBefore(codeStr, SBUtils.WHITESPACE_STRINGS).trim();
        name = SBUtils.consumeBefore(codeStr, ",", ")").trim();
        Debug.trace("Parameter type = " + type+", name = " + name);
        SBUtils.trimLeft(codeStr);
    }
    
    public Parameter(SyntaxNode parent, String type){
        super(parent);
        this.type = type;
        this.name = "";
    }
    public String getP5jsCode() {
        return name;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Parameter{" + "name=" + name + ", type=" + type + '}';
    }

}
