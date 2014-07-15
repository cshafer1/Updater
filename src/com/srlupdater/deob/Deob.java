package com.srlupdater.deob;

import com.srlupdater.deob.RedundantMethod.RedundantMethod;
import org.objectweb.asm.tree.*;


import java.util.HashMap;


/*
 * @Author : NKN
 */
public class Deob {
    private HashMap<String, ClassNode> classes;


    public Deob(HashMap<String,ClassNode> classes){
        this.classes=classes;
        //classes = new RedundantMethod(classes).refactor();
    }


}
