package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.Updater;
import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.ListIterator;

public class LinkedListAnalyzer extends AbstractAnalyzer {
    public String getTheFuckenValueOf(String ClassName) { //more iterating than desired
        for(Hook hook:Updater.hooks){
            if (hook.getClassName().equals(ClassName))
                return hook.getClassLocation();
        }
        return "";
    }
    @Override
    protected boolean canRun(ClassNode node) {
        int fieldCount = 0;
        int nodeCount = 0;
        ListIterator<FieldNode> fnIt = node.fields.listIterator();
        while (fnIt.hasNext()) {
            fieldCount++;
            FieldNode fn = fnIt.next();
            if (((fn.access & Opcodes.ACC_STATIC) == 0)) {
                if (fn.desc.equals(String.format("L%s;", getTheFuckenValueOf("Node"))))
                    nodeCount++;
            }
        }
        return nodeCount == 2 && fieldCount == 2;
    }
    private Hook hook;

    @Override
    protected Hook analyse(ClassNode node) {
        hook = new Hook("LinkedList",node.name);
        return hook;
    }
}
