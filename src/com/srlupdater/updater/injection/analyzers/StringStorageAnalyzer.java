package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import com.srlupdater.updater.utils.Utils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.*;

public class StringStorageAnalyzer extends AbstractAnalyzer {

    @Override
    protected boolean canRun(ClassNode node) {
        if (classNodes.containsKey("StringStorage"))
            return false;
        int count = 0;
        ListIterator<FieldNode> li = node.fields.listIterator();
        while (li.hasNext()) {
            FieldNode fn = li.next();
            if (fn.desc.equals("Ljava/lang/String;"))
                count++;
        }
        return count > 20;
    }

    public static HashMap<String, String> StringStorageValues = new HashMap<String, String>();

    private void storeStrings(ClassNode node) {
        ListIterator<MethodNode> mnli = node.methods.listIterator();
        while (mnli.hasNext()) {
            MethodNode mn = mnli.next();
            if (mn.name.equals("<clinit>")) {
                ListIterator<AbstractInsnNode> ainli = mn.instructions.iterator();
                while (ainli.hasNext()) {
                    AbstractInsnNode ain = ainli.next();
                    if (ain.getOpcode() == Opcodes.LDC) {
                        String text = (String) ((LdcInsnNode) ain).cst;
                        if (text == null)
                            continue;
                        if (text.length() < 4)
                            continue;
                        AbstractInsnNode next = ain.getNext();
                        if (next.getOpcode() == Opcodes.PUTSTATIC) {
                            FieldInsnNode fin = (FieldInsnNode) next;
                            String[] theValues = new String[]{text};
                            for (int i=0; i<theValues.length; i++) {
                                if (!StringStorageValues.containsKey(theValues[i])) {
                                    StringStorageValues.put(theValues[i], fin.name);
                                }
                                //System.out.println(theValues[i]);
                            }
                        }
                    }
                }
            }
        }
    }

    public static String getStringStorageField(String lookupString) {
        if (StringStorageValues.containsKey(lookupString))
            return StringStorageValues.get(lookupString);
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
