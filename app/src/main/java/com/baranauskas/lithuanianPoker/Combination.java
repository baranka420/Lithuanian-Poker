package com.baranauskas.lithuanianPoker;

/**
 * Created by Mok on 2017-11-21.
 */

public class Combination {
    int value;
    String name;
    int combinationNumber;
    int combinationNumber2;
    String combinationSuits;

    public Combination(int val, String nam, int number1, int number2, String suits){
        this.value = val;
        this.name = nam;
        this.combinationNumber = number1;
        this.combinationNumber2 = number2;
        this.combinationSuits = suits;
    }
    public void setValue(int val){this.value = val;}
    public void setName(String nam){this.name = nam;}
    public int getValue() {return this.value;}
    public String getName() {return this.name;}
    public int getCombinationNumber() {return this.combinationNumber;}
    public int getCombinationNumber2() {return this.combinationNumber2;}
    public String getCombinationSuits() {return this.combinationSuits;}
}
