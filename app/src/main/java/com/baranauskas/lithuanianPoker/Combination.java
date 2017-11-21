package com.baranauskas.lithuanianPoker;

/**
 * Created by Mok on 2017-11-21.
 */

public class Combination {
    int value;
    String name;

    public Combination(int val, String nam){
        this.value = val;
        this.name = nam;
    }
    public void setValue(int val){this.value = val;}
    public void setName(String nam){this.name = nam;}
    public int getValue() {return this.value;}
    public String getName() {return this.name;}
}
