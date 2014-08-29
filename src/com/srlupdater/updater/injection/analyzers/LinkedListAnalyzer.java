package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.ListIterator;

public class LinkedListAnalyzer extends AbstractAnalyzer {

    @Override
    protected boolean canRun(ClassNode node) {
        int fieldCount = 0;
        int nodeCount = 0;
        ListIterator<FieldNode> fnIt = node.fields.listIterator();
        while (fnIt.hasNext()) {
            fieldCount++;
            FieldNode fn = fnIt.next();
            if (((fn.access & Opcodes.ACC_STATIC) == 0)) {
                if (fn.desc.equals(String.format("L%s;", classNodes.get("Node").name)))
                    nodeCount++;
            }
        }
        return nodeCount == 2 && fieldCount == 2;
    }

    @Override
    protected Hook analyse(ClassNode node) {
        Hook hook = new Hook("LinkedList",node.name);
        classNodes.put("LinkedList",node);
        return hook;
    }
}
