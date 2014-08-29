package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.lang.reflect.Modifier;
import java.util.ListIterator;

public class NodeAnalyzer extends AbstractAnalyzer {
    @Override
    protected boolean canRun(ClassNode node) {
        int selfCount = 0, longCount = 0;
        if(!node.superName.contains("java") || node.access != Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER)
            return false;
        ListIterator<FieldNode> fnIt = node.fields.listIterator();
        while(fnIt.hasNext()){
            FieldNode fn = fnIt.next();
            if(fn.desc.equals("L"+node.name+";"))
                selfCount++;
            if(fn.desc.equals("J"))
                longCount++;
        }
        return selfCount == 2 && longCount == 1;
    }

    private void getPrevNext(ClassNode node) {
        ListIterator<FieldNode> li = node.fields.listIterator();
        while (li.hasNext()) {
            FieldNode fn = li.next();
            if (fn.desc.equals(String.format("L%s;", node.name))) {
                if (Modifier.isPublic(fn.access)) {
                    hook.addFieldHook("prev", fn.name, fn.desc);
                } else {
                    hook.addFieldHook("next", fn.name, fn.desc);
                }
            } else if (fn.desc.equals("J")) {
                hook.addFieldHook("id", fn.name, fn.desc);
            }
        }
    }

    private Hook hook;

    @Override
    protected Hook analyse(ClassNode node) {
        hook = new Hook("Node",node.name);
        getPrevNext(node);
        return hook;
    }
}
