/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.statements;

import processing2js.expressions.FunctionCall;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import processing2js.Debug;
import processing2js.SyntaxErrorException;
import processing2js.SBUtils;
import processing2js.expressions.Assignment;
import processing2js.expressions.Expression;
import processing2js.expressions.UnaryPostfixOperator;
import processing2js.expressions.UnaryPrefixOperator;
import processing2js.expressions.VariableReference;
import processing2js.funcvar.Function;

/**
 *
 * @author dahjon
 */
abstract public class Statement extends SyntaxNode {

    String firstIdentifier;
    final static String FUNCTION_CALL_END_STRING = "(";

    //final static String ASSIGN_END_STRING = "=";
    // static String[] END_STRINGS = {FUNCTION_CALL_END_STRING, ASSIGN_END_STRING, "++", "--", "]"};
    public Statement(SyntaxNode parent) {
        super(parent);
    }

    static Statement factory(SyntaxNode parent, StringBuilder codeStr) throws SyntaxErrorException {
        SBUtils.trimAndRemoveSemicolon(codeStr);
        Debug.tracePrio("->factory codeStr:\n" + SBUtils.debugStr(codeStr));
        handlePreload(parent, codeStr);
        Statement stm = Return.getInstanceIfYou(parent, codeStr);
        if (stm != null) {
            return stm;

        } else if ((stm = JsCodeLine.getInstanceIfYou(parent, codeStr)) != null) {
            return stm;

//        } else if ((stm = Comment.getInstanceIfYou(parent, codeStr)) != null) {
//            return stm;
        } else if ((stm = LocalVariable.getInstanceIfYou(parent, codeStr)) != null) {
            return stm;
        } else if ((stm = IfStatement.getInstanceIfYou(parent, codeStr)) != null) {
            return stm;
        } else if ((stm = While.getInstanceIfYou(parent, codeStr)) != null) {
            return stm;
        } else if ((stm = Foreach.getInstanceIfYou(parent, codeStr)) != null) { //Foreach Must be befor for
            return stm;
        } else if ((stm = For.getInstanceIfYou(parent, codeStr)) != null) {
            return stm;
        } else if ((stm = Switch.getInstanceIfYou(parent, codeStr)) != null) {
            return stm;
        } else if ((stm = Break.getInstanceIfYou(parent, codeStr)) != null) {
            return stm;
        } else if ((stm = Case.getInstanceIfYou(parent, codeStr)) != null) {
            return stm;
        } else if ((stm = Default.getInstanceIfYou(parent, codeStr)) != null) {
            return stm;
        } else if ((stm = FunctionCall.getInstanceIfYou(parent, codeStr)) != null) {
            return stm;
        } else if ((stm = UnaryPostfixOperator.getInstanceIfYou(parent, codeStr)) != null) {
            return stm;
        } else if ((stm = UnaryPrefixOperator.getInstanceIfYou(parent, codeStr)) != null) {
            return stm;
        } else if (codeStr.charAt(0) == '{') {
            return new Block(parent, codeStr);

        } else {
            stm = VariableReference.getInstanceIfYou(codeStr, parent);
            SBUtils.trimLeft(codeStr);
            if (stm != null) {
                return Assignment.getInstanceIfAssignemnt(codeStr, parent, (VariableReference) stm);

            }

//            ArrayList<String> endStrings = new ArrayList();
//            endStrings.add(FUNCTION_CALL_END_STRING);
//            endStrings.addAll(Arrays.asList(Assignment.ASSIGNMENT_OPERATOR_STRINGS));
//            endStrings.add("++");
//            endStrings.add("--");
//            endStrings.add("[");
//            endStrings.add(".");
//            String firstIdentifier = SBUtils.consumeBefore(codeStr, endStrings);
//            Debug.trace("Statement::factory firstIdentifier = '" + firstIdentifier + "'");
//            firstIdentifier = firstIdentifier.trim();
//            Debug.trace("Statement::factory codeStr:\n" + codeStr);
////        codeStr = new StringBuilder(codeStr.toString().trim());
//            SBUtils.trimLeft(codeStr);
//            String endStr = SBUtils.findFirstString(codeStr, endStrings);
//            Debug.trace("Statement::factory endStr = " + endStr);
//            if (endStr.equals(".") || endStr.equals("[")) {
//                return new VariableReference(parent, firstIdentifier, codeStr);
            //} else if (endStr.equals(FUNCTION_CALL_END_STRING)) {
            //Debug.trace("if (endStr == FUNCTION_CALL_END_STRING) firstIdentifier = " + firstIdentifier);
//                if (firstIdentifier.equals("if")) {
//                    return new IfStatement(parent, codeStr);
//
//                } else if (firstIdentifier.equals("while")) {
//                    return new While(parent, codeStr);
//
//                } else if (firstIdentifier.equals("for")) {
//                    return new For(parent, codeStr);
//
//                } else {
            //return new FunctionCall(parent, firstIdentifier, codeStr, true);
//                }
//            } else {
//                if (firstIdentifier.contains(" ")) {
//                    String[] typename = firstIdentifier.split(" ");
//                    return new LocalVariable(parent, typename[0], typename[1], codeStr);
//                } else {
//                String operator = SBUtils.getStringThatIsEgualToBeginning(codeStr, Assignment.ASSIGNMENT_OPERATOR_STRINGS);
////                    if (codeStr.charAt(0) == '=') {
//                if (operator != null) {
//
//                    return new Assignment(parent, firstIdentifier, operator, codeStr);
//                }
//                else {
//                    return new UnaryPostfixOperator(parent, firstIdentifier, codeStr);
//                }
            //    }
//            }
        }
//        switch (endStr) {
//            case FUNCTION_CALL_END_STRING:
//                return new FunctionCall(firstIdentifier, codeStr, true);
//            case ASSIGN_END_STRING:
//                return new Assignment(firstIdentifier, codeStr);
//
//            default:
//                throw new AssertionError();
//        }
        return null;
    }

    public boolean isFunctionWithName(String name) {
        if (this instanceof FunctionCall) {
            FunctionCall o2 = (FunctionCall) this;
            if (o2.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAssignmentOf(String name) {
        if (this instanceof Assignment) {
            Assignment vn = (Assignment) this;
            VariableReference vta = vn.getVariableToAssign();
            if (vta.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    

    static void handlePreload(SyntaxNode parent, StringBuilder codeStr) {
        if (parent instanceof Function) {
            Function func = (Function) parent;
            if (func.getName().equals("setup")) {
                boolean move = false;
                if (SBUtils.consumeRegEx(codeStr, "//preload") != null) {
                    move = true;
                } else {
                    Statement stm = func.getLastStatement();
                    if (stm != null && stm instanceof Assignment) {
                        Expression expr = ((Assignment) stm).getAssignExtr();
                        if (expr instanceof FunctionCall) {
                            final String fn = ((FunctionCall) expr).getName();
                            if (fn.equals("loadImage") || fn.equals("loadFont") || fn.equals("new SoundFile")) {
                                move = true;
                            }
                        }
                    }
                }
                if (move) {
                    Statement stm = func.popLastStatement();
                    parent.getGlobal().addToPreload(stm);
                    SBUtils.trimLeft(codeStr);
                    //JOptionPane.showMessageDialog(null, "Handle preload:" + codeStr);
                }

            }
        }
    }

    public static void main(String[] args) throws SyntaxErrorException {
        StringBuilder codeStr = new StringBuilder(""
                + "   ellipse(i,i,i,i);\n"
                + "   i=i-1;\n"
        );
        Statement firstStmt = factory(null, codeStr);
        Debug.trace("firstStmt = " + firstStmt);
        Debug.trace("Före andra stmt codeStr = " + codeStr);
        Statement secondStmt = factory(null, codeStr);
        Debug.trace("secondStmt = " + secondStmt);

    }

    public DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(this.toString());
        return node;

    }

    public abstract String getP5jsCode();

    public static void consumeStatementsUntilBrace(SyntaxNode parent, StringBuilder codeStr, ArrayList<Statement> statements) throws SyntaxErrorException {
        Debug.trace("->consumeStatementsUntilBrace codeStr = " + codeStr);
        char nextChar = codeStr.charAt(0);
        if (codeStr.length() == 0) {
            throw new SyntaxErrorException("Unexpected end of file when } was expected, directly when trying to get statement to construct " + parent.getClass().getName(), codeStr);
        }
        while (nextChar != '}') {
            SBUtils.trimLeft(codeStr);
            Statement stm = Statement.factory(parent, codeStr);
            Debug.trace("consumeStatementsUntilBrace stm:\n " + stm);
            statements.add(stm);
            SBUtils.trimLeft(codeStr);
            Debug.traceLim("consumeStatementsUntilBrace innan deleteChartrim borde vara ;  codeStr = " + codeStr);
            if (codeStr.length() == 0) {
                throw new SyntaxErrorException("Unexpected end of file when } was expected, when trying to get statement to construct " + parent.getClass().getName(), codeStr);
            }
            Debug.trace("consumeStatementsUntilBrace innan deleteChartrim borde vara ;  codeStr.charAt(0) = " + codeStr.charAt(0));

            if (codeStr.charAt(0) == ';') {
                SBUtils.deleteChartrim(codeStr);
                Debug.trace("consumeStatementsUntilBrace Tar bort semikolon");
            }
            nextChar = codeStr.charAt(0);
            Debug.trace("consumeStatementsUntilBrace sist i loop nextChar = " + nextChar);
            if (nextChar == ')') {
                throw new SyntaxErrorException("consumeStatementsUntilBrace sist i loop. Nästa tecken är ), vilket den aldrig ska vara", codeStr);
            }

        }
    }

}
