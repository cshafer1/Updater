package com.srlupdater.deob.ControlFlowCorrection;

/*
 * @Author : Frement
 */
import java.util.ArrayList;

public class Block {
    public int index;
    public int target;
    public String type;
    public ArrayList<Block> nodes = new ArrayList<Block>();

    public Block(int index, int target, String type) {
        this.index = index;
        this.target = target;
        this.type = type;
    }
}