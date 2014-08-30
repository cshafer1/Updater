package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.FieldAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.ListIterator;

public class NodeAnalyzer extends AbstractAnalyzer {

    @Override
    protected boolean canRun(ClassNode node) {
        if(!node.superName.contains("java") || node.access != Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER || classNodes.containsKey("Node"))
            return false;
        int selfCount = 0, longCount = 0;
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

    @Override
    protected Hook analyse(ClassNode node) {
        Hook hook = new Hook("Node",node.name);
        classNodes.put("Node",node);
        ListIterator<FieldNode> fnIt = node.fields.listIterator();
        while(fnIt.hasNext()){
            FieldNode fn = fnIt.next();
            new getPrevAnalyzer(node,hook,fn).run();
            new getNextAnalyzer(node,hook,fn).run();
            new getIDAnalyzer(node,hook,fn).run();
        }

        return hook;
    }

    private class getPrevAnalyzer extends FieldAnalyzer {
        public getPrevAnalyzer(ClassNode node, Hook hook, FieldNode fn) {
            super(node, hook, fn);
        }

        @Override
        protected boolean canRun() {
            return !hook.getFieldHooks().containsKey("getPrev") && fn.desc.equals(String.format("L%s;", node.name)) && (fn.access == Opcodes.ACC_PUBLIC);
        }

        @Override
        protected void analyze() {
            hook.addFieldHook("getPrev",fn.name,fn.desc);
        }
    }

    private class getNextAnalyzer extends FieldAnalyzer {
        public getNextAnalyzer(ClassNode node, Hook hook, FieldNode fn) {
            super(node, hook, fn);
        }

        @Override
        protected boolean canRun() {
            return !hook.getFieldHooks().containsKey("getNext") && fn.desc.equals(String.format("L%s;", node.name)) && (fn.access != Opcodes.ACC_PUBLIC);
        }

        @Override
        protected void analyze() {
            hook.addFieldHook("getNext",fn.name,fn.desc);
        }
    }

    private class getIDAnalyzer extends FieldAnalyzer {
        public getIDAnalyzer(ClassNode node, Hook hook, FieldNode fn) {
            super(node, hook, fn);
        }

        @Override
        protected boolean canRun() {
            return !hook.getFieldHooks().containsKey("getID") && fn.desc.equals("J");
        }

        @Override
        protected void analyze() {
            hook.addFieldHook("getID",fn.name,fn.desc);
        }
    }

}
