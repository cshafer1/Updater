package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class ModelAnalyzer extends AbstractAnalyzer {

    @Override
    protected boolean canRun(ClassNode node) {
        if (!node.superName.endsWith(classNodes.get("Renderable").name))
            return false;
        ListIterator<MethodNode> mnLi = node.methods.listIterator();
        int count = 0; // static boolean[] of length 4096, we want 2 of them.
        while (mnLi.hasNext()) {
            MethodNode mn = mnLi.next();
            if (mn.name.equals("<clinit>")) {
                ListIterator<AbstractInsnNode> ainli = mn.instructions.iterator();
                while (ainli.hasNext()) {
                    AbstractInsnNode ain = ainli.next();
                    if (ain.getOpcode() == Opcodes.NEWARRAY) {
                        if (((IntInsnNode) ain).operand == 4) {
                            AbstractInsnNode prev = mn.instructions.get(ainli.previousIndex() - 1);
                            if (prev.getOpcode() == Opcodes.SIPUSH) {
                                if (((IntInsnNode) prev).operand == 4096) {
                                    count++;
                                }
                            }
                        }
                    }
                }
                break;
            }
        }
        return count == 2;
    }

    @Override
    protected Hook analyse(ClassNode node) {
        Hook hook = new Hook("Model",node.name);
        classNodes.put("Model", node);
        return hook;
    }
}
