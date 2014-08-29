package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.Updater;
import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import com.srlupdater.updater.utils.Utils;
import org.objectweb.asm.tree.ClassNode;

public class StreamAnalyzer extends AbstractAnalyzer {
    public String getTheFuckenValueOf(String ClassName) { //more iterating than desired
        for(Hook hook:Updater.hooks){
            if (hook.getClassName().equals(ClassName))
                return hook.getClassLocation();
        }
        return "";
    }
    @Override
    protected boolean canRun(ClassNode node) {
        if (!node.superName.endsWith(getTheFuckenValueOf("Node")))
            return false;
        if (node.methods.size() < 20)
            return false;
        return Utils.isConstructorDescriptor(node, "([B)V");
    }
    private Hook hook;

    @Override
    protected Hook analyse(ClassNode node) {
        hook = new Hook("Stream",node.name);
        return hook;
    }
}
