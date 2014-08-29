package com.srlupdater.deob;

import com.srlupdater.deob.ArithmeticDeob.ArithmeticDeob;
import com.srlupdater.deob.ControlFlowCorrection.ControlFlowCorrection;
import com.srlupdater.deob.Generic.DumpJar;
import com.srlupdater.deob.RedundantMethod.MethodRemoval;
import com.srlupdater.updater.Updater;
import org.objectweb.asm.tree.ClassNode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/*
 * @Author : NKN
 */
public class Deob {
    private HashMap<String, ClassNode> classes;
    private boolean useOutput,dumpClasses;

    public Deob(HashMap<String,ClassNode> classes, boolean useOutput, boolean dumpClasses){
        this.classes=classes;
        this.useOutput = useOutput;
        this.dumpClasses = dumpClasses;

    }

    public static List<String> deobOutput = new ArrayList<String>();
    public BufferedWriter writer = null;
    public HashMap<String, ClassNode> run(){
        System.out.println("{*Starting Deob*");
        if (useOutput) {
            classes = new MethodRemoval(classes).refactor();
        } else {
            try {
                List<String> lines = Files.readAllLines(Paths.get("output.txt"), Charset.defaultCharset());
                int i = 0;
                while (i < lines.size()) {
                    System.out.println((String)lines.get(i));
                    i++;
                }
            }
            catch ( IOException e) {
                e.printStackTrace();
            }
        }
        if (!dumpClasses) {
            classes = new ControlFlowCorrection(classes).refactor();
        } else {
            classes = new ArithmeticDeob(classes).refactor();
            new DumpJar(classes).createJar();
        }
        if (useOutput) {
            try {
                writer = new BufferedWriter( new FileWriter("output.txt"));
                int i = 0;
                while (i < deobOutput.size()) {
                    writer.write((String)deobOutput.get(i)); //store deob debug in output.txt for when useOutput = true
                    i++;
                }
            }
            catch ( IOException e)
            {
            }
            finally
            {
                try
                {
                    if ( writer != null)
                        writer.close();
                }
                catch ( IOException e)
                {
                }
            }
        }
        System.out.println("*Ending Deob*}");
        return classes;
    }


}