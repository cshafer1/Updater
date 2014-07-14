package com.srlnkn.updater.injection.generic;

public class FieldHook {
    private final String fieldName;
    private final String fieldLocation;
    private final String fieldReturn;
    private long multiplier = -1;

    public FieldHook(String fieldName, String fieldLocation, String fieldReturn ){
        this.fieldName = fieldName;
        this.fieldLocation = fieldLocation;
        this.fieldReturn = fieldReturn;
    }
    public FieldHook(String fieldName, String fieldLocation, String fieldReturn, long multiplier ){
        this.fieldName = fieldName;
        this.fieldLocation = fieldLocation;
        this.fieldReturn = fieldReturn;
        this.multiplier = multiplier;
    }

    public String getName(){
        return fieldName;
    }

    public String getLocation(){
        return fieldLocation;
    }

    public String getReturn(){
        return fieldReturn;
    }

    public long getMultiplier(){
        return multiplier;
    }

    public void setMultiplier(long multiplier){
        this.multiplier = multiplier;
    }
}