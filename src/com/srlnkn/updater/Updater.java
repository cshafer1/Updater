package com.srlnkn.updater;

import com.srlnkn.updater.utils.Configs;
import com.srlnkn.updater.utils.JarUtils;
import com.srlnkn.updater.utils.Utils;
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
                System.out.println("Jar location: " + jarLink);
                String codeName = codeMatcher.group(1).replaceAll(".class", "");
                System.out.println("Code name: " + codeName);
                System.out.println("\nLoading parameters...");
                Pattern paramRegex = Pattern.compile("<param name=\"([^\\s]+)\"\\s+value=\"([^>]*)\">");
                Matcher paramMatcher = paramRegex.matcher(pageSource);
                while (paramMatcher.find()) {
                    String key = paramMatcher.group(1);
                    String value = paramMatcher.group(2);
                    System.out.printf("%-20s %s", key, value + "\n");
                    HashMap<String, String> parameters = new HashMap<>();
                    parameters.put(key, value);
                }
                File dir = new File(Configs.HOME);
                if(!dir.exists())
                    dir.mkdir();
                File cachedClient = new File(Configs.HOME, "client.jar");
                if (!cachedClient.exists()) {
                    System.out.println("\nDownloading Client");
                    Utils.downloadFile(jarLink, cachedClient);
                    System.out.println("\nFinished Downloading");
                }
                HashMap<String, ClassNode> classMap = JarUtils.parseJar(new JarFile(cachedClient));
                if(JarUtils.isUpdated(classMap.get("client"), jarLink)){
                    System.out.println("\nRunescape Updated!\nDownloading Client");
                    Utils.downloadFile(jarLink, cachedClient);
                    System.out.println("\nFinished Downloading");
                }





            }



        } catch (Exception e) {
            System.out.println("Error constructing client");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading... please check your internet connection.", "Error loading..", JOptionPane.ERROR_MESSAGE);
        }
    }
}
