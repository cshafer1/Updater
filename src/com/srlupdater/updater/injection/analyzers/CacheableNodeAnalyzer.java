package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.ListIterator;

public class CacheableNodeAnalyzer extends AbstractAnalyzer {

    @Override
    protected boolean canRun(ClassNode node) {
        if (!node.superName.equals(classNodes.get("Node").name))
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

    @Override
    protected Hook analyse(ClassNode node) {
        Hook hook = new Hook("CacheableNode",node.name);
        classNodes.put("CacheableNode",node);
        return hook;
    }
}
