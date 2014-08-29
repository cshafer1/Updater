package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.Updater;
import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class CacheableNodeQueueAnalyzer extends AbstractAnalyzer {
    public String getTheFuckenValueOf(String ClassName) { //more iterating than desired
        for(Hook hook:Updater.hooks){
            if (hook.getClassName().equals(ClassName))
                return hook.getClassLocation();
        }
        return "";
    }
    @Override
    protected boolean canRun(ClassNode node) {
        if (!node.superName.endsWith("Object"))
            return false;
        if (node.fields.size() == 1)
            if (((FieldNode) node.fields.get(0)).desc.equals("L" + getTheFuckenValueOf("CacheableNode") + ";"))
                return true;
        return false;
    }
    private Hook hook;
    private void getHead(ClassNode node) {
        if (((FieldNode) node.fields.get(0)).desc.equals("L" + getTheFuckenValueOf("CacheableNode") + ";")) {
            FieldNode f = (FieldNode) node.fields.toArray()[0];
            hook.addFieldHook("getHead", f.name, f.desc);
        }
    }

    @Override
    protected Hook analyse(ClassNode node) {
        hook = new Hook("CacheableNodeQueue",node.name);
        getHead(node);
        return hook;
    }
}
