package com.srlupdater.deob.RedundantMethod;


import com.srlupdater.deob.Generic.DeobFrame;
import org.objectweb.asm.tree.ClassNode;

import org.objectweb.asm.tree.MethodNode;


import java.util.*;

/*
 * @Author : NKN
 */
public class RedundantMethod extends DeobFrame {
   //private HashMap<String, ClassNode> classes;

    public RedundantMethod(HashMap<String,ClassNode> classes){
        super(classes);

    }
    @Override
    public HashMap<String,ClassNode> refactor(){
        HashMap<String,ClassNode> refactored = new HashMap<>();
        Iterator it = classes.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pairs = (Map.Entry)it.next();
            ClassNode node = (ClassNode)pairs.getValue();
            List<MethodNode> mnIt = getMethods(node);
            for(MethodNode mn : mnIt){
                node.methods.remove(mn);
            }

        }
        return refactored;
    }

    public List<MethodNode> getMethods(ClassNode node){
        return null;
    }


}
