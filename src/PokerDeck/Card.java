/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PokerDeck;

/**
 *
 * @author Administrator
 */
public class Card {

    private int nNum;
    private int nColor;
    private boolean bFaceDown;
//Constructor
    public Card(int nC, int nN) {
        nNum = nN;
        nColor = nC;
        bFaceDown = true;
    }

    public String printCard() {
        return this.getVisualColorOfCard() + this.getVisualNumberOfCard();
    }

    public String getCardNameFromPNG() {
        return this.getVisualColorOfCard() + this.nNum;
    }

    public void setNum(int num) {
        this.nNum = num;
    }

    public int getNum() {
        return this.nNum;
    }

    public void setColor(int color) {
        this.nColor = color;
    }

    public int getColor(int color) {
        return this.nColor;
    }

    public boolean validateCard() {
        //TODO Card Validation
        return true;
    }

    public String getVisualColorOfCard() {
        if (this.nColor == 1) {
            return "Club";
        } else if (this.nColor == 2) {
            return "Diamoud";
        } else if (this.nColor == 3) {
            return "Heart";
        } else {
            return "Spade";
        }
    }

    public String getVisualNumberOfCard() {
        if (this.nNum == 1) {
            return "A";
        } else if (this.nNum == 11) {
            return "J";
        } else if (this.nNum == 12) {
            return "Q";
        } else if (this.nNum == 13) {
            return "K";
        } else if (this.nNum == 10) {
            return "10";
        } else {
            char x = (char) ('0' + this.nNum);
            return String.valueOf(x);
        }
    }

    public int getBlackJackNumberOfCard() {
        if (this.nNum == 1) {
            return 11;
        } else if (this.nNum >= 10) {
            return 10;
        } else {
            return this.nNum;
        }
    }
}
