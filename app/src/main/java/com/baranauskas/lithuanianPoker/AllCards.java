package com.baranauskas.lithuanianPoker;

/**
 * Created by Administrator on 2017.11.15.
 */

public class AllCards {
    public String cardSuit;
    public int cardId;
    public boolean occupied;

    public AllCards(int x, String y) {
        this.cardId = x;
        this.cardSuit = y;
        this.occupied = false;
    }

    public int cardId() { return this.cardId(); }
    public String cardSuit() { return this.cardSuit(); }
    public boolean occupied() { return this.occupied; }
    public void setOccupied(){this.occupied = true; }
}
