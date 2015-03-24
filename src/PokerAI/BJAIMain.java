/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PokerAI;

import PokerDeck.Card;
import PokerDeck.CardDeck;
import PokerGame.BlackJackRule;
import java.awt.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;
import java.util.TreeMap;

/**
 *
 * @author Administrator
 */
public class BJAIMain {

    int PlayerValue; // 0 low 1 nor 2 high
    int PlayerPower; // 0 low 1 nor 2 high

    boolean getNewCard(CardDeck cardDeck, Card[] cardArray) {
        return true;
    }

    //简单的AI，在计算自己手手牌的分布的时候不考虑对手的手牌
    TreeMap<Integer, Integer> GetOpponentPointDistribution(CardDeck cdBeforeGame, ArrayList<Card> myCard, ArrayList<Card> oppCard) {

        TreeMap<Integer, Integer> mapOpp = new TreeMap<Integer, Integer>();
        int nOppNum = oppCard.size();
        if (cdBeforeGame.getNumber() < myCard.size() + nOppNum) {
            return null;
        }

        CardDeck tempDeck = new CardDeck(cdBeforeGame.getCardList());
        for (Card card : myCard) {
            tempDeck.removeCardFromDeck(card);
        }
        for (Card card : oppCard) {
            if (oppCard.indexOf(card) != 0) {
                tempDeck.removeCardFromDeck(card);
            }
        }

        //backtrack获得全排列
        for (Card card : tempDeck.getCardList()) {
            oppCard.add(card);
            int nMaxNumber = BlackJackRule.GetMaxValueOfHand(oppCard);
            oppCard.remove(card);
            if (!mapOpp.containsKey(nMaxNumber)) {
                mapOpp.put(nMaxNumber, 1);
            } else {
                mapOpp.put(nMaxNumber, mapOpp.get(nMaxNumber) + 1);
            }
        }
        return mapOpp;
    }

    TreeMap<Integer, Integer> GetDistributionOfOwnNewCard(CardDeck cdBeforeGame, ArrayList<Card> myCard, ArrayList<Card> oppCard) {

        TreeMap<Integer, Integer> mapOwn = new TreeMap<Integer, Integer>();
        int nOppNum = oppCard.size();
        if (cdBeforeGame.getNumber() < myCard.size() + nOppNum) {
            return null;
        }

        CardDeck tempDeck = new CardDeck(cdBeforeGame.getCardList());
        for (Card card : myCard) {
            tempDeck.removeCardFromDeck(card);
        }

        for (Card card : tempDeck.getCardList()) {
            myCard.add(card);
            int nMaxNumber = BlackJackRule.GetMaxValueOfHand(myCard);
            if (!mapOwn.containsKey(nMaxNumber)) {
                mapOwn.put(nMaxNumber, 1);
            } else {
                mapOwn.put(nMaxNumber, mapOwn.get(nMaxNumber) + 1);
            }
            myCard.remove(card);
        }
        return mapOwn;
    }

    TreeMap<Integer, Integer> GetDistributionOfOwn(CardDeck cdBeforeGame, ArrayList<Card> myCard, ArrayList<Card> oppCard) {

        if (cdBeforeGame.getNumber() < myCard.size() + oppCard.size()) {
            return null;
        }

        int nMaxNumber = BlackJackRule.GetMaxValueOfHand(myCard);
        TreeMap<Integer, Integer> mapOwn = new TreeMap<Integer, Integer>();
        mapOwn.put(nMaxNumber, 100);
        return mapOwn;
    }

    double[] GetWinningPoss(TreeMap<Integer, Integer> myDist, TreeMap<Integer, Integer> oppDist) {
        long nMyWin = 0;
        long nOppWin = 0;
        long nDraw = 0;
        long nTotal = 0;

        if (myDist == null || myDist.size() == 0) {
            System.out.println("Warning myDist is NULL");
            double[] winArray = {0, 0, 1};
            return winArray;
        }
        if (oppDist == null) {
            System.out.println("Warning oppDist is NULL");
            //Every Move Goes to hell;
            double[] winArray = {0, 1, 0};
            return winArray;
        }
        if (myDist.size() == 0) {
            //Every Move Goes to hell;
            System.out.println("Warning myDist size is 0");
            double[] winArray = {0, 0, 1};
            return winArray;
        }
        if (oppDist.size() == 0) {
            //Every Move Goes to hell;
            System.out.println("Warning oppDist size is 0");
            double[] winArray = {0, 1, 0};
            return winArray;
        }

        for (int myPoint : myDist.keySet()) {
            for (int oppPoint : oppDist.keySet()) {
                if (myPoint == oppPoint) {
                    nDraw += myDist.get(myPoint) * oppDist.get(oppPoint);
                } else if (myPoint > oppPoint) {
                    nMyWin += myDist.get(myPoint) * oppDist.get(oppPoint);
                } else {
                    nOppWin += myDist.get(myPoint) * oppDist.get(oppPoint);
                }
                nTotal += myDist.get(myPoint) * oppDist.get(oppPoint);
            }
        }
        double resultArray[] = {(double) nMyWin / (double) nTotal, (double) nDraw / (double) nTotal,
            (double) nOppWin / (double) nTotal};

        return resultArray;
    }

    //Level SB AI 完全胡逼完
    public boolean doMakeDecisionLevelSB(CardDeck cdBeforeGame, ArrayList<Card> myCard, ArrayList<Card> oppCard) {
        return Math.random() < 0.5;
    }

    //Level 0 AI 主要小于13我就抓牌
    public boolean doMakeDecisionLevel0(CardDeck cdBeforeGame, ArrayList<Card> myCard, ArrayList<Card> oppCard) {
        if (BlackJackRule.GetMaxValueOfHand(myCard) < 18 && BlackJackRule.GetMaxValueOfHand(myCard) != -1) {
            return true;
        } else {
            return false;
        }
    }

    //Level 1 AI 不关注抓牌完对方手牌的变化,只要胜率能提高我就抓牌
    public boolean doMakeDecisionLevel1(CardDeck cdBeforeGame, ArrayList<Card> myCard, ArrayList<Card> oppCard) {
        TreeMap<Integer, Integer> myTreeMap = GetDistributionOfOwnNewCard(cdBeforeGame, myCard, oppCard);
        TreeMap<Integer, Integer> myTreeMapWhenStop = GetDistributionOfOwn(cdBeforeGame, myCard, oppCard);
        TreeMap<Integer, Integer> oppTreeMap = GetOpponentPointDistribution(cdBeforeGame, myCard, oppCard);

        double[] resultArray = GetWinningPoss(myTreeMap, oppTreeMap);
        double[] resultWhenStop = GetWinningPoss(myTreeMapWhenStop, oppTreeMap);

        if ((resultArray[0] - resultArray[2]) > (resultWhenStop[0] - resultWhenStop[2])) {
            return true;
        } else {
            return false;
        }
    }

}
