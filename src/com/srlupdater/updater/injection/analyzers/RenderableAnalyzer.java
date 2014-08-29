package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.Updater;
import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Modifier;

public class RenderableAnalyzer extends AbstractAnalyzer {

    @Override
    protected boolean canRun(ClassNode node) {
        if(node.superName.equals(classNodes.get("CacheableNode").name)){
            if(node.access == (Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_SUPER)){
                return true;
            }
        }
        return false;
    }
    private Hook hook;

    @Override
    protected Hook analyse(ClassNode node) {
        hook = new Hook("Renderable",node.name);
        classNodes.put("Renderable",node);
        return hook;
    }
}
