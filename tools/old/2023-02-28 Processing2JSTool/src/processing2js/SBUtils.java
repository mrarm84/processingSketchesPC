/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author dahjon
 */
public class SBUtils {

    public static final String[] WHITESPACE_STRINGS = {" ", "\n", "\r", "\t"};

    public static int replaceAll(StringBuilder procKod, String serachStr, String replStr) {
        Pattern pattern = Pattern.compile(serachStr);
        Matcher m = pattern.matcher(procKod);
        int end = 0;
        int forandring = 0;
        while (m.find(end)) {
            Debug.trace("replaceAll m.group() = " + m.group());
            procKod.replace(m.start(), m.end(), replStr);
            end = m.start() + replStr.length();
            forandring = (m.end() - m.start()) - replStr.length();
        }
        return forandring;
    }

    public static void trimAndRemoveSemicolon(StringBuilder str) {
        trimLeft(str);
        removeifSemicolon(str);
        trimLeft(str);

    }

    public static String debugStr(StringBuilder sb) {

        return sb.substring(0, Math.min(100, sb.length()));
    }

    public static void removeifSemicolon(StringBuilder str) {
        while (str.length() > 0
                && str.charAt(0) == ';') {
            str.deleteCharAt(0);
        }
    }

    public static void trimLeft(StringBuilder str) {
        while (str.length() > 0
                && (str.charAt(0) == ' ' || str.charAt(0) == '\n' || str.charAt(0) == '\t' || str.charAt(0) == '\r')) {
            str.deleteCharAt(0);
        }
    }

    public static void deleteChartrim(StringBuilder str) {
        if (str.length() > 0) {
            str.deleteCharAt(0);
            trimLeft(str);
        }
    }

    public static void deleteChartrim(StringBuilder str, int antal) {
        if (str.length() > 0) {
            for (int i = 0; i < antal; i++) {
                str.deleteCharAt(0);

            }
            trimLeft(str);
        }
    }

    public static void trimDeleteChartrim(StringBuilder str) {
        if (str.length() > 0) {
            trimLeft(str);
            str.deleteCharAt(0);
            trimLeft(str);
        }
    }

    public static char trimConsumeChartrim(StringBuilder str) {
        if (str.length() > 0) {
            trimLeft(str);
            char ret = str.charAt(0);
            str.deleteCharAt(0);
            trimLeft(str);
            return ret;
        }
        return ' ';
    }

    public static String consume(StringBuilder codeStr, int end) {
        String beg = "";
        if (codeStr.length() >= end) {
            beg = codeStr.substring(0, end);
            codeStr.delete(0, end);
        } else {
            beg = codeStr.toString();
        }
        return beg;
    }

    public static String consumeRegEx(StringBuilder procKod, String serachStr) {
        Pattern pattern = Pattern.compile(serachStr);
        Matcher m = pattern.matcher(procKod);
        if (m.find()) {
            if (m.start() == 0) {
                String fstr = m.group();
                //System.out.println("consumeRegEx m.group() = " + fstr + ", fstr.length(): "+fstr.length());
                Debug.trace("consumeRegEx m.group() = " + fstr + ", fstr.length(): " + fstr.length());
                procKod.delete(0, fstr.length());
                return fstr;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static boolean isBeginningEqualToRegEx(StringBuilder procKod, String serachStr) {
        Pattern pattern = Pattern.compile(serachStr);
        Matcher m = pattern.matcher(procKod);
        if (m.find()) {
            if (m.start() == 0) {
                String fstr = m.group();
                Debug.trace("consumeRegEx m.group() = " + fstr + ", fstr.length(): " + fstr.length());
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean beginsWith(StringBuilder code, String searchStr) {
        return code.substring(0, searchStr.length()).equals(searchStr);
    }

    public static void main(String[] args) {
        StringBuilder str = new StringBuilder("(int)$hej, kalle)");
        String r = "\\([a-zA-Z_$][a-zA-Z0-9_$]*\\) *[a-zA-Z_$][a-zA-Z0-9_$]*";
        Debug.trace(consumeRegEx(str, r));
        str = new StringBuilder("setup(void hej){");
        Debug.trace(consumeRegEx(str, r));
    }

    public static String consumeBefore(StringBuilder codeStr, String endStr) {
        int end = codeStr.indexOf(endStr);
        String beg = codeStr.substring(0, end);
        codeStr.delete(0, end);
        return beg;
    }

    public static String consumeIncluding(StringBuilder codeStr, String endStr) {
        int end = codeStr.indexOf(endStr) + endStr.length();
        String beg = codeStr.substring(0, end);
        codeStr.delete(0, end);
        return beg;
    }

    public static String consumeBefore(StringBuilder codeStr, ArrayList<String> endStrings) {
        return consumeBefore(codeStr, endStrings.toArray(new String[0]));
    }

    public static String consumeBefore(StringBuilder codeStr, String... endStrings) {
        int firstEnd = codeStr.indexOf(endStrings[0]);
        Debug.trace("consumeBefore firstEnd = " + firstEnd + ", endStrings[0] = " + endStrings[0]);
        for (int i = 1; i < endStrings.length; i++) {
            String str = endStrings[i];
            int end = codeStr.indexOf(str);
            if (end != -1) {
                if (firstEnd == -1) {
                    Debug.trace("consumeBefore firstend==-1:   i = " + i + ", end = " + end + ", str = " + str);
                    firstEnd = end;
                } else if (end < firstEnd) {
                    Debug.trace("consumeBefore end < firstEnd:   i = " + i + ", end = " + end + ", str = " + str);
                    firstEnd = end;
                }
            }
            Debug.trace("consumeBefore i = " + i + ", end = " + end + ", str = " + str + " firstEnd = " + firstEnd);
        }
        Debug.trace("consumeBefore efter loop  firstEnd = " + firstEnd);
        if (firstEnd != -1) {
            String beg = codeStr.substring(0, firstEnd);
            codeStr.delete(0, firstEnd);
            return beg;
        } else {
            return null;
        }
    }

    public static String findFirstString(StringBuilder codeStr, String... str) {
        for (int i = 0; i < str.length; i++) {
            String s = str[i];
            if (codeStr.indexOf(s) == 0) {
                return s;
            }
        }
        return null;
    }

    public static String findFirstString(StringBuilder codeStr, ArrayList<String> str) {
        for (int i = 0; i < str.size(); i++) {
            String s = str.get(i);
            if (codeStr.indexOf(s) == 0) {
                return s;
            }
        }
        return null;
    }

    //    public VariableReference(String name) {
    //        this.name = name;
    //        Debug.trace("VariableReference name = " + name);
    //
    //    }
    public static String consumeIdentifierName(StringBuilder codeStr) {
        String name = "";
        boolean first = true;
        int i = 0;
        char c = codeStr.charAt(i);
        while (Utils.isValidIdentifierNamechar(c, first)) {
            name += c;
            first = false;
            i++;
            c = codeStr.charAt(i);
        }
        codeStr.delete(0, i);
        return name;
    }

    public static int consumeInteger(StringBuilder codeStr) {
        String numStr = "";
        int i = 0;
        char c = codeStr.charAt(i);
        while (Character.isDigit(c)) {
            numStr += c;
            i++;
            c = codeStr.charAt(i);
        }
        codeStr.delete(0, i);
        int num = Integer.parseInt(numStr);
        return num;
    }

    public static boolean isBeginningEqualToString(StringBuilder codeStr, String str) {
        boolean ret = false;
        if (codeStr.length() >= str.length()) {
            ret = str.equals(codeStr.substring(0, str.length()));
        }
        return ret;
    }

    public static boolean isBeginningEqualToStringThenRemoveAndTrim(StringBuilder codeStr, String str) {
        boolean isEqual = isBeginningEqualToString(codeStr, str);
        if (isEqual) {
            deleteChartrim(codeStr, str.length());
        }
        return isEqual;
    }

    public static String getStringThatIsEgualToBeginning(StringBuilder codeStr, String[] cmdStr) {
        String retval = null;
        for (int i = 0; (retval == null) && (i < cmdStr.length); i++) {
            String str = cmdStr[i];
            if (SBUtils.isBeginningEqualToString(codeStr, str)) {
                retval = str;
            }
        }
        return retval;
    }

}
