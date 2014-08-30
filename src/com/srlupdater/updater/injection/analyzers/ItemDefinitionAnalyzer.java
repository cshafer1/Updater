package com.srlupdater.updater.injection.analyzers;

import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.Hook;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class ItemDefinitionAnalyzer extends AbstractAnalyzer {

    private boolean foundActions = false;

    @Override
    protected boolean canRun(ClassNode node) {
        if (!node.superName.endsWith(classNodes.get("CacheableNode").name) || classNodes.containsKey("ItemDefinition"))
            return false;
        ListIterator<MethodNode> mnli = node.methods.listIterator();
        while (mnli.hasNext()) {
            MethodNode mn = mnli.next();
            if (mn.name.equals("<init>")) {
                getActions(mn);
                break;
            }
        }
        return foundActions;
    }

    private void getActions(MethodNode constructor) {
        ListIterator<AbstractInsnNode> ainli = constructor.instructions.iterator();
        boolean gFlag = false;
        boolean iFlag = false;
        String gName = StringStorageAnalyzer.getStringStorageField("Take");
        String iName = StringStorageAnalyzer.getStringStorageField("Drop");
        while (ainli.hasNext()) {
            AbstractInsnNode ain = ainli.next();
            if (ain.getOpcode() == Opcodes.ANEWARRAY) {
                TypeInsnNode tin = (TypeInsnNode) ain;
                if (tin.desc.contains("java/lang/String")) {
                    while (ain.getNext() != null) {
                        ain = ain.getNext();
                        if (ain.getOpcode() == Opcodes.GETSTATIC) {
                            FieldInsnNode fin = (FieldInsnNode) ain;
                            if (fin.name.equals(gName)) {
                                gFlag = true;
                            } else if (fin.name.equals(iName)) {
                                iFlag = true;
                            }
                        } else if (ain.getOpcode() == Opcodes.PUTFIELD) {
                            FieldInsnNode fin = (FieldInsnNode) ain;
                            if (gFlag) {
                                    //hook.addFieldHook("groundActions", fin.name, fin.desc);
                                gFlag = false;
                                foundActions = true;
                                break;
                            } else if (iFlag) {
                                    //hook.addFieldHook("inventoryActions", fin.name, fin.desc);
                                iFlag = false;
                                foundActions = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected Hook analyse(ClassNode node) {
        Hook hook = new Hook("ItemDefinition",node.name);
        classNodes.put("ItemDefinition",node);
        return hook;
    }
}
