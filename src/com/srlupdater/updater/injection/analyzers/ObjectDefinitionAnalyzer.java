package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class ObjectDefinitionAnalyzer extends AbstractAnalyzer {

    @Override
    protected boolean canRun(ClassNode node) {
        if (!node.superName.endsWith(classNodes.get("CacheableNode").name) || classNodes.containsKey("ObjectDefinition"))
            return false;
        ListIterator<MethodNode> mnli = node.methods.listIterator();
        while (mnli.hasNext()) {
            MethodNode mn = mnli.next();
            if (mn.name.equals("<clinit>")) {
                ListIterator<AbstractInsnNode> ainli = mn.instructions.iterator();
                while (ainli.hasNext()) {
                    AbstractInsnNode ain = ainli.next();
                    if (ain.getOpcode() == Opcodes.SIPUSH) {
                        if (((IntInsnNode) ain).operand == 500) {
                            ain = ain.getNext();
                            if (ain.getOpcode() == Opcodes.INVOKESPECIAL) {
                                MethodInsnNode min = (MethodInsnNode) ain;
                                //System.out.println("//found " + min.owner + "!");
                                //if (min.owner.equals(Storage.checkedClasses.get("NodeCache").name) && min.name.equals("<init>"))
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected Hook analyse(ClassNode node) {
        Hook hook = new Hook("ObjectDefinition",node.name);
        classNodes.put("ObjectDefinition",node);
        return hook;
    }
}
