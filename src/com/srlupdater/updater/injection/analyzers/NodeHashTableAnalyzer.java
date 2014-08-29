package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.Updater;
import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class NodeHashTableAnalyzer extends AbstractAnalyzer {

    @Override
    protected boolean canRun(ClassNode node) {
        if (!node.superName.endsWith("Object"))
            return false;
        if (node.fields.size() >= 2) {
            for (int fni=0; fni < node.fields.size(); fni++) {
                FieldNode fn = (FieldNode) node.fields.get(fni);
                if (fn.desc.equals("[L" + classNodes.get("Node").name + ";"))
                    return true;
            }
        }
        return false;
    }
    private Hook hook;
   /* private void getNodes(ClassNode node) {
        for (int fni=0; fni < node.fields.size(); fni++) {
            FieldNode fn = (FieldNode) node.fields.toArray()[fni];
            if (fn.desc.equals("[L" + getTheFuckenValueOf("Node") + ";"))
                hook.addFieldHook("getNodes", fn.name, fn.desc);
        }
    } */

    @Override
    protected Hook analyse(ClassNode node) {
        hook = new Hook("NodeHashTable",node.name);
        classNodes.put("NodeHashTable",node);
        return hook;
    }
}
