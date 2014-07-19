package com.srlupdater.deob.ArithmeticDeob;


import com.srlupdater.deob.Generic.DeobFrame;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.*;

/*
 * @Author : Krazy_Meerkat
 */
public class ArithmeticDeob extends DeobFrame {

    public ArithmeticDeob(HashMap<String, ClassNode> classes){
        super(classes);

    }
    @Override
    public HashMap<String,ClassNode> refactor(){
        System.out.println("*   Starting Arithmetic Deob*");
        List<InsnWrapper> replace = getInstructions();
        for(InsnWrapper wrap : replace){
            for(ClassNode node : classes.values()){
                if(!wrap.owner.equals(node.name))
                    continue;
                for (MethodNode mn : (Iterable<MethodNode>) node.methods) {
                    if(mn.name.equals(wrap.mn.name) && mn.desc.equals(wrap.mn.desc)) {
                        int i = 0;
                        while (i < mn.instructions.size()) {
                            if (!wrap.insnArray.get(i).equals(null))
                                mn.instructions.toArray()[i] = wrap.insnArray.get(i);
                            i++;
                        }
                    }
                }
            }
        }
        System.out.println("*   Arithmetic Deob Finished*");
        return classes;
    }

    public static final <T> void swap (List<T> l, int i, int j) {
        Collections.<T>swap(l, i, j);
    }

    public List<InsnWrapper> getInstructions(){
        Integer LDCIMULGETSTATIC = 0;
        Boolean FoundLDC = false;
        Boolean FoundLDCthenIMUL = false;
        Boolean FoundLDCthenIMULthenGETSTATIC = false;
        Integer GETSTATICIMULLDC = 0;
        Boolean FoundGETSTATIC = false;
        Boolean FoundGETSTATICthenIMUL = false;
        List <AbstractInsnNode> insnArray = new ArrayList<>();
        List <InsnWrapper> theData = new ArrayList<>();
        Iterator it = classes.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pairs = (Map.Entry)it.next();
            ClassNode node = (ClassNode)pairs.getValue();
            ListIterator<MethodNode> mnIt = node.methods.listIterator();
            while (mnIt.hasNext()) {
                MethodNode mn = mnIt.next();
                Iterator<InsnNode> it2 = mn.instructions.iterator();
                int i = 0;
                insnArray = new ArrayList<>();
                FoundLDCthenIMUL = false;
                FoundLDC = false;
                FoundLDCthenIMULthenGETSTATIC = false;
                FoundGETSTATICthenIMUL = false;
                FoundGETSTATIC = false;
                while (it2.hasNext()) {
                    AbstractInsnNode AbsInsn = it2.next();
                    insnArray.add(AbsInsn);
                    if (FoundLDCthenIMUL) {
                        if (AbsInsn.getOpcode() == Opcodes.GETSTATIC) {
                            FoundLDCthenIMULthenGETSTATIC = true; // LDC IMUL GETSTATIC
                        }
                        FoundLDCthenIMUL = false;
                        FoundLDC = false;
                    }
                    if (FoundLDC) {
                        if (AbsInsn.getOpcode() == Opcodes.IMUL) {
                            FoundLDCthenIMUL = true; // LDC IMUL
                        }
                        FoundLDC = false;
                    }
                    if (AbsInsn.getOpcode() == Opcodes.LDC) {
                        FoundLDC = true; // LDC
                    }
                    if (FoundGETSTATICthenIMUL) {
                        if (AbsInsn.getOpcode() == Opcodes.LDC) {
                            GETSTATICIMULLDC++; // Count GETSTATIC IMUL LDC
                        }
                        FoundGETSTATICthenIMUL = false;
                        FoundGETSTATIC = false;
                    }
                    if (FoundGETSTATIC) {
                        if (AbsInsn.getOpcode() == Opcodes.IMUL) {
                            FoundGETSTATICthenIMUL = true; // GETSTATIC IMUL
                        }
                        FoundGETSTATIC = false;
                    }
                    if (AbsInsn.getOpcode() == Opcodes.GETSTATIC) {
                        FoundGETSTATIC = true; // GETSTATIC
                    }
                    if (FoundLDCthenIMULthenGETSTATIC) {
                        swap(insnArray, i-2, i);
                        LDCIMULGETSTATIC++;
                        FoundLDCthenIMULthenGETSTATIC = false;
                    }
                    i++;
                }
                theData.add(new InsnWrapper(mn, node.name, insnArray));
            }
        }
        System.out.println("*      "+Integer.toString(LDCIMULGETSTATIC)+"/"+Integer.toString(GETSTATICIMULLDC+LDCIMULGETSTATIC)+" expressions modified*");
        return theData;
    }
}
