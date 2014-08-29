package com.srlupdater.updater.injection.generic;

public class FieldHook {
    private final String fieldName;
    private final String fieldLocation;
    private final String fieldDesc;
    private long multiplier = -1;

    public FieldHook(String fieldName, String fieldLocation, String fieldDesc ){
        this.fieldName = fieldName;
        this.fieldLocation = fieldLocation;
        this.fieldDesc = fieldDesc;
    }
    public FieldHook(String fieldName, String fieldLocation, String fieldDesc, long multiplier ){
        this.fieldName = fieldName;
        this.fieldLocation = fieldLocation;
        this.fieldDesc = fieldDesc;
        this.multiplier = multiplier;
    }

    public String getName(){
        return fieldName;
    }

    public String getLocation(){
        return fieldLocation;
    }

    public String getReturn(){
        return fieldDesc;
    }

    public long getMultiplier(){
        return multiplier;
    }

    public void setMultiplier(long multiplier){
        this.multiplier = multiplier;
    }
}