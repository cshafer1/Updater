package com.srlupdater.updater.utils;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ListIterator;
import java.util.Random;


public final class Utils{

    private static final Random random = new Random();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyy-hh-mm-ss");
    private static Utils instance = new Utils();

    /**
     * Private constructor so the user can't create instances
     */
    private Utils() {
    }

    /**
     * Returns the one and only instance of this class
     *
     * @return Utilities instance
     */
    public static Utils getInstance() {
        return instance;
    }


    /**
     *
     * @param mn  Method node we're searching
     * @param pat Pattern to look for
     * @return    Array of found instructions
     * Adapted from n3ss3s's work here:
     * http://javahacking.org/forum/index.php?/topic/440-following-gotos/
     */
    public static AbstractInsnNode[] match(MethodNode mn, AbstractInsnNode[] pat){
        ListIterator<AbstractInsnNode> instructionList = mn.instructions.iterator();
        AbstractInsnNode instruction;
        jmp:while(instructionList.hasNext()){
            instruction = instructionList.next();
            AbstractInsnNode[] found = new AbstractInsnNode[pat.length];
            for(int i = 0;i<pat.length;i++){
                if(instruction instanceof JumpInsnNode){
                    instruction = mn.instructions.get(mn.instructions.indexOf((JumpInsnNode)((JumpInsnNode) instruction).label.getNext()));
                }
                if(instruction.getOpcode() != pat[i].getOpcode())
                    continue jmp;
                found[i] = instruction;
            }
            return found;
        }
        return null;

    }

    /**
     * Generates a random number between min and max
     *
     * @param min minimum number
     * @param max maximum number
     * @return random number between min and max
     */




    public static int random(int min, int max) {
        return random.nextInt(Math.abs(max - min)) + min;
    }


    public static String opcodeToString(int opc) {
                Field[] declaredFields = Opcodes.class.getDeclaredFields();
                for (Field field : declaredFields) {
                        field.setAccessible(true);

                                try {
                                if (field.getInt(Opcodes.class.getClass()) == opc) {
                                        return field.getName();
                                    }
                            } catch (Exception e) {}
                    }
                return "";
    }

    /**
     * Sleeps for a certain amount of milliseconds
     *
     * @param ms amount of milliseconds to sleep
     */
    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            System.out.println("Error while trying to sleep: " + ex.getMessage());
        }
    }
    public static void copyFileUsingFileChannels(File source, File dest)
            throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }


    public static boolean isConstructorDescriptor(ClassNode classNode, String desc) {
        ListIterator<MethodNode> mnli = classNode.methods.listIterator();
        while (mnli.hasNext()) {
            MethodNode mn = mnli.next();
            if (mn.name.equals("<init>"))
                if (mn.desc.contains(desc))
                    return true;
        }
        return false;
    }

    /**
     * Sleeps for a random amount of time between min and max
     *
     * @param min minimum number
     * @param max maximum number
     */
    public static void sleep(int min, int max) {
        sleep(random(min, max));
    }


    /**
     * Returns the current date
     */
    private static Date getDate() {
        return Calendar.getInstance().getTime();
    }

    /**
     * Saves an image in the screenshots folder
     *
     * @param img the image to save
     */
    public static void saveImage(BufferedImage img) {
        try {
            File directory = new File("Screenshots/");
            if (!directory.exists()) {
                directory.mkdir();
            }

            if (directory.exists()) {
                File path = new File("Screenshots/" + dateFormat.format(getDate()) + ".png");
                ImageIO.write(img, "png", path);
            }
        } catch (Exception ex) {
            System.out.println("Error while trying to save an image: " + ex.getMessage());
        }
    }

    private static URLConnection createConnection(URL url) throws IOException {
        URLConnection con = url.openConnection();
        con.addRequestProperty("Protocol", "HTTP/1.1");
        con.addRequestProperty("Connection", "keep-alive");
        con.addRequestProperty("Keep-Alive", "200");
        con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:9.0.1) Gecko/20100101 Firefox/9.0.1");
        return con;
    }

    /**
     * Loads a page from the given link
     *
     * @param link link to the page
     * @return page of the link
     */
    public static String getPage(String link) {
        try {
            URLConnection con = createConnection(new URL(link));
            byte[] buffer = new byte[con.getContentLength()];
            try (DataInputStream stream = new DataInputStream(con.getInputStream())) {
                stream.readFully(buffer);
            }
            return new String(buffer);
        } catch (Exception ex) {
            System.out.println("Error while trying to get a page: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Attempts to download a file from a certain link
     *
     * @param link link to the file
     * @param outputFile path to save
     */
    public static void downloadFile(String link, String outputFile) {
        downloadFile(link, new File(outputFile));
    }

    public static void downloadFile(String url, File output) {
        try {
            URLConnection con = createConnection(new URL(url));
            try (FileOutputStream fos = new FileOutputStream(output); InputStream in = con.getInputStream()) {
                byte[] buffer = new byte[1024];
                for (int i; (i = in.read(buffer)) != -1; ) {
                    fos.write(buffer, 0, i);
                }
            }
        } catch (Exception ex) {
            System.out.println("Error while downloading file: " + ex.getMessage());
        }
    }



    /**
     * Scales the given image to the input width/height
     *
     * @param width  new width
     * @param height new height
     * @param img    img to resize
     * @return scaled image
     */
    public static Image scaleImage(int width, int height, Image img) {
        return img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    /**
     * Scales the given image icon to the input width/height
     *
     * @param width  new width
     * @param height new height
     * @param img    image icon to resize
     * @return scaled image icon
     */
    public static ImageIcon scaleIcon(int width, int height, ImageIcon img) {
        return new ImageIcon(scaleImage(width, height, img.getImage()));
    }

    /**
     * Loads an image from our resources
     *
     * @param resourcePath path to the image
     * @return image from the resource path
     */
    public Image loadResourceImage(String resourcePath) {
        try {
            return ImageIO.read(getClass().getResource(resourcePath));
        } catch (Exception ex) {
            System.out.println("Error while loading image from resources: " + ex.getMessage());
        }
        return null;
    }

    /**
     * Creates a gray scaled variant of the given image
     *
     * @param img     image to grayscale
     * @param percent grayscale percentage
     * @return grayscaled image
     */
    public static Image grayScaleImage(Image img, int percent) {
        ImageProducer producer = new FilteredImageSource(img.getSource(), new GrayFilter(true, percent));
        return Toolkit.getDefaultToolkit().createImage(producer);
    }

}
