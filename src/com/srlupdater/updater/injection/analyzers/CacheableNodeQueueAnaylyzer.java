package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.Updater;
import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class CacheableNodeQueueAnaylyzer extends AbstractAnalyzer {

    @Override
    protected boolean canRun(ClassNode node) {
        if (!node.superName.endsWith("Object"))
            return false;
        if (node.fields.size() == 1)
            if (((FieldNode) node.fields.get(0)).desc.equals("L"+classNodes.get("CacheableNode").name+";"))
                return true;
        return false;
    }


    @Override
    protected Hook analyse(ClassNode node) {
        Hook hook = new Hook("CacheableNodeQueue",node.name);
        classNodes.put("CacheableNodeQueue",node);
        return hook;
    }
}
