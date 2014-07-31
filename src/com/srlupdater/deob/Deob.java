package com.srlupdater.deob;

import com.srlupdater.deob.ArithmeticDeob.ArithmeticDeob;
import com.srlupdater.deob.ControlFlowCorrection.ControlFlowCorrection;
import com.srlupdater.deob.Generic.DumpJar;
import com.srlupdater.deob.RedundantMethod.MethodRemoval;
import org.objectweb.asm.tree.ClassNode;

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
        System.out.println("{*Starting Deob*");
        classes = new ControlFlowCorrection(classes).refactor();
        classes = new MethodRemoval(classes).refactor();
        classes = new ArithmeticDeob(classes).refactor();
        new DumpJar(classes).createJar();
        System.out.println("*Ending Deob*}");
        return classes;
    }


}
