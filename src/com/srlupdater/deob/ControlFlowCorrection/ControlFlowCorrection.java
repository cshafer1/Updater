package com.srlupdater.deob.ControlFlowCorrection;


import com.srlupdater.deob.Generic.DeobFrame;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static com.srlupdater.updater.utils.Utils.opcodeToString;

/*
 * @Author : Frement
 * Modified by Krazy_Meerkat
 */
public class ControlFlowCorrection extends DeobFrame {

    public ControlFlowCorrection(HashMap<String, ClassNode> classes){
        super(classes);

    }

    private Block getBlock(ArrayList<Block> blocks, int index) {
        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i).index == index) {
                return blocks.get(i);
            }
        }

        return null;
    }

    private boolean isEmptyBlock(ArrayList<AbstractInsnNode> block) {
        return (block.size() == 1 && block.get(0).getOpcode() == Opcodes.GOTO);
    }

    private boolean isEndBlock(ArrayList<AbstractInsnNode> block) {
        if (opcodeToString(block.get(block.size() - 1).getOpcode()).contains("RETURN")) {
            return true;
        } else {
            if (block.get(block.size() - 1).getOpcode() == Opcodes.ATHROW) {
                return true;
            }
        }

        return false;
    }

    private int findJumpBlock(ArrayList<ArrayList<AbstractInsnNode>> blocks, AbstractInsnNode target) {
        Iterator it = blocks.iterator();
        while (it.hasNext()) {
            ArrayList<AbstractInsnNode> block = (ArrayList<AbstractInsnNode>) it.next();
            if (block.contains(target)) {
                return (blocks.indexOf(block) + 1);
            }
        }

        return -1;
    }

    private int findJumpTargetBlock(ArrayList<ArrayList<AbstractInsnNode>> blocks, ArrayList<AbstractInsnNode> block) {
        JumpInsnNode insn = (JumpInsnNode) block.get(block.size() - 1);

        return findJumpBlock(blocks, insn.label);
    }

    @Override
    public HashMap<String,ClassNode> refactor(){
        int ordered = 0;
        int removed = 0;
        System.out.println("*   Starting Control Flow Correction*");
        for(ClassNode node : classes.values()){
            for (MethodNode method : (Iterable<MethodNode>) node.methods) {
                ArrayList<ArrayList<AbstractInsnNode>> blocks = new ArrayList<ArrayList<AbstractInsnNode>>();
                Iterator<AbstractInsnNode> it = method.instructions.iterator();
                ArrayList<AbstractInsnNode> currentBlock = new ArrayList<AbstractInsnNode>();
                while (it.hasNext()) {
                    AbstractInsnNode insn = it.next();
                    currentBlock.add(insn);
                    String pos = method.instructions.indexOf(insn) + ": ";
                    if (insn.getType() == AbstractInsnNode.LDC_INSN) {
                        //System.out.println(pos + opcodeToString(insn.getOpcode()) + " " + ((LdcInsnNode) insn).cst.toString());
                    } else if (insn.getOpcode() == Opcodes.GETFIELD) {
                        //System.out.println(pos + opcodeToString(insn.getOpcode()) + " " + ((FieldInsnNode) insn).name);
                    } else if (insn.getOpcode() == Opcodes.GETSTATIC) {
                        //System.out.println(pos + opcodeToString(insn.getOpcode()) + " " + ((FieldInsnNode) insn).owner + "." + ((FieldInsnNode) insn).name);
                    } else if (insn.getType() == AbstractInsnNode.JUMP_INSN) {
                        JumpInsnNode jin = (JumpInsnNode) insn;
                        blocks.add(currentBlock);
                        currentBlock = new ArrayList<AbstractInsnNode>();
                        //System.out.println(pos + opcodeToString(insn.getOpcode()) + " " + method.instructions.indexOf(jin.label));
                    } else {
                        //System.out.println(pos + opcodeToString(insn.getOpcode()));
                        if (opcodeToString(insn.getOpcode()).contains("RETURN") || insn.getOpcode() == Opcodes.ATHROW) {
                            blocks.add(currentBlock);
                            currentBlock = new ArrayList<AbstractInsnNode>();
                        }
                    }
                }
                if (currentBlock.size() > 0) {
                    blocks.add(currentBlock);
                }
                Iterator bit = blocks.iterator();
                ArrayList<Block> pBlocks = new ArrayList<Block>();
                while (bit.hasNext()) {
                    ArrayList<AbstractInsnNode> block = (ArrayList<AbstractInsnNode>) bit.next();
                    String type = "";
                    if (isEmptyBlock(block)) {
                        type ="(empty)";
                    } else if (isEndBlock(block)) {
                        type = "(end)";
                    } else {
                        if (block.get(block.size() - 1).getOpcode() != Opcodes.GOTO) {
                            type = "(immediate)";
                        }
                    }
                    if (type != "(end)") {
                        int target = findJumpTargetBlock(blocks, block);
                        if (type == "(immediate)") {
                            pBlocks.add(new Block(blocks.indexOf(block) + 1, target, ""));
                        //    System.out.println((blocks.indexOf(block) + 1) + " -> " + target);
                        }
                        if (type == "(immediate)") {
                            pBlocks.add(new Block(blocks.indexOf(block) + 1, blocks.indexOf(block) + 2, type));
                        //    System.out.println((blocks.indexOf(block) + 1) + " -> " + (blocks.indexOf(block) + 2) + " " + type);
                        } else {
                            pBlocks.add(new Block(blocks.indexOf(block) + 1, target, type));
                        //    System.out.println((blocks.indexOf(block) + 1) + " -> " + target + " " + type);
                        }
                    } else {
                        pBlocks.add(new Block(blocks.indexOf(block) + 1, -1, type));
                    //    System.out.println((blocks.indexOf(block) + 1) + " " + type);
                    }
                    Iterator cit = block.iterator();
                    while (cit.hasNext()) {
                        AbstractInsnNode insn = (AbstractInsnNode) cit.next();
                        String pos = (method.instructions.indexOf(insn) + 1) + ": ";
                        if (insn.getType() == AbstractInsnNode.LDC_INSN) {
                        //    System.out.println(pos + opcodeToString(insn.getOpcode()) + " " + ((LdcInsnNode) insn).cst.toString());
                        } else if (insn.getOpcode() == Opcodes.GETFIELD) {
                        //    System.out.println(pos + opcodeToString(insn.getOpcode()) + " " + ((FieldInsnNode) insn).name);
                        } else if (insn.getOpcode() == Opcodes.GETSTATIC) {
                        //    System.out.println(pos + opcodeToString(insn.getOpcode()) + " " + ((FieldInsnNode) insn).owner + "." + ((FieldInsnNode) insn).name);
                        } else if (insn.getType() == AbstractInsnNode.JUMP_INSN) {
                            JumpInsnNode jin = (JumpInsnNode) insn;
                        //    System.out.println(pos + opcodeToString(insn.getOpcode()) + " " + method.instructions.indexOf(jin.label) + " (" + findJumpBlock(blocks, jin.label) + ")");
                        } else {
                        //    System.out.println(pos + opcodeToString(insn.getOpcode()));
                        }
                    }
                   // System.out.println("");
                }
                            /* REMOVE EMPTY BLOCKS */
                ArrayList<Block> rBlocks = new ArrayList<Block>();
                for (int i = 0; i < pBlocks.size(); i++) {
                    if (pBlocks.get(i).type == "(empty)") {
                        rBlocks.add(pBlocks.get(i));
                        for (int j = 0; j < pBlocks.size(); j++) {
                            if (pBlocks.get(j).target == pBlocks.get(i).index) {
                                pBlocks.get(j).target = pBlocks.get(i).target;
                            }
                        }
                    }
                }
                for (int i = 0; i < rBlocks.size(); i++) {
                    pBlocks.remove(rBlocks.get(i));
                }
                removed = removed + rBlocks.size();
                            /* ORDER BLOCKS */
                if (pBlocks.size() > 0) {
                    Graph g = new Graph();
                    for (int i = 0; i < pBlocks.size(); i++) {
                        if (pBlocks.get(i).target != -1) {
                            g.addEdge(Integer.toString(pBlocks.get(i).index), Integer.toString(pBlocks.get(i).target));
                        }
                    }
                    Iterator<String> blockIt = null;
                    blockIt = new PreOrderDFSIterator(g, Integer.toString(pBlocks.get(0).index)); //DFS
                    int i = 0;
                    int e = 0;
                    int blockNumber = 0;
                    while (blockIt.hasNext()) {
                        blockNumber = Integer.valueOf(blockIt.next()); //new block order from the DFS
                        Iterator cit = blocks.iterator();
                        while (cit.hasNext()) {
                            ArrayList<AbstractInsnNode> block = (ArrayList<AbstractInsnNode>) cit.next();
                            if (blockNumber == e) { //edit the next block's instructions into our methodNode
                                Iterator dit = block.iterator();
                                while (dit.hasNext()) {
                                    AbstractInsnNode insn = (AbstractInsnNode) dit.next();
                                    if (i < method.instructions.size()) { //edit if there is a current insn
                                        method.instructions.toArray()[i] = insn;
                                    } else {
                                        method.instructions.add(insn); //add instructions if we exceed the array size
                                    }
                                    i = i + 1;
                                }
                                ordered = ordered + 1;
                            }
                            e = e + 1;
                        }
                    }
                    //while (i < method.instructions.size()) { //remove any extra instructions if we shortened the array
                    //    method.instructions.remove(method.instructions.toArray()[i]);
                    //}
                    //method.instructions.resetLabels();
                }
                            /* PRINT BLOCKS */
                for (int i = 0; i < pBlocks.size(); i++) {
                    if (pBlocks.get(i).target != -1) {
                    //    System.out.println(pBlocks.get(i).index + " -> " + pBlocks.get(i).target + " " + pBlocks.get(i).type);
                    } else {
                    //    System.out.println(pBlocks.get(i).index + " " + pBlocks.get(i).type);
                    }
                }
               // System.out.println(" ");
            }
        } //
        System.out.println("*      "+Integer.toString(ordered)+" methods constructed*");
        System.out.println("*      "+Integer.toString(removed)+" methods refactored*");
        System.out.println("*   Control Flow Correction Finished*");
        return classes;
    }

}
