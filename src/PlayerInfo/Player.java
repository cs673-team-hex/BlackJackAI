/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PlayerInfo;

import PokerGame.BlackJackRule;
import PokerDeck.Card;
import PokerGame.BlackJackPlayRound;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Administrator
 */
public class Player {

    final boolean bAI;
    int nNumCards;
    double dMoney;
    boolean bDouble;
    ArrayList<Card> CardArray;
//Constructor
    public Player(double dInitMoney, boolean bSetAI) {
        bAI = bSetAI;
        dMoney = dInitMoney;
        CardArray = new ArrayList<Card>();
        bDouble = false;
    }
//双倍
    public void doDouble(boolean b) {
        this.bDouble = b;
    }
    
    // get current balance for player from server.    
    public double getBalance() {
        double currentBalance = 0;
        JSONObject money = new JSONObject();
        try {
            currentBalance = money.getDouble("balance");
            
        } catch (JSONException ex) {
            Logger.getLogger(BlackJackPlayRound.class.getName()).log(Level.SEVERE, null, ex);
        }       
        return currentBalance;
    }
    // put current balance for player to server
    public JSONObject putBalance(double d) {
        JSONObject putCurrentBalance = new JSONObject();
        try {
            putCurrentBalance.put("balance", d);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(putCurrentBalance);
        return putCurrentBalance;
    }
    
//检查是否双倍
    public boolean AmIDouble() {
        return this.bDouble;
    }
//赢钱
    public void EarnMoney(double dInitMoney) {
        if (dInitMoney > 0) {
            this.dMoney += dInitMoney;
        }
    }

    public void LoseMoney(double dInitMoney) {
        if (dInitMoney > 0) {
            this.dMoney -= dInitMoney;
        }
        if (this.dMoney < 0) {
            this.dMoney = 0;
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
// Display the current money for player.
    public double getMoney() {
        dMoney = getBalance();
        return this.dMoney;
    }
}
