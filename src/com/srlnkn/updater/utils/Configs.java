package com.srlnkn.updater.utils;

/**
 * @Author : NKN
 */
public class Configs {
    public static final String HOME;


    static {
        HOME = System.getenv("appdata") + "/SRLUpdater/";
    }
}
