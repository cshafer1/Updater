package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.Updater;
import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public class ActorAnalyzer extends AbstractAnalyzer {

    @Override
    protected boolean canRun(ClassNode node) {
        if (!node.superName.equals(classNodes.get("Renderable").name))
            return false;

        if(node.access == (Opcodes.ACC_ABSTRACT + Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER))
            return true;
        return false;
    }

    @Override
    protected Hook analyse(ClassNode node) {
        Hook hook = new Hook("Actor",node.name);
        classNodes.put("Actor",node);
        return hook;
    }
}
