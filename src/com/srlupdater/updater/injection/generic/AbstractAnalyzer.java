package com.srlupdater.updater.injection.generic;

import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;

public abstract class AbstractAnalyzer {
    public static HashMap<String, ClassNode> classNodes = new HashMap<>();
    public static HashMap<String, String> className = new HashMap<>();
    /** Runs the analyzer */
    public Hook run(ClassNode node) {
        if (canRun(node)) {
            return analyse(node);
        }
        return null;
    }

    /** Checks if the analyzer can run */
    protected abstract boolean canRun(ClassNode node);

    /** Checks if the analyzes the node */
    protected abstract Hook analyse(ClassNode node);


}