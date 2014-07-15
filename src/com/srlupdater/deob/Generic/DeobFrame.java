package com.srlupdater.deob.Generic;

import org.objectweb.asm.tree.ClassNode;


import java.util.HashMap;

/*
 * @Author : NKN
 */
public abstract class DeobFrame {



    protected abstract HashMap<String,ClassNode> refactor(HashMap<String,ClassNode> classes);




}
