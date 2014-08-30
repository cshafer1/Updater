package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class CacheableNodeQueueAnalyzer extends AbstractAnalyzer {

    @Override
    protected boolean canRun(ClassNode node) {
        if (!node.superName.endsWith("Object") || classNodes.containsKey("CacheableNodeQueue"))
            return false;
        if (node.fields.size() == 1)
            if (((FieldNode) node.fields.get(0)).desc.equals("L" + classNodes.get("CacheableNode").name + ";"))
                return true;
        return false;
    }

    /*private void getHead(ClassNode node) {
        if (((FieldNode) node.fields.get(0)).desc.equals("L" + getTheFuckenValueOf("CacheableNode") + ";")) {
            FieldNode f = (FieldNode) node.fields.toArray()[0];
            hook.addFieldHook("getHead", f.name, f.desc);
        }
    }*/

    @Override
    protected Hook analyse(ClassNode node) {
        Hook hook = new Hook("CacheableNodeQueue",node.name);
        classNodes.put("CacheableNodeQueue",node);
        return hook;
    }
}
