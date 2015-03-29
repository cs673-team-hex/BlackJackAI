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

/**
 *
 * @author Administrator
 */
public class BlackJackPlayRound {

    private final Player pPlayer;
    private final Player pAI;

    private Player pCurrentPlayer;
    private final CardDeck cardDeck;
    private int nMoneyOfRound;
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
        nMoneyOfRound = 50;
        UI.setBet(nMoneyOfRound);

        pPlayer.doDouble(false);

    }

    public Player getCurrentPlayer() {
        return pCurrentPlayer;
    }

    public boolean isPlayerPhase() {
        return pCurrentPlayer == pPlayer;
    }

    public void setMoneyOfRound(int nMoney) {
        //Logic
        this.nMoneyOfRound = nMoney;
        //UI
        UI.setBet(nMoney);
    }

    public void DoubleMoneyOfRound() {
        nMoneyOfRound *= 2;
        this.setMoneyOfRound(nMoneyOfRound);
    }

    public void HalfMoneyOfRound() {
        nMoneyOfRound /= 2;
        this.setMoneyOfRound(nMoneyOfRound);
    }

    public int getMoneyOfRoundth() {
        return this.nMoneyOfRound;
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
            RoundEndAIWin();
            pWinPlayer = pAI;
        } else {
            RoundEndYouWin();
            pWinPlayer = pPlayer;
        }
        UI.TerminateControlOfPlayer();
        game.PrintLog();
        return nSituation;
    }

    public void RoundEndAIWin() {
        pPlayer.LoseMoney(nMoneyOfRound);
        pAI.EarnMoney(nMoneyOfRound);

        //UI
        UI.RefreshWhenAIWin();
        UI.AskForNextRound();
    }
    
    public Player GetWinPlayer(){
        return pWinPlayer;
    }

    public void RoundEndYouWin() {
        pPlayer.EarnMoney(nMoneyOfRound);
        pAI.LoseMoney(nMoneyOfRound);

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
            this.nMoneyOfRound = nMoneyOfRound * 2;
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
