package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.tree.ClassNode;

public class NpcDefinitionAnalyzer extends AbstractAnalyzer {

    @Override
    protected boolean canRun(ClassNode node) {
        if (!node.superName.endsWith(classNodes.get("CacheableNode").name))
            return false;
        return StringStorageAnalyzer.findSSRef("Hidden", node);
    }
    private Hook hook;

    @Override
    protected Hook analyse(ClassNode node) {
        hook = new Hook("NpcDefinition",node.name);
        classNodes.put("NpcDefinition",node);
        return hook;
    }
}
