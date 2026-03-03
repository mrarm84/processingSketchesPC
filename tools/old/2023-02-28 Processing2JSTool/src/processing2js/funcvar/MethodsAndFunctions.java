/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.funcvar;

import java.util.ArrayList;
import processing2js.Debug;
import processing2js.SBUtils;
import processing2js.statements.Parameter;
import processing2js.statements.Statement;
import processing2js.statements.SyntaxNode;

/**
 *
 * @author dahjon
 */
abstract public class MethodsAndFunctions extends FuncVar{

    public static String getBegStr(StringBuilder codeStr) {
        String begstr = SBUtils.consumeRegEx(codeStr, "[a-zA-Z_$][a-zA-Z_$0-9]*\\s*[\\[\\]]*\\s+[a-zA-Z_$][a-zA-Z_$0-9]*+\\s*\\(");
        return begstr;
    }
    protected ArrayList<Parameter> parameters = new ArrayList<>();
    protected ArrayList<Statement> statements = new ArrayList<>();
    
    public MethodsAndFunctions(SyntaxNode parent, String name, String type) {
        super(parent, name, type);
    }

    public MethodsAndFunctions(SyntaxNode parent) {
        super(parent);
    }
    abstract public ArrayList<Parameter> getParameters();

    public void extractParameters(StringBuilder codeStr) {
        SBUtils.trimLeft(codeStr);
        char nextChar = codeStr.charAt(0);
        Debug.trace("Function nextChar = " + nextChar);
        if (nextChar != ')') {
            //Åtminstone en parameter
            nextChar = ',';
            while (nextChar == ',') {
                if (codeStr.charAt(0) == ',') {
                    codeStr.deleteCharAt(0);
                }
                SBUtils.trimLeft(codeStr);
                Parameter param = new Parameter(this, codeStr);
                parameters.add(param);
                nextChar = codeStr.charAt(0);
            }
        }
        codeStr.deleteCharAt(0); //Remove (
        SBUtils.trimLeft(codeStr);
        codeStr.deleteCharAt(0); //Remove {
        SBUtils.trimLeft(codeStr);
    }
    public void getNameAndTypeFromBegStr(String begstr) {
        String[] begstrarr = begstr.split(" ", 2);
        type = begstrarr[0].trim();
        name = begstrarr[1].trim();
    }
    public void secondPass() {
        for (int i = 0; i < parameters.size(); i++) {
            parameters.get(i).secondPass();
        }
        for (int i = 0; i < statements.size(); i++) {
            statements.get(i).secondPass();
        }
    }
}
