package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.ListIterator;

public class NodeAnalyzer extends AbstractAnalyzer {
    @Override
    protected boolean canRun(ClassNode node) {
        int selfCount = 0, jCount = 0;
        if(!node.superName.contains("java") || node.access != Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER)
            return false;
        ListIterator<FieldNode> fnIt = node.fields.listIterator();
        while(fnIt.hasNext()){
            FieldNode fn = fnIt.next();
            if(fn.desc.equals("L"+node.name+";"))
                selfCount++;
            if(fn.desc.equals("J"))
                jCount++;
        }
        return selfCount == 2 && jCount == 1;
    }

    @Override
    protected Hook analyse(ClassNode node) {
        Hook hook = new Hook("Node",node.name);
        return hook;
    }
}
