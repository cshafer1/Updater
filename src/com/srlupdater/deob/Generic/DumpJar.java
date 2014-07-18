package com.srlupdater.deob.Generic;

import com.srlupdater.updater.utils.Configs;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * @Author : NKN
 */
public class DumpJar {
    private HashMap<String, ClassNode> classes;

    public DumpJar(HashMap<String, ClassNode> classes){
        this.classes = classes;
    }
    private void add(String path, byte[] info,  JarOutputStream target) throws IOException
    {
        BufferedInputStream in = null;
        try
        {
            JarEntry entry = new JarEntry(path.replace("\\", "/"));
            target.putNextEntry(entry);
            in = new BufferedInputStream(new ByteArrayInputStream(info));

            byte[] buffer = new byte[1024];
            while (true)
            {
                int count = in.read(buffer);
                if (count == -1)
                    break;
                target.write(buffer, 0, count);
            }
            target.closeEntry();
        }
        finally
        {
            if (in != null)
                in.close();
        }
    }
    public void createJar() {
        System.out.println("*   Creating Jar*");
        try {
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
            JarOutputStream target = new JarOutputStream(new FileOutputStream(Configs.HOME+"output.jar"), manifest);
            for (ClassNode node : classes.values()) {
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                node.accept(cw);
                add(node.name+".class",cw.toByteArray(),target);
            }
            target.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("*   Finished jar.  Location: "+ Configs.HOME+"output.jar");
    }
}
