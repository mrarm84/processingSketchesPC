/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js;

/**
 *
 * @author dahjon
 */
public class Debug {

    
    
    static int traceLevel = 0;
    
    public static final int PRIO_TRACE = 1;
    public static final int NORMAL_TRACE = 2;
    public static final int EXTENDED_TRACE = 3;
    
    public static boolean isDebugOn() {
        return traceLevel>0;
    }

    public static void trace(Object obj) {
        trace(NORMAL_TRACE, obj.toString());
    }
    
    public static void traceLim(Object obj) {
        traceLim(NORMAL_TRACE, obj.toString());
    }
    
   public static void traceLim(int level, String str) {
        if (traceLevel >= level) {
            StackTraceElement e = Thread.currentThread().getStackTrace()[3];
            final String tracestr = e.getFileName() + ":" + e.getLineNumber() + " " + str.substring(0,Math.min(100, str.length()));
            System.out.println(tracestr);

        }
    }
   
   public static void trace(int level, String str) {
        if (traceLevel >= level) {
            StackTraceElement e = Thread.currentThread().getStackTrace()[3];
            final String tracestr = e.getFileName() + ":" + e.getLineNumber() + " " + str;
            System.out.println(tracestr);

        }
    }

    public static void tracePrio(String str) {
        trace(PRIO_TRACE, str);
    }

    public static void traceExtended(String str) {
        trace(EXTENDED_TRACE, str);
    }
    

}
