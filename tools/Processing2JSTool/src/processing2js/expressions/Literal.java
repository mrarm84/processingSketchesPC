/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.expressions;

import processing2js.Debug;
import processing2js.SBUtils;
import processing2js.statements.SyntaxNode;

/**
 *
 * @author dahjon
 */
public class Literal extends Expression {

    private String value;
    private String type;

//    public Literal(String value) {
//        this.value = value;
//    }
    public static Literal getInstanceIfYou(SyntaxNode parent, StringBuilder codeStr) {

        char nextChar = codeStr.charAt(0);
        Debug.trace("Literal::getInstanceIfYou nextChar = " + nextChar);
        if (Character.isDigit(nextChar) || nextChar == '.' || nextChar == '"' || nextChar == '\'') {
            return new Literal(parent, codeStr);
        } else {
            return null;
        }
    }

    public static String consumeNumberString(StringBuilder codeStr) {
        String ret = "";
        char c = codeStr.charAt(0);
        int i = 0;
        boolean number = false;
        while (i < codeStr.length() && (Character.isDigit(c) || c == '.')) {
            ret += c;
            i++;
            c = codeStr.charAt(i);
            number = true;
            //Debug.trace("consumeNumberString i = " + i + ", c = " + c);
        }

        codeStr.delete(0, i);
        SBUtils.trimLeft(codeStr);
        if (codeStr.charAt(0) == 'f') {
            SBUtils.trimConsumeChartrim(codeStr);
        }
        Debug.trace("consumeNumberString ret = " + ret + "codeStr: " + codeStr);
        return ret;
    }

    Literal(SyntaxNode parent, StringBuilder codeStr) {
        super(parent);

        SBUtils.trimLeft(codeStr);
        int nextChar = codeStr.charAt(0);
        Debug.trace("Literal::Literal nextChar = " + nextChar);
        if (nextChar == '"') {
            codeStr.deleteCharAt(0);
            value = SBUtils.consumeIncluding(codeStr, "\"");
            while (value.length()>2 && value.charAt(value.length() - 2) == '\\') {
                value += SBUtils.consumeIncluding(codeStr, "\"");

            }

            value = "\"" + value;

            type = "String";
        } else if (nextChar == '\'') {
            value = codeStr.substring(0, 3);
            codeStr.delete(0, 3);
            type = "char";
        } else {
            value = consumeNumberString(codeStr);
            if (value.contains(".")) {
                type = "float";
            } else {
                type = "int";
            }
        }
        Debug.trace("Literal::Literal value = " + value);
    }

    public void secondPass() {

    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Literal{" + "value=" + value + ", type:" + getType() + '}';
    }

    public String getP5jsCode() {
        return value;
    }

    public String getValue() {
        return value;
    }

}
