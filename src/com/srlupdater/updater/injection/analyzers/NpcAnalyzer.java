package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.ListIterator;

public class NpcAnalyzer extends AbstractAnalyzer {

    @Override
    protected boolean canRun(ClassNode node) {
        if (!node.superName.endsWith(classNodes.get("Actor").name))
            return false;
        ListIterator<FieldNode> li = node.fields.listIterator();
        while (li.hasNext()) {
            FieldNode fn = li.next();
            if (fn.desc.equals("L" + classNodes.get("NpcDefinition").name + ";")) {
                return true;
            }
        }
        return false;
    }

    /*private void getDefinition(ClassNode node) {
        ListIterator<FieldNode> li = node.fields.listIterator();
        while (li.hasNext()) {
            FieldNode fn = li.next();
            if (fn.desc.equals("L" + getTheFuckenValueOf("NpcDefinition") + ";"))
                hook.addFieldHook("definition", fn.name, fn.desc);
        }
    }*/

    @Override
    protected Hook analyse(ClassNode node) {
        Hook hook = new Hook("Npc",node.name);
        classNodes.put("Npc",node);
        return hook;
    }
}
