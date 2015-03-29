/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PokerDeck;

import PlayerInfo.Player;
import java.util.ArrayList;
import java.util.Collections;
import Log.Log;

/**
 *
 * @author Administrator
 */
public class CardDeck {

    //Top Card is the Last Card
    private int nNumberCard;
    private ArrayList<Card> CardList;
//Constructor
    public CardDeck(ArrayList<Card> cl) {
        CardList = new  ArrayList<Card>();
        this.nNumberCard = cl.size();
        for (Card card : cl) {
            CardList.add(card);
        }
    }
//Constructor
    public CardDeck() {
        CardList = new ArrayList<Card>();
        for (int i = 1; i <= 4; i++) {
            for (int j = 1; j <= 13; j++) {
                CardList.add(new Card(i, j));
                nNumberCard++;
            }
        }
        this.Shuffle();
    }

    public ArrayList<Card> getCardList() {
        return this.CardList;
    }

    public void ClearDeck(){
        CardList.clear();
        Log.getInstance().Log(1, "Clear the Deck");
    }
    
    public void RebuildDeck() {
        this.ClearDeck();
        for (int i = 1; i <= 4; i++) {
            for (int j = 1; j <= 13; j++) {
                CardList.add(new Card(i, j));
                nNumberCard++;
            }
        }
        this.Shuffle();
    }

    public boolean removeCardFromDeck(Card card) {
        boolean bSucc = true;
        int nIndex = CardList.indexOf(card);
        if (nIndex > 0) {
            CardList.remove(nIndex);
            nNumberCard--;
        } else {
            bSucc = false;
        }
        return bSucc;
    }

    public Card giveTopCardToPlayer(Player player) {
        if (nNumberCard == 0) {
            return new Card(1,1);
        }
        nNumberCard--;
        Card card = CardList.get(nNumberCard);
        player.getNewCard(card);
        CardList.remove(card);
        return card;
    }

    public int getNumber() {
        return this.nNumberCard;
    }

    public void Shuffle() {
        Collections.shuffle(CardList);
    }

}
