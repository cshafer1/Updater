package com.srlupdater.deob.ArithmeticDeob;


import com.srlupdater.deob.Generic.DeobFrame;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

/*
 * @Author : Krazy_Meerkat
 */
public class ArithmeticDeob extends DeobFrame {

    public ArithmeticDeob(HashMap<String, ClassNode> classes){
        super(classes);

    }
    @Override
    public HashMap<String,ClassNode> refactor(){
        HashMap<String,ClassNode> refactored = new HashMap<>();
        Iterator it = classes.entrySet().iterator();
        Boolean FoundLDC = false;
        Boolean FoundLDCthenIMUL = false;
        Boolean FoundLDCthenIMULthenGETSTATIC = false;
        AbstractInsnNode insn1 = null;
        AbstractInsnNode insn3 = null;
        while(it.hasNext()){
            Map.Entry pairs = (Map.Entry)it.next();
            ClassNode node = (ClassNode)pairs.getValue();
            ClassNode newnode = node;
            newnode.methods.clear();
            ListIterator<MethodNode> mnIt = node.methods.listIterator();
            while (mnIt.hasNext()) {
                MethodNode mn = mnIt.next();
                Iterator<AbstractInsnNode> it2 = mn.instructions.iterator();
                while (it2.hasNext()) {
                    AbstractInsnNode insn = it2.next();
                    if (FoundLDCthenIMUL) {
                        if (insn.getOpcode() == Opcodes.GETSTATIC) {
                            FoundLDCthenIMULthenGETSTATIC = true; // Stage 3
                            insn3 = insn;
                        }
                        FoundLDCthenIMUL = false;
                    }
                    if (FoundLDC) {
                        if (insn.getOpcode() == Opcodes.IMUL) {
                            FoundLDCthenIMUL = true; // Stage 2
                        }
                        FoundLDC = false;
                    }
                    if (insn.getOpcode() == Opcodes.LDC) {
                        FoundLDC = true; // Stage 1
                        insn1 = insn;
                    }
                    if (FoundLDCthenIMULthenGETSTATIC) {
                        AbstractInsnNode insn2 = insn3; // This is here as a buffer
                        mn.instructions.set(insn3, insn1); // LDC on the right
                        mn.instructions.set(insn1, insn2); // GETSTATIC on the left
                        FoundLDCthenIMULthenGETSTATIC = false;
                    }
                }
                newnode.methods.add(mn); // Add the modified methodnode to the new node
            }
            refactored.put(newnode.name, newnode); // Put the new node into refactored
        }
        return refactored;
    }
}
