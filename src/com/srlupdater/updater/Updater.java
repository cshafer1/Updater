package com.srlupdater.updater;

import com.srlupdater.deob.Deob;
import com.srlupdater.updater.utils.Configs;
import com.srlupdater.updater.utils.JarUtils;
import com.srlupdater.updater.utils.Utils;
import org.objectweb.asm.tree.ClassNode;


import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Author : NKN & JJ & Krazy Meerkat
 */
public class Updater {

    public Integer CustomRevison = 0; //This must be left at 0 when not in use.

    public Updater(){
        try {
            String rsLink = "http://oldschool11.runescape.com/";
            String pageSource = Utils.getPage(rsLink);
            Pattern archiveRegex = Pattern.compile("archive=(.*) ");
            Matcher archiveMatcher = archiveRegex.matcher(pageSource);
            Pattern codeRegex = Pattern.compile("code=(.*) ");
            Matcher codeMatcher = codeRegex.matcher(pageSource);
            if (archiveMatcher.find() && codeMatcher.find()) {
                String jarLink = rsLink + archiveMatcher.group(1);
                //System.out.println("Jar location: " + jarLink);
                String codeName = codeMatcher.group(1).replaceAll(".class", "");
                //System.out.println("Code name: " + codeName);
                //System.out.println("\nLoading parameters...");
                Pattern paramRegex = Pattern.compile("<param name=\"([^\\s]+)\"\\s+value=\"([^>]*)\">");
                Matcher paramMatcher = paramRegex.matcher(pageSource);
                while (paramMatcher.find()) {
                    String key = paramMatcher.group(1);
                    String value = paramMatcher.group(2);
                    //System.out.printf("%-20s %s", key, value + "\n");
                    HashMap<String, String> parameters = new HashMap<>();
                    parameters.put(key, value);
                }
                File dir = new File(Configs.HOME);
                if(!dir.exists())
                    dir.mkdir();
                File tempCachedClient = new File(Configs.HOME, "client.jar");
                if (CustomRevison > 0) {
                    tempCachedClient = new File(Configs.HOME, "client"+CustomRevison+".jar");
                }
                if ((!tempCachedClient.exists()) && (CustomRevison < 1)) {
                    System.out.println("\n//Downloading Initial Client");
                    Utils.downloadFile(jarLink, tempCachedClient);
                }
                File cachedClient = null;
                if (CustomRevison < 1) {
                    HashMap<String, ClassNode> tempClassMap = JarUtils.parseJar(new JarFile(tempCachedClient));
                    Integer RevisionNumber = JarUtils.getRevision(tempClassMap.get("client"));
                    if (tempCachedClient.exists()) {
                        System.out.println("\n//Using Client "+RevisionNumber);
                        cachedClient = new File(Configs.HOME, "client"+RevisionNumber+".jar");
                        Utils.copyFileUsingFileChannels(tempCachedClient, cachedClient); //Create Revision-Stamped client
                    } else if (JarUtils.isUpdated(tempClassMap.get("client"), jarLink)) {
                        System.out.println("\n//Downloading Client "+RevisionNumber);
                        cachedClient = new File(Configs.HOME, "client"+RevisionNumber+".jar");
                        Utils.downloadFile(jarLink, cachedClient);
                        Utils.copyFileUsingFileChannels(cachedClient, tempCachedClient); //Update client.jar for the initial revision check
                    } else {
                        System.out.println("\n//Using Client "+RevisionNumber);
                        cachedClient = new File(Configs.HOME, "client"+RevisionNumber+".jar");
                    }
                } else {
                    if (tempCachedClient.exists()) {
                        System.out.println("\n//Using Client "+CustomRevison);
                        cachedClient = new File(Configs.HOME, "client"+CustomRevison+".jar");
                    } else {
                        System.out.println(" Couldn't find client"+CustomRevison+".jar in directory AppData/Roaming/SRLUpdater/");
                        System.exit(0);
                    }
                }
                if (cachedClient.exists()) { //Only continue if the final client exists
                    HashMap<String, ClassNode> ClassMap = JarUtils.parseJar(new JarFile(cachedClient));
                    Deob deob = new Deob(ClassMap);
                    System.exit(1);
                    System.out.println(" ");
                    System.out.println("{*");
                    System.out.println("**  SRL's Un-Named Updater");
                    System.out.println("**    Developed by");
                    System.out.println("**      NKN, JJ and Krazy_Meerkat");
                    System.out.println("*}");
                    System.out.println(" ");
                    System.out.println("const");
                    if (CustomRevison > 0) {
                        System.out.println(" ReflectionRevision = '"+CustomRevison+"';");
                    } else {
                        System.out.println(" ReflectionRevision = '"+JarUtils.getRevision(ClassMap.get("client"))+"';");
                    }
                    System.out.println(" ");
                }
            }
        } catch (Exception e) {
            System.out.println("Error constructing client");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading... please check your internet connection.", "Error loading..", JOptionPane.ERROR_MESSAGE);
        }
    }
}
