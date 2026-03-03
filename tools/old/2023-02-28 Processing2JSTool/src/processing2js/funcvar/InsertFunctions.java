/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.funcvar;


/**
 *
 * @author Jonathan
 */
public class InsertFunctions {

    //Function creatwTwoDimentionalArray = new Function("")
//    function processing2p5jsNew2DArray(var x, y){
//    var arr = new Array(x);
//    for (var i = 0; i < x; i++) {
//       arr[i] = new Array(y);
//    }
    final static public String ArrayListRemove
            = "function processing2p5jsArrayListRemove(arr, index){\n"
            + "  if (typeof(index) === 'number') {\n"
            + "    var retVal = arr[index];\n"
            + "    arr.splice(index,1);\n"
            + "    return retVal;\n"
            + "    \n"
            + "  }\n"
            + "  else{\n"
            + "    for(var i =0; i < arr.length ; i++){\n"
            + "      if(arr[i]==index){\n"
            + "        var retVal = arr[i];\n"
            + "        arr.splice(i,1);\n"
            + "        return retVal;\n"
            + "      }\n"
            + "    }\n"
            + "  }\n"
            + "}\n";

    final static public String NewNumericArray
            = "function processing2jsNewNumericArray(x){\n"
            + "    let arr = new Array(x);\n"
            + "    for (var i = 0; i < x; i++) {\n"
            + "       arr[i] = 0;\n"
            + "    }\n"
            + "    return arr;\n"
            + "}\n";
    final static public String New2DArray
            = "function processing2jsNew2DArray(x,y){\n"
            + "    var arr = new Array(x);\n"
            + "    for (var i = 0; i < x; i++) {\n"
            + "       arr[i] = new Array(y);\n"
            + "    }\n"
            + "    return arr;\n"
            + "}\n";
    final static public String New2DNumericArray
            = "function processing2jsNew2DNumericArray(x,y){\n"
            + "    var arr = new Array(x);\n"
            + "    for (var i = 0; i < x; i++) {\n"
            + "        arr[i] = new Array(y);\n"
            + "        for (var j = 0; j < y; j++) {\n"
            + "            arr[i][j] = 0;"
            + "        }\n"
            + "    }\n"
            + "    return arr;\n"
            + "}\n";
    final static public String NewNDArray
            = "function processing2jsNewNDimArray(dimensions) {\n"
            + "    if (dimensions.length > 0) {\n"
            + "        let dim = dimensions[0];\n"
            + "        let rest = dimensions.slice(1);\n"
            + "        let newArray = new Array();\n"
            + "        for (var i = 0; i < dim; i++) {\n"
            + "            newArray[i] = processing2jsNewNDimArray(rest);\n"
            + "        }\n"
            + "        return newArray;\n"
            + "     } else {\n"
            + "        return undefined;\n"
            + "     }\n"
            + " }\n";
    final static public String NewNumericNDArray
            = "function processing2jsNewNumericNDimArray(dimensions) {\n"
            + "    if (dimensions.length > 0) {\n"
            + "        var dim = dimensions[0];\n"
            + "        var rest = dimensions.slice(1);\n"
            + "        var newArray = new Array();\n"
            + "        for (var i = 0; i < dim; i++) {\n"
            + "            newArray[i] = processing2jsNewNumericNDimArray(rest);\n"
            + "        }\n"
            + "        return newArray;\n"
            + "     } else {\n"
            + "        return 0;\n"
            + "     }\n"
            + " }\n";    
    final static public String New3DArray
            = "function processing2jsNew3DArray(x,y,z){\n"
            + "    var arr = new Array(x);\n"
            + "    for (var i = 0; i < x; i++) {\n"
            + "       arr[i] = new Array(y);\n"
            + "       for (var j = 0; j < y; j++) {\n"
            + "          arr[i][j] = new Array(z);\n"
            + "       }\n"
            + "    }\n"
            + "    return arr;\n"
            + "}\n";
    final static public String CompareColors
            = "function processing2jsCompareGet(c1, c2){\n"
            + "  var cc1 = color(c1);\n"
            + "  var cc2 = color(c2);\n"
            + "  print(\"cmpc c1: \" +c1 + \", c2: \"+c2+\", cc1: \" +cc1 + \", cc2: \"+cc2); \n"
            + "  var retVal = cc1.toString()==cc2.toString();\n"
            + "  print(\"cmpcc retVal: \"+retVal); \n"
            + "  return retVal;\n"
            + "}";
    final static public String delay
            = "function delay( milliseconds){\n"
            + "   var start = new Date().getTime();\n"
            + "   var stop=false;\n"
            + "   while(!stop) {\n"
            + "      if ((new Date().getTime() - start) > milliseconds){\n"
            + "         stop=true;\n"
            + "    }\n"
            + "  }\n"
            + "}";
    final static public String fullScreen
            = "function fullScreen( ){\n"
            + "   createCanvas(displayWidth, displayHeight);\n"
            + "}\n";

    /*final static public String PVectorLine
            = "function processing2p5jsPVectorLine(p1, p2){\n"

    /*final static public String PVectorLine
            = "function processing2p5jsPVectorLine(p1, p2){\n"
            + "  line(p1.x, p1.y, p2.x, p2.y);\n"
            + "  \n"
            + "}";
     */
    public static void insert(StringBuilder procCode, String func) {
        if (procCode.indexOf(func) == -1) {
            procCode.append("\n");
            procCode.append(func);
        }
    }

}
