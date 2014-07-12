package com.srlnkn.updater.injection.generic;




import java.util.HashMap;

public class Hook {
    private final HashMap<String, FieldHook> fieldHooks = new HashMap<>();
    private final String classLocation;
    private final String className;


    public Hook(String className, String classLocation){
        this.className = className;
        this.classLocation = classLocation;
    }

    public void addFieldHook(String fieldName, String fieldLocation, String fieldReturn){
        fieldHooks.put(fieldName,new FieldHook(fieldName, fieldLocation, fieldReturn));
    }

    public void addFieldHook(String fieldName, String fieldLocation, String fieldReturn, int multiplier){
        fieldHooks.put(fieldName, new FieldHook(fieldName, fieldLocation, fieldReturn, multiplier));
    }

    public HashMap<String, FieldHook> getFieldHooks(){
        return fieldHooks;
    }

    public String getClassName(){
        return className;
    }

    public String getClassLocation(){
        return classLocation;
    }

}