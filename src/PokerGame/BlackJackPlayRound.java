/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PokerGame;

import PokerAI.BJAIMain;
import PokerDeck.Card;
import PokerDeck.CardDeck;
import PlayerInfo.Player;
import UI.BlackJackUINew;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Administrator
 */
public class BlackJackPlayRound {

    private final Player pPlayer;
    private final Player pAI;

    private Player pCurrentPlayer;
    private final CardDeck cardDeck;
    private double dMoneyOfRound;
    private Player pWinPlayer;

    BlackJackUINew UI;
    BlackJackPlay game;

    public BlackJackPlayRound(Player A, Player B, CardDeck d, BlackJackUINew ui, BlackJackPlay GAME) {
        pPlayer = A;
        pAI = B;
        cardDeck = d;

        UI = ui;
        pCurrentPlayer = pPlayer;
        game = GAME;
        dMoneyOfRound = 50;
        UI.setBet(dMoneyOfRound);

        pPlayer.doDouble(false);

    }

    
    public Player getCurrentPlayer() {
        return pCurrentPlayer;
    }

    public boolean isPlayerPhase() {
        return pCurrentPlayer == pPlayer;
    }

    public void setMoneyOfRound(double nMoney) {
        //Logic
        this.dMoneyOfRound = nMoney;
        //UI
        UI.setBet(nMoney);
    }

    public void DoubleMoneyOfRound() {
        dMoneyOfRound *= 2;
        this.setMoneyOfRound(dMoneyOfRound);
    }

    public void HalfMoneyOfRound() {
        dMoneyOfRound /= 2;
        this.setMoneyOfRound(dMoneyOfRound);
    }

    public double getMoneyOfRoundth() {
        return this.dMoneyOfRound;
    }

    public void SendFirstTwoCardsToBothPlayer() throws InterruptedException {

        //Logic
        Card AICard1 = cardDeck.giveTopCardToPlayer(pAI);
        Card AICard2 = cardDeck.giveTopCardToPlayer(pAI);

        Card playerCard1 = cardDeck.giveTopCardToPlayer(pPlayer);
        Card playerCard2 = cardDeck.giveTopCardToPlayer(pPlayer);
        //UI
        if (pAI.getPlayerCards().size() != 2) {
            System.out.println("ERROR! Card Number Error by Player");
            return;
        }

        UI.SendCardToPosition(true, AICard1, 1, false);
        UI.SendCardToPosition(true, AICard2, 2, true);

        UI.SendCardToPosition(false, playerCard1, 1, true);
        UI.SendCardToPosition(false, playerCard2, 2, true);

        UI.RefreshNumOfPlayerHand();

    }

    public int RoundEndByPlayer() {
        return RoundEnd(BlackJackRule.GetBlackJackResult(pPlayer, pAI));
    }

    //Return Situation: 
    //10 AI BlackJack AI win
    //-10 Player BlackJack PlayerWin
    //20 AI win 5 Dragons AI win
    //-20 player win 5 Dragons Player Win
    //30 AI bigger or equal with player AI win
    //-30 player bigger than AI Player Win
    //100 Player Surrender so AI win
    public int RoundEnd(int nSituation) {

        if (nSituation > 0) {
            try {
                RoundEndAIWin();
            } catch (JSONException ex) {
                Logger.getLogger(BlackJackPlayRound.class.getName()).log(Level.SEVERE, null, ex);
            }
            pWinPlayer = pAI;
        } else {
            RoundEndYouWin();
            pWinPlayer = pPlayer;
        }
        UI.TerminateControlOfPlayer();
        game.PrintLog();
        pPlayer.putBalance(pPlayer.getMoney());
        return nSituation;
    }

    public void RoundEndAIWin() throws JSONException {
        pPlayer.LoseMoney(dMoneyOfRound);
        pAI.EarnMoney(dMoneyOfRound);
        
        //Send the amount of lost money to sever.
        JSONObject putLost = new JSONObject();
        try {
            putLost.put("winning", dMoneyOfRound);
        } catch (JSONException ex) {
            Logger.getLogger(BlackJackPlayRound.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        //UI
        UI.RefreshWhenAIWin();
        UI.AskForNextRound();
    }
    
    public Player GetWinPlayer(){
        return pWinPlayer;
    }

    public void RoundEndYouWin() {
        pPlayer.EarnMoney(dMoneyOfRound);
        pAI.LoseMoney(dMoneyOfRound);
        
        //Send the amount of winned money to sever.
        JSONObject putWin = new JSONObject();
        try {
            putWin.put("winning", dMoneyOfRound);
        } catch (JSONException ex) {
            Logger.getLogger(BlackJackPlayRound.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        //UI
        UI.RefreshWhenYouWin();
        UI.AskForNextRound();
    }

    public void PlayerPhase() {
        PlayerDoublePhase();
        return;
    }

    public void PlayerDoublePhase() {
        if (DecidePlayerDoubleFromAI()) {
            this.dMoneyOfRound = dMoneyOfRound * 2;
        }
    }

    public boolean DecidePlayerDoubleFromAI() {
        return BlackJackRule.GetMaxValueOfHand(pPlayer.getPlayerCards()) > 14;
    }

    public boolean DecidePlayerDoubleFromUI() {
        return true;
    }

    public Card PlayerHit() {
        Card card = cardDeck.giveTopCardToPlayer(pPlayer);
        UI.SendCardToPosition(false, card, pPlayer.getPlayerCards().size(), true);
        UI.RefreshNumOfPlayerHand();

        //TODO player Bust
        if (BlackJackRule.AmIBust(pPlayer)) {
            ;
        }
        return card;
    }

    public void AIPhase() {
        BJAIMain aiMain = new BJAIMain();
        pCurrentPlayer = pPlayer;

        while (BlackJackRule.GetMaxValueOfHand(pAI.getPlayerCards()) < 17) {

            Card card = cardDeck.giveTopCardToPlayer(pAI);
            UI.SendCardToPosition(true, card, pAI.getPlayerCards().size(), true);

            if (BlackJackRule.AmIBust(pAI)) {
                //AI Bust by 17 Rule
                RoundEndByPlayer();
                return;
            }
        }

        while (aiMain.doMakeDecisionLevel1(cardDeck, pAI.getPlayerCards(), pPlayer.getPlayerCards())) {
            cardDeck.giveTopCardToPlayer(pAI);

            if (BlackJackRule.AmIBust(pAI.getPlayerCards())) {
                //AI Bust by BAD decison
                RoundEndByPlayer();
                return;
            }
        }

        //AI survive without Bust
        RoundEndByPlayer();
    }
}
