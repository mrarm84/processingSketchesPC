/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing2js.statements;

import java.util.ArrayList;
import processing2js.Debug;
import processing2js.Global;
import processing2js.SBUtils;
import processing2js.funcvar.ClassNode;
import processing2js.funcvar.Function;
import processing2js.funcvar.Method;

/**
 *
 * @author dahjon
 */
public abstract class SyntaxNode {
    protected SyntaxNode parent=null;

    public SyntaxNode(SyntaxNode parent) {
        this.parent = parent;
    }
    public abstract void secondPass();

    public SyntaxNode getParent() {
        return parent;
    }
    
    public Global getGlobal(){
        //Debug.trace("getGlobal-> parent: "+parent);
        SyntaxNode node = parent;
        while(node.getParent()!=null){
            node = node.getParent();
            //Debug.trace("node = " + node);
        }
        return (Global)node;
    }
    public int indetionDept(){
        Debug.trace("getGlobal-> indetionDept: "+parent);
        SyntaxNode node = parent;
        int indept=0;
        while(node.getParent()!=null){
            node = node.getParent();
            Debug.trace("indetionDept node = " + node);
            if(node instanceof Function ||
                    node instanceof Method ||
                    node instanceof ClassNode ||
                    node instanceof Block ){
                indept++;
            }
        }
        return indept;
    }
    public String indetionDeptString(){
        Debug.trace("getGlobal-> indetionDept: "+parent);
        SyntaxNode node = parent;
        String indept="";
        while(node.getParent()!=null){
            node = node.getParent();
            Debug.trace("indetionDept node = " + node);
            if(node instanceof Function ||
                    node instanceof Method ||
                    node instanceof ClassNode ||
                    node instanceof Block ){
                indept+=Global.INDENT;
            }
        }
        return indept;
    }
    
    
    public ArrayList<VariableDefinition> getNonClassVariables(){
        return null;
    }
}
