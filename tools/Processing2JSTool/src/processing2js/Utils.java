/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js;

import java.io.File;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import processing2js.statements.Parameter;
import processing2js.statements.VariableDefinition;

/**
 *
 * @author dahjon
 */
public class Utils {

    public static boolean isValidIdentifierNamechar(int c, boolean isFirst) {
        boolean ret = Character.isAlphabetic(c) || c == '$' || c == '_';
        if (!isFirst) {
            ret = ret || Character.isDigit(c);
        }
        return ret;
    }

    public static String match2dArray(String[][] arr, String orgName) {
        String ret = orgName;
        boolean found = false;
        for (int i = 0; !found && i < arr.length; i++) {
            String[] row = arr[i];
            if (row[0].equals(orgName)) {
                ret = row[1];
                found = true;
            }
        }
        return ret;
    }

    public static boolean searchArray(String[] arr, String searchStr) {
        boolean found = false;
        for (int i = 0; !found && i < arr.length; i++) {
            String row = arr[i];
            if (row.equals(searchStr)) {
                found = true;
            }
        }
        return found;
    }

    public static boolean searchArrayList(ArrayList<String> arr, String searchStr) {
        boolean found = false;
        for (int i = 0; !found && i < arr.size(); i++) {
            String row = arr.get(i);
            if (row.equals(searchStr)) {
                found = true;
            }
        }
        return found;
    }

    public static VariableDefinition searchVariableArrayList(ArrayList<VariableDefinition> arr, String searchStr) {
        boolean found = false;
        VariableDefinition var = null;
        if (arr != null) {
            for (int i = 0; !found && i < arr.size(); i++) {
                String name = arr.get(i).getName();
                if (name.equals(searchStr)) {
                    found = true;
                    var = arr.get(i);
                }
            }
        }
        return var;
    }
    public static VariableDefinition searchParameterArrayList(ArrayList<Parameter> arr, String searchStr) {
        boolean found = false;
        VariableDefinition var = null;
        if (arr != null) {
            for (int i = 0; !found && i < arr.size(); i++) {
                String name = arr.get(i).getName();
                if (name.equals(searchStr)) {
                    found = true;
                    var = arr.get(i);
                }
            }
        }
        return var;
    }

    public static boolean isEvenNumberOfQuotationMarksBeforeInLine(StringBuilder codeStr, int pos) {
        return isEvenNumberOfOccurrencesBeforeInLine(codeStr, '\"', pos);
    }

    public static boolean isEvenNumberOfOccurrencesBeforeInLine(StringBuilder codeStr, char searchChar, int pos) {
        int count = countNumberOfOccurrencesBeforeInLine(codeStr, searchChar, pos);
        int countMod = count % 2;
        Debug.trace("countMod = " + countMod);
        boolean ret = countMod == 0;
        return ret;
    }

    public static int countNumberOfOccurrencesBeforeInLine(StringBuilder codeStr, char searchChar, int pos) {
        int count = 0;
        while (pos > 0 && codeStr.charAt(pos) != '\n') {
            pos--;
            if (codeStr.charAt(pos) == searchChar) {
                count++;
            }
        }
        return count;
    }

    public static void removeNotJsLine(StringBuilder codeStr) {

        Debug.trace("->removeNotJs// codeStr =´ " + codeStr);
        final String token = "//notjs";
        int pos = codeStr.indexOf(token);
        while (pos < codeStr.length() && pos != -1) {
            char nextChar = codeStr.charAt(pos + token.length());
            if (nextChar == ' ' || nextChar == '\n') {
                int prevEol = codeStr.lastIndexOf("\n", pos);
                Debug.trace("removeNotJsLine pos = " + pos + ", prevEol = " + prevEol);
                codeStr.delete(prevEol, pos);
            } else {
                pos += token.length();
            }
            pos = codeStr.indexOf("//", pos + token.length());

        }
    }

    public static void removeSingleLineComments(StringBuilder codeStr, String... exceptions) {

        Debug.trace("->removeSingleLineComments// codeStr =´ " + codeStr);
        //codeStr.append("\n");
        int pos = codeStr.indexOf("//");
        while (pos < codeStr.length() && pos != -1) {
            boolean skip = false;
                    Debug.trace("removeSingleLineComments before if(isEven... pos: "+pos+", codeStr(pos) = " + codeStr.substring(pos));

            if (isEvenNumberOfQuotationMarksBeforeInLine(codeStr, pos)) {
                int eol = codeStr.indexOf("\n", pos);
                for (int i = 0; i < exceptions.length; i++) {
                    String exception = exceptions[i];
                    final int exceptionEndPos = pos + 2 + exception.length();
                    if (exceptionEndPos < codeStr.length()) {
                        final String ss = codeStr.substring(pos + 2, exceptionEndPos);
                        Debug.trace("removeSingleLineComments ss = '" + ss + "', exception = " + exception + "', codeStr.length() = " + codeStr.length());

                        if (exception.equals(ss)) {
                            skip = true;
                            pos = pos + 2 + exception.length();
                        }
                    }
                }
                Debug.trace("removeSingleLineComments pos = " + pos + ", eol = " + eol + ", skip = " + skip);
                if (!skip) {
                    //System.out.println("Utils.removeSingleLineComments pos = " + pos+", codeStr.length() = " + codeStr.length()+", eol = " + eol);
                    codeStr.delete(pos, eol);
                }
            }
            else {
                pos++;
            }
            pos = codeStr.indexOf("//", pos);
        }
    }

    public static void removeMultiLineComments(StringBuilder codeStr) {
        Debug.trace("->removeMultiLineComments/* codeStr =´ " + codeStr);
        //codeStr.append("\n");
        int pos = codeStr.indexOf("/*");
        while (pos < codeStr.length() && pos != -1) {
            if (isEvenNumberOfQuotationMarksBeforeInLine(codeStr, pos)) {
                int eol = codeStr.indexOf("*/", pos);
                Debug.trace("removeMultiLineComments pos = " + pos + ", eol = " + eol);
                codeStr.delete(pos, eol + 2);
            }
            pos = codeStr.indexOf("/*", pos);
        }
    }

    public static void removeImport(StringBuilder codeStr) {
        Debug.trace("->removeImport codeStr =´ " + codeStr);
        SBUtils.trimLeft(codeStr);
        while (SBUtils.isBeginningEqualToString(codeStr, "import")) {
            int eol = codeStr.indexOf("\n");
            Debug.trace("removeImport eol = " + eol);
            codeStr.delete(0, eol);
            SBUtils.trimLeft(codeStr);
        }
    }

    public static String fixDataPath(File sketchDir, String resPathWithQuote) {
        String resPath = resPathWithQuote.trim().substring(1, resPathWithQuote.length() - 1);
        String newPath = "data" + "/" + resPath;
        File resData = new File(sketchDir, newPath);
        if (resData.exists()) {
            return "\"" + newPath + "\"";
        }
        //JOptionPane.showMessageDialog(null,"sketchDir: "+ sketchDir + "\nnewPath:"+newPath+"\nresData: "+resData );
        return resPathWithQuote;
    }

    public static boolean isStringNumericType(String type) {
        return type.equals("float") || type.equals("double") || type.equals("int") || type.equals("long");
    }
}
