package com.srlupdater.deob.ControlFlowCorrection;

/*
 * @Author : Frement
 */
import java.util.ArrayList;

public class BlockNode {
    public ArrayList<BlockNode> predecessor;
    public ArrayList<BlockNode> successor;
    public boolean isVisited;
    public boolean isException;

    public BlockNode(ArrayList<BlockNode> predecessor, ArrayList<BlockNode> successor, boolean isException) {
        this.predecessor = predecessor;
        this.successor = successor;
        this.isException = isException;
        this.isVisited = false;
    }
}