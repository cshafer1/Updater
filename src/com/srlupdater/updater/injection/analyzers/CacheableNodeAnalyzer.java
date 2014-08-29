package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.Updater;
import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.ListIterator;

public class CacheableNodeAnalyzer extends AbstractAnalyzer {
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
        int countSelf = 0;
        ListIterator<FieldNode> li = node.fields.listIterator();
        while (li.hasNext()) {
            FieldNode fn = li.next();
            if (fn.desc.equals(String.format("L%s;", node.name)) ) {
                countSelf++;
            }
            if (countSelf > 2)
                return false;
        }
        return countSelf == 2;
    }
    private Hook hook;

    @Override
    protected Hook analyse(ClassNode node) {
        hook = new Hook("CacheableNode",node.name);
        return hook;
    }
}
