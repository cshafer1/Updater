package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.Updater;
import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.ArrayList;
import java.util.ListIterator;

public class NodeCacheAnalyzer extends AbstractAnalyzer {
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
        ArrayList<String> descriptors = new ArrayList<String>();
        descriptors.add(getTheFuckenValueOf("CacheableNode"));
        descriptors.add(getTheFuckenValueOf("NodeHashTable"));
        descriptors.add(getTheFuckenValueOf("CacheableNodeQueue"));
        int count = 0;
        ListIterator<FieldNode> li = node.fields.listIterator();
        while (li.hasNext()) {
            FieldNode fn = li.next();
            String d = fn.desc.replace("L", "").replace(";", "");
            if (descriptors.contains(d)) {
                if (d.equals(descriptors.get(0))) {

                }
                else if (d.equals(descriptors.get(1))) {

                }
                else if (d.equals(descriptors.get(2))) {

                }
                else
                    continue;
                count++;
            }
        }

        return count == 2;
    }

    private Hook hook;

    private void fieldAnalyzer(ClassNode node) {
        ArrayList<String> descriptors = new ArrayList<String>();
        descriptors.add(getTheFuckenValueOf("CacheableNode"));
        descriptors.add(getTheFuckenValueOf("NodeHashTable"));
        descriptors.add(getTheFuckenValueOf("CacheableNodeQueue"));
        ListIterator<FieldNode> li = node.fields.listIterator();
        while (li.hasNext()) {
            FieldNode fn = li.next();
            String d = fn.desc.replace("L", "").replace(";", "");
            if (descriptors.contains(d)) {
                if (d.equals(descriptors.get(0)))
                    hook.addFieldHook("cacheableNode", fn.name, fn.desc);
                else if (d.equals(descriptors.get(1)))
                    hook.addFieldHook("hashTable", fn.name, fn.desc);
                else if (d.equals(descriptors.get(2)))
                    hook.addFieldHook("queue", fn.name, fn.desc);
                else
                continue;
            }
        }
    }

    @Override
    protected Hook analyse(ClassNode node) {
        hook = new Hook("NodeCache",node.name);
        fieldAnalyzer(node);
        return hook;
    }
}
