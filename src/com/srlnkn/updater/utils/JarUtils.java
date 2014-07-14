package com.srlnkn.updater.utils;

import org.objectweb.asm.ClassReader;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/*
 * @Author : NKN
 */
public class JarUtils {

    public static int getRevision(ClassNode node){
        ListIterator<MethodNode> mnIt = node.methods.listIterator();
        while(mnIt.hasNext()){
            MethodNode mn = mnIt.next();
            if(mn.name.equals("init")){
                ListIterator<AbstractInsnNode> abIn = mn.instructions.iterator();
                while(abIn.hasNext()){
                    AbstractInsnNode ain = abIn.next();
                    if(ain instanceof IntInsnNode){
                        if(((IntInsnNode) ain).operand == 765){
                            ain = ain.getNext();
                            if(ain instanceof IntInsnNode)
                                if(((IntInsnNode) ain).operand == 503)
                                    return ((IntInsnNode)ain.getNext()).operand;
                        }
                    }
                }


            }
        }
      return -1;
    }
    public static boolean isUpdated(ClassNode cached, String jarLink) {
        try {
            URL url = new URL("jar:"+jarLink+"!/");
            JarURLConnection conn = (JarURLConnection)url.openConnection();
            conn.setDoOutput(true);
            JarFile jar = conn.getJarFile();
            Enumeration<?> enumeration = jar.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry entry = (JarEntry) enumeration.nextElement();
                if (entry.getName().equals("client.class")) {
                    ClassReader classReader = new ClassReader(jar.getInputStream(entry));
                    ClassNode classNode = new ClassNode();
                    classReader.accept(classNode, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    return getRevision(cached) != getRevision(classNode);

                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static HashMap<String, ClassNode> parseJar(JarFile jarfile) {
        HashMap<String, ClassNode> classes = new HashMap();
        try {
            Enumeration<?> enumeration = jarfile.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry entry = (JarEntry) enumeration.nextElement();
                if (entry.getName().endsWith(".class")) {
                    ClassReader classReader = new ClassReader(jarfile.getInputStream(entry));
                    ClassNode classNode = new ClassNode();
                    classReader.accept(classNode, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    classes.put(classNode.name, classNode);
                }
            }
            jarfile.close();
            return classes;
        } catch (Exception e) {
            return null;
        }
    }
}
