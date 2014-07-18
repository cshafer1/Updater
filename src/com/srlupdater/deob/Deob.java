package com.srlupdater.deob;

import com.srlupdater.deob.Generic.DumpJar;
import com.srlupdater.deob.RedundantMethod.MethodRemoval;
import org.objectweb.asm.tree.*;


import java.util.HashMap;


/*
 * @Author : NKN
 */
public class Deob {
    private HashMap<String, ClassNode> classes;


    public Deob(HashMap<String,ClassNode> classes){
        this.classes=classes;

    }

    public HashMap<String, ClassNode> run(){
        System.out.println("*Starting deob*");
        classes = new MethodRemoval(classes).refactor();
        new DumpJar(classes).createJar();
        System.out.println("*Ending deob*");
        return classes;
    }


}
