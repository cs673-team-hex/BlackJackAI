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
//Constructor
    public Player(int nInitMoney, boolean bSetAI) {
        bAI = bSetAI;
        nMoney = nInitMoney;
        CardArray = new ArrayList<Card>();
        bDouble = false;
    }
//双倍
    public void doDouble(boolean b) {
        this.bDouble = b;
    }
//检查是否双倍
    public boolean AmIDouble() {
        return this.bDouble;
    }
//赢钱
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
//得到手牌
    public ArrayList<Card> getPlayerCards() {
        return this.CardArray;
    }
//再要一张牌
    public void SendNewCardToPlayer(Card card) {
        CardArray.add(card);
    }
//显示手牌
    public String printCardInHand() {
        StringBuffer sb = new StringBuffer();
        for (Card card : CardArray) {
            sb.append(card.printCard()).append(" ");
        }
        sb.append("Total Num: ");
        sb.append(BlackJackRule.GetMaxValueOfHand(CardArray));
        return sb.toString();
    }
//清空手牌
    public void ResetHand() {
        CardArray.clear();
        nNumCards++;
    }
//把隐藏手牌show出来
    public Card getHiddenCard() {
        if (CardArray.size() > 0) {
            return CardArray.get(0);
        } else {
            return null;
        }
    }
//要牌
    public void getNewCard(Card cardNewCard) {
        CardArray.add(cardNewCard);
        nNumCards++;
    }
//显示目前金钱
    public int getMoney() {
        return this.nMoney;
    }
}
