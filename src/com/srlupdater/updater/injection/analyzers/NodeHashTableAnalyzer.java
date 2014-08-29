package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.Updater;
import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class NodeHashTableAnalyzer extends AbstractAnalyzer {
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
        if (node.fields.size() >= 2) {
            for (int fni=0; fni < node.fields.size(); fni++) {
                FieldNode fn = (FieldNode) node.fields.toArray()[fni];
                if (fn.desc.equals("[L" + getTheFuckenValueOf("Node") + ";"))
                    return true;
            }
        }
        return false;
    }
    private Hook hook;
    private void getNodes(ClassNode node) {
        for (int fni=0; fni < node.fields.size(); fni++) {
            FieldNode fn = (FieldNode) node.fields.toArray()[fni];
            if (fn.desc.equals("[L" + getTheFuckenValueOf("Node") + ";"))
                hook.addFieldHook("getNodes", fn.name, fn.desc);
        }
    }

    @Override
    protected Hook analyse(ClassNode node) {
        hook = new Hook("NodeHashTable",node.name);
        getNodes(node);
        return hook;
    }
}
