package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class ItemAnalyzer extends AbstractAnalyzer {

    @Override
    protected boolean canRun(ClassNode node) {
        if (!node.superName.equals(classNodes.get("Renderable").name) || classNodes.containsKey("Item"))
            return false;
        ListIterator<MethodNode> mnli = node.methods.listIterator();
        while (mnli.hasNext()) {
            MethodNode mn = mnli.next();
            if (mn.name.equals("<init>")) {
                if (mn.instructions.get(3).getOpcode() == Opcodes.RETURN)
                    return true;
            }
        }
        return false;
    }

    @Override
    protected Hook analyse(ClassNode node) {
        Hook hook = new Hook("Item",node.name);
        classNodes.put("Item",node);
        return hook;
    }
}
