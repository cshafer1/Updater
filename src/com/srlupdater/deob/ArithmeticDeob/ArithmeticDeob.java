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
        Integer LDCIMULGETSTATIC = 0;
        Boolean FoundLDC = false;
        Boolean FoundLDCthenIMUL = false;
        Boolean FoundLDCthenIMULthenGETSTATIC = false;
        Integer GETSTATICIMULLDC = 0;
        Boolean FoundGETSTATIC = false;
        Boolean FoundGETSTATICthenIMUL = false;
        AbstractInsnNode insn1 = null;
        AbstractInsnNode insn3 = null;
        System.out.println("*   Starting Arithmetic Deob*");
        Iterator it = classes.entrySet().iterator();
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
                            FoundLDCthenIMULthenGETSTATIC = true; // LDC IMUL GETSTATIC
                            insn3 = insn;
                        }
                        FoundLDCthenIMUL = false;
                        FoundLDC = false;
                    }
                    if (FoundLDC) {
                        if (insn.getOpcode() == Opcodes.IMUL) {
                            FoundLDCthenIMUL = true; // LDC IMUL
                        }
                        FoundLDC = false;
                    }
                    if (insn.getOpcode() == Opcodes.LDC) {
                        FoundLDC = true; // LDC
                        insn1 = insn;
                    }
                    if (FoundGETSTATICthenIMUL) {
                        if (insn.getOpcode() == Opcodes.LDC) {
                            GETSTATICIMULLDC++; // Count GETSTATIC IMUL LDC
                        }
                        FoundGETSTATICthenIMUL = false;
                        FoundGETSTATIC = false;
                    }
                    if (FoundGETSTATIC) {
                        if (insn.getOpcode() == Opcodes.IMUL) {
                            FoundGETSTATICthenIMUL = true; // GETSTATIC IMUL
                        }
                        FoundGETSTATIC = false;
                    }
                    if (insn.getOpcode() == Opcodes.GETSTATIC) {
                        FoundGETSTATIC = true; // GETSTATIC
                    }
                    if (FoundLDCthenIMULthenGETSTATIC) {
                        AbstractInsnNode insn2 = insn3; // This is here as a buffer
                        mn.instructions.set(insn3, insn1); // LDC on the right
                        mn.instructions.set(insn1, insn2); // GETSTATIC on the left
                        FoundLDCthenIMULthenGETSTATIC = false;
                        LDCIMULGETSTATIC++;
                    }
                }
                newnode.methods.add(mn); // Add the modified methodnode to the new node
            }
            String name = (String)pairs.getKey();
            classes.remove(node); // Remove the old node
            classes.put(name, newnode); // Put the new node into classes
        }
        System.out.println("*      "+Integer.toString(LDCIMULGETSTATIC)+"/"+Integer.toString(GETSTATICIMULLDC+LDCIMULGETSTATIC)+" expressions modified*");
        System.out.println("*   Arithmetic Deob Finished*");
        return classes;
    }
}
