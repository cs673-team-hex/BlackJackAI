/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PlayerInfo;

import PokerGame.BlackJackRule;
import PokerDeck.Card;
import java.util.ArrayList;

/**
 *
 * @author Administrator
 */
public class Player {

    final boolean bAI;
    int nNumCards;
    int nMoney;
    boolean bDouble;
    ArrayList<Card> CardArray;

    public Player(int nInitMoney, boolean bSetAI) {
        bAI = bSetAI;
        nMoney = nInitMoney;
        CardArray = new ArrayList<Card>();
        bDouble = false;
    }

    public void doDouble(boolean b) {
        this.bDouble = b;
    }

    public boolean AmIDouble() {
        return this.bDouble;
    }

    public void EarnMoney(int nInitMoney) {
        if (nInitMoney > 0) {
            this.nMoney += nInitMoney;
        }
    }

    public void LoseMoney(int nInitMoney) {
        if (nInitMoney > 0) {
            this.nMoney -= nInitMoney;
        }
        if (this.nMoney < 0) {
            this.nMoney = 0;
        }
    }

    public ArrayList<Card> getPlayerCards() {
        return this.CardArray;
    }

    public void SendNewCardToPlayer(Card card) {
        CardArray.add(card);
    }

    public String printCardInHand() {
        StringBuffer sb = new StringBuffer();
        for (Card card : CardArray) {
            sb.append(card.printCard()).append(" ");
        }
        sb.append("Total Num: ");
        sb.append(BlackJackRule.GetMaxValueOfHand(CardArray));
        return sb.toString();
    }

    public void ResetHand() {
        CardArray.clear();
        nNumCards++;
    }

    public Card getHiddenCard() {
        if (CardArray.size() > 0) {
            return CardArray.get(0);
        } else {
            return null;
        }
    }

    public void getNewCard(Card cardNewCard) {
        CardArray.add(cardNewCard);
        nNumCards++;
    }

    public int getMoney() {
        return this.nMoney;
    }
}
