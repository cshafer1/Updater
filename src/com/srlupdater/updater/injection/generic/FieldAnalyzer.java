package com.srlupdater.updater.injection.generic;


import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * @Author : NKN
 */
public abstract class FieldAnalyzer {
    public final ClassNode node;
    public final Hook hook;
    public FieldNode fn;
    public MethodNode mn;

    public FieldAnalyzer(ClassNode node, Hook hook, FieldNode fn) {
        this.node = node;
        this.hook = hook;
        this.fn = fn;
    }

    public FieldAnalyzer(ClassNode node, Hook hook, MethodNode mn) {

        this.node = node;
        this.hook = hook;
        this.mn = mn;
    }

    public void run() {
        if (canRun())
            analyze();
    }

    /**
     * Checks if the analyzer can run
     */
    protected abstract boolean canRun();

    /**
     * Checks if the analyzes the node
     */
    protected abstract void analyze();
}