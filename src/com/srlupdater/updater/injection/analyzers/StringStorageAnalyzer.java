package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import com.srlupdater.updater.utils.Utils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class StringStorageAnalyzer extends AbstractAnalyzer {

    @Override
    protected boolean canRun(ClassNode node) {
        int count = 0;
        ListIterator<FieldNode> li = node.fields.listIterator();
        while (li.hasNext()) {
            FieldNode fn = li.next();
            if (fn.desc.equals("Ljava/lang/String;"))
                count++;
        }
        return count > 20;
    }

    public static List<String> StringStorageValues = new ArrayList<String>();

    private void storeStrings(ClassNode node) {
        ListIterator<FieldNode> li = node.fields.listIterator();
        while (li.hasNext()) {
            FieldNode fn = li.next();
            if (fn.desc.equals("Ljava/lang/String;"))
                StringStorageValues.add(fn.name);
        }
    }

    public static String getStringStorageField(String lookupString) {
        int i = 0;
        while (i < StringStorageValues.size()) {
            if (lookupString.equals(StringStorageValues.get(i)))
                return StringStorageValues.get(i);
        }
        return "";
    }

    public static boolean findSSRef(String lookupString, ClassNode classNode) {
        String owner = classNodes.get("StringStorage").name;
        String field = getStringStorageField(lookupString);
        ListIterator<MethodNode> mnli = classNode.methods.listIterator();
        while (mnli.hasNext()) {
            MethodNode mn = mnli.next();
            if (Utils.findInstruction(mn, new FieldInsnNode(Opcodes.GETSTATIC, owner, field, null)))
                return true;
        }
        return false;
    }

    @Override
    protected Hook analyse(ClassNode node) {
        Hook hook = new Hook("StringStorage",node.name);
        classNodes.put("StringStorage", node);
        storeStrings(node);
        return hook;
    }
}
