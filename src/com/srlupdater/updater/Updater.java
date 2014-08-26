package com.srlupdater.updater;

import com.srlupdater.deob.Deob;
import com.srlupdater.updater.injection.analyzers.NodeAnalyzer;
import com.srlupdater.updater.injection.generic.AbstractAnalyzer;
import com.srlupdater.updater.injection.generic.FieldHook;
import com.srlupdater.updater.injection.generic.Hook;
import com.srlupdater.updater.utils.Configs;
import com.srlupdater.updater.utils.JarUtils;
import com.srlupdater.updater.utils.Utils;
import org.objectweb.asm.tree.ClassNode;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Developers : NKN & Krazy Meerkat
 * Contributors: Frement, JJ & 200_success
 */
public class Updater {

    private Integer CustomRevison = 54; //This must be left at 0 when not in use.
    private boolean useOutput = false; //Used to quickly debug.
    public static boolean dumpClasses = false; //Control Flow Deob won't be used when dumping .Jar
    private ArrayList<Hook> hooks = new ArrayList<>();
    private Hook analyzerHook;
    public Updater(){
        try {
            File cachedClient = null;
            if(!useOutput) {
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
                    if (!dir.exists())
                        dir.mkdir();
                    File tempCachedClient = new File(Configs.HOME, "client.jar");
                    if (CustomRevison > 0) {
                        tempCachedClient = new File(Configs.HOME, "client" + CustomRevison + ".jar");
                    }
                    if ((!tempCachedClient.exists()) && (CustomRevison < 1)) {
                        System.out.println("\n{Downloading Initial Client}");
                        Utils.downloadFile(jarLink, tempCachedClient);
                    }

                    if (CustomRevison < 1) {
                        HashMap<String, ClassNode> tempClassMap = JarUtils.parseJar(new JarFile(tempCachedClient));
                        Integer RevisionNumber = JarUtils.getRevision(tempClassMap.get("client"));
                        cachedClient = new File(Configs.HOME, "client" + RevisionNumber + ".jar");
                        if (tempCachedClient.exists() && !cachedClient.exists()) {
                            System.out.println("\n{Using Client " + RevisionNumber + "}");
                            cachedClient = new File(Configs.HOME, "client" + RevisionNumber + ".jar");
                            Utils.copyFileUsingFileChannels(tempCachedClient, cachedClient); //Create Revision-Stamped client
                        } else if (JarUtils.isUpdated(tempClassMap.get("client"), jarLink)) {
                            System.out.println("\n{Downloading Client " + ++RevisionNumber + "}");
                            cachedClient = new File(Configs.HOME, "client" + RevisionNumber + ".jar");
                            Utils.downloadFile(jarLink, cachedClient);
                            Utils.copyFileUsingFileChannels(cachedClient, tempCachedClient); //Update client.jar for the initial revision check
                        } else {
                            System.out.println("\n{Using Client " + RevisionNumber + "}");
                        }
                    } else {
                        if (tempCachedClient.exists()) {
                            System.out.println("\n{Using Client " + CustomRevison + "}");
                            cachedClient = new File(Configs.HOME, "client" + CustomRevison + ".jar");
                        } else {
                            System.out.println(" Couldn't find client" + CustomRevison + ".jar in directory AppData/Roaming/SRLUpdater/");
                            System.exit(0);
                        }
                    }

                }
            }
            else
                cachedClient = new File(Configs.HOME, "output.jar");
            System.out.println(cachedClient.exists());
            if (cachedClient.exists()) { //Only continue if the final client exists
                HashMap<String, ClassNode> ClassMap = JarUtils.parseJar(new JarFile(cachedClient));
                if(!useOutput) {
                    Deob deob = new Deob(ClassMap);
                    ClassMap = deob.run();
                }
                System.out.println(" ");
                System.out.println("{*");
                System.out.println("**  SRL's Un-Named Updater");
                System.out.println("**    Developed by");
                System.out.println("**      NKN, JJ and Krazy_Meerkat");
                System.out.println("*}");
                System.out.println(" ");
                runAnalyzers(ClassMap);
                for(Hook hook:hooks){
                    System.out.println("Class Hook: "+hook.getClassName() + "  " + hook.getClassLocation());
                    HashMap<String, FieldHook> hooks = hook.getFieldHooks();

                    for(Map.Entry<String, FieldHook> entry : hooks.entrySet()){
                        FieldHook fHook = entry.getValue();
                        System.out.println("   Field Hook: " +fHook.getLocation() + "  " + fHook.getName() + "  " + fHook.getReturn() + "   " + fHook.getMultiplier());
                    }
                }
                System.out.println("const");
                if (CustomRevison > 0) {
                    System.out.println(" ReflectionRevision = '" + CustomRevison + "';");
                } else {
                    System.out.println(" ReflectionRevision = '" + JarUtils.getRevision(ClassMap.get("client")) + "';");
                }
                System.out.println(" ");
            }
        } catch (Exception e) {
            System.out.println("Error constructing client");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading... please check your internet connection.", "Error loading..", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void runAnalyzers(HashMap<String, ClassNode> classMap){
        System.out.println("\nAnalyzing has started!");


        ArrayList<AbstractAnalyzer> analyzers = new ArrayList<>();


        analyzers.add(new NodeAnalyzer());


        Collection<ClassNode> classNodes = classMap.values();
        for(AbstractAnalyzer analyzer : analyzers){
            for(ClassNode classNode : classNodes){
                analyzerHook = analyzer.run(classNode);
                if(analyzerHook != null){
                    hooks.add(analyzerHook);
                }
            }
        }
    }
}
