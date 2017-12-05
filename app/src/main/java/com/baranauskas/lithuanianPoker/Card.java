package com.baranauskas.lithuanianPoker;

/**
 * Created by Administrator on 2017.11.15.
 */

public class Card {
    public int cardNameID;
    public String cardSuit;

    public Card(int x, String y) {
        this.cardNameID = x;
        this.cardSuit = y;
    }

    public int getCardNameID() { return this.cardNameID; }
    public String getCardSuit() { return this.cardSuit; }

    // return a string representation of this point
    public String toString() {
        return "(" + getCardNameID() + ", " + getCardSuit() + ")";
    }
}
