package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.Updater;
import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Modifier;

public class RenderableAnalyzer extends AbstractAnalyzer {
    public String getTheFuckenValueOf(String ClassName) { //more iterating than desired
        for(Hook hook:Updater.hooks){
            if (hook.getClassName().equals(ClassName))
                return hook.getClassLocation();
        }
        return "";
    }
    @Override
    protected boolean canRun(ClassNode node) {
        if (!node.superName.endsWith(getTheFuckenValueOf("CacheableNode")))
            return false;
        if (!Modifier.isAbstract(node.access))
            return false;
        return true;
    }
    private Hook hook;

    @Override
    protected Hook analyse(ClassNode node) {
        hook = new Hook("Renderable",node.name);
        return hook;
    }
}
