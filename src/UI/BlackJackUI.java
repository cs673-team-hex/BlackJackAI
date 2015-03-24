/*
 * Copyright (c) 2010, Oracle. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of Oracle nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package UI;

import Log.Log;
import PlayerInfo.Player;
import PokerDeck.Card;
import PokerDeck.CardDeck;
import PokerGame.BlackJackPlay;
import PokerGame.BlackJackPlayRound;
import PokerGame.BlackJackRule;
import java.sql.Time;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class BlackJackUI extends javax.swing.JFrame {

    /**
     * Creates new form Antenna
     */
    ArrayList<JButton> AICardList = new ArrayList<JButton>();
    ArrayList<JButton> YourCardList = new ArrayList<JButton>();
    ArrayList<JButton> YourActionList = new ArrayList<JButton>();
    BlackJackPlay game;
    BlackJackPlayRound round;

    public BlackJackUI() {
        initComponents();

        AICardList.add(jAICard1);
        AICardList.add(jAICard2);
        AICardList.add(jAICard3);
        AICardList.add(jAICard4);
        AICardList.add(jAICard5);

        YourCardList.add(jYourCard1);
        YourCardList.add(jYourCard2);
        YourCardList.add(jYourCard3);
        YourCardList.add(jYourCard4);
        YourCardList.add(jYourCard5);

        YourActionList.add(jHit);
        YourActionList.add(jStand);
        YourActionList.add(jSurrender);
        YourActionList.add(jDouble);

    }

    public void DoSomethingAtBegin() throws MessagingException {
        InitialBoardsBetweenRounds();
        //GameAI
        game = new BlackJackPlay(this);
        try {
            game.GameBegin();

        } catch (InterruptedException ex) {
            Logger.getLogger(BlackJackUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void AskForNextRound() {
        jNextRound.setVisible(true);
    }
     

    public void setRoundInfo(BlackJackPlayRound bjPlayRound) {
        round = bjPlayRound;
        RefreshRoundNumber();
    }

    public void RefreshRoundNumber() {
        jRound.setText("Round " + game.getNumOfRound());
    }
    
    public void RefreshLog(String strContent){
        jLog.setText(strContent);   
    }
    
    public void GameEndProcedure() throws MessagingException
    {
        String strTime = String.valueOf(System.currentTimeMillis());
        Log.getInstance().Log(1, "Player ID:" + strTime);
        RefreshLog(Log.getInstance().getLog());
        
        JOptionPane.showMessageDialog(this, "Press Ok to Send E-Mail(Takes 20s),Your ID: " + strTime + "\n", "Thanks!", JOptionPane.INFORMATION_MESSAGE);
        Log.getInstance().MailLog();
        Log.getInstance().ClearLog();
        CleanLog();
        //New Game!!!
        DoSomethingAtBegin();
    }

    public void InitialBoardsBetweenRounds() {

        String strCardPath = "/PokerCardImage/Empty.png";
        ImageIcon icon = new ImageIcon(getClass().getResource(strCardPath));

        for (JButton jbutton : AICardList) {
            jbutton.setIcon(icon);
        }
        for (JButton jbutton : YourCardList) {
            jbutton.setIcon(icon);
        }

        jYourCard4.setIcon(icon);

        jAIBlackJack.setVisible(false);
        jYouBlackJack.setVisible(false);
        jAIWin.setVisible(false);
        jYouWin.setVisible(false);
        jNextRound.setVisible(false);

        RefreshNumOfPlayerHand();
        HideAIHand();
    }
   
    public void RefreshMoneyOfBothPlayer() {
        //TODO should get money from DB,
        //dangerous if not modified
        if (game == null) {
            jAIMoney.setText(String.valueOf(1000));
            jYourMoney.setText(String.valueOf(1000));
        } else {
            jAIMoney.setText(String.valueOf(game.getAI().getMoney()));
            jYourMoney.setText(String.valueOf(game.getPlayer().getMoney()));
        }
    }

    public void TerminateControlOfPlayer() {
        for (JButton button : YourActionList) {
            button.setEnabled(false);
        }
    }

    public void RestoreControlOfPlayer() {
        for (JButton button : YourActionList) {
            button.setEnabled(true);
        }
    }

    public void HideAIHand() {
        jAIScore.setText(String.valueOf("0"));
    }

    public void RefreshNumOfPlayerHand() {
        if (game != null) {
            Player player = game.getPlayer();
            int nNumber = BlackJackRule.GetMaxValueOfHand(player);
            jYourScore.setText(String.valueOf(nNumber));
        }
    }

    public void RefreshNumOfAIHand() {
        Player player = game.getAI();
        int nNumber = BlackJackRule.GetMaxValueOfHand(player);
        jAIScore.setText(String.valueOf(nNumber));
    }

    //不处理逻辑，只处理AI，逻辑在底层实现,Position从1到5
    public void SendCardToPosition(boolean bAI, Card card, int nPosition, boolean bFaceup) {
        if (nPosition > 5 || nPosition < 1) {
            return;
        }

        JButton jAIButton = new JButton();
        String cardAddress = "";
        if (!bFaceup) {
            cardAddress = "/PokerCardImage/Back.png";
        }
        switch (nPosition) {
            case 1:
                jAIButton = bAI ? jAICard1 : jYourCard1;
                break;
            case 2:
                jAIButton = bAI ? jAICard2 : jYourCard2;
                break;
            case 3:
                jAIButton = bAI ? jAICard3 : jYourCard3;
                break;
            case 4:
                jAIButton = bAI ? jAICard4 : jYourCard4;
                break;
            case 5:
                jAIButton = bAI ? jAICard5 : jYourCard5;
                break;
            default:
                throw new AssertionError();
        }
        //jAICard1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PokerCardImage/cic.png")));
        if (bFaceup) {
            cardAddress = "/PokerCardImage/" + card.getCardNameFromPNG() + ".png";
        }

        jAIButton.setIcon(
                new javax.swing.ImageIcon(getClass().getResource(cardAddress)));
    }

    public void setBet(int nBet) {
        jBet.setText(String.valueOf(nBet));
    }

    public void RefreshWhenAIWin() {
        jAIWin.setVisible(true);
        SendCardToPosition(true, game.getAI().getHiddenCard(), 1, true);

        RefreshNumOfAIHand();
        RefreshMoneyOfBothPlayer();
    }

    public void RefreshWhenYouWin() {
        jYouWin.setVisible(true);
        SendCardToPosition(true, game.getAI().getHiddenCard(), 1, true);

        RefreshNumOfAIHand();
        RefreshMoneyOfBothPlayer();
    }

    public void DisableDouble() {
        jDouble.setEnabled(false);
    }

    public void DisableSurrender() {
        jSurrender.setEnabled(false);
    }
    public void DisableStand(){
        jStand.setEnabled(false);
    }
    
    public void AppendLog(String strNew)
    {
        jLog.append(strNew);
    }
    
    public void CleanLog()
    {
        jLog.setText("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jAIScore2 = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jLog = new javax.swing.JTextArea();
        jStand = new javax.swing.JButton();
        jDouble = new javax.swing.JButton();
        jSurrender = new javax.swing.JButton();
        jTextField10 = new javax.swing.JTextField();
        jBet = new javax.swing.JTextField();
        jHit = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jAICard1 = new javax.swing.JButton();
        jAICard2 = new javax.swing.JButton();
        jAICard5 = new javax.swing.JButton();
        jAICard4 = new javax.swing.JButton();
        jAICard3 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jAIMoney = new javax.swing.JTextField();
        jAIBlackJack = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jAIScore = new javax.swing.JTextField();
        jYouBlackJack = new javax.swing.JTextField();
        jTextField11 = new javax.swing.JTextField();
        jYourScore = new javax.swing.JTextField();
        jYourCard5 = new javax.swing.JButton();
        jYourCard4 = new javax.swing.JButton();
        jYourCard3 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        jYourMoney = new javax.swing.JTextField();
        jYourCard1 = new javax.swing.JButton();
        jYourCard2 = new javax.swing.JButton();
        jYouWin = new javax.swing.JTextField();
        jAIWin = new javax.swing.JTextField();
        jNextRound = new javax.swing.JButton();
        jRound = new javax.swing.JTextField();

        jAIScore2.setEditable(false);
        jAIScore2.setFont(new java.awt.Font("宋体", 1, 24)); // NOI18N
        jAIScore2.setText("0");
        jAIScore2.setEnabled(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BlackJack A.I. by HEX");
        setAlwaysOnTop(true);
        setResizable(false);

        jLog.setEditable(false);
        jLog.setColumns(20);
        jLog.setRows(5);
        jScrollPane1.setViewportView(jLog);

        jStand.setFont(new java.awt.Font("宋体", 1, 24)); // NOI18N
        jStand.setText("STAND");
        jStand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStandActionPerformed(evt);
            }
        });

        jDouble.setFont(new java.awt.Font("宋体", 1, 24)); // NOI18N
        jDouble.setText("DOUBLE");
        jDouble.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDoubleActionPerformed(evt);
            }
        });

        jSurrender.setFont(new java.awt.Font("宋体", 1, 24)); // NOI18N
        jSurrender.setText("SURRENDER");
        jSurrender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSurrenderActionPerformed(evt);
            }
        });

        jTextField10.setEditable(false);
        jTextField10.setFont(new java.awt.Font("宋体", 1, 24)); // NOI18N
        jTextField10.setText("Bet:");
        jTextField10.setFocusable(false);
        jTextField10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField10ActionPerformed(evt);
            }
        });

        jBet.setEditable(false);
        jBet.setFont(new java.awt.Font("宋体", 1, 24)); // NOI18N
        jBet.setText("50");
        jBet.setEnabled(false);

        jHit.setFont(new java.awt.Font("宋体", 1, 24)); // NOI18N
        jHit.setText("HIT");
        jHit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jHitActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("宋体", 1, 18)); // NOI18N
        jButton1.setText("E-Mail LOG");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jStand, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jDouble, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jSurrender, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jButton1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(jTextField10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 66, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jBet, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(6, 6, 6))
                    .add(jHit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 374, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 138, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jTextField10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jBet, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jButton1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(11, 11, 11)
                .add(jHit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jStand, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDouble, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jSurrender, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(63, 63, 63))
        );

        jTextField1.setEditable(false);
        jTextField1.setFont(new java.awt.Font("宋体", 1, 14)); // NOI18N
        jTextField1.setText("MoneyTotal:");
        jTextField1.setFocusable(false);

        jAIMoney.setEditable(false);
        jAIMoney.setFont(new java.awt.Font("宋体", 1, 14)); // NOI18N
        jAIMoney.setFocusable(false);

        jAIBlackJack.setEditable(false);
        jAIBlackJack.setFont(new java.awt.Font("宋体", 1, 24)); // NOI18N
        jAIBlackJack.setText("BLACK JACK!");
        jAIBlackJack.setFocusable(false);

        jTextField9.setEditable(false);
        jTextField9.setFont(new java.awt.Font("宋体", 1, 24)); // NOI18N
        jTextField9.setText("Score:");
        jTextField9.setFocusable(false);
        jTextField9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField9ActionPerformed(evt);
            }
        });

        jAIScore.setEditable(false);
        jAIScore.setFont(new java.awt.Font("宋体", 1, 24)); // NOI18N
        jAIScore.setText("0");
        jAIScore.setEnabled(false);

        jYouBlackJack.setEditable(false);
        jYouBlackJack.setFont(new java.awt.Font("宋体", 1, 24)); // NOI18N
        jYouBlackJack.setText("BLACK JACK!");
        jYouBlackJack.setFocusable(false);

        jTextField11.setEditable(false);
        jTextField11.setFont(new java.awt.Font("宋体", 1, 24)); // NOI18N
        jTextField11.setText("Score:");
        jTextField11.setFocusable(false);
        jTextField11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField11ActionPerformed(evt);
            }
        });

        jYourScore.setEditable(false);
        jYourScore.setFont(new java.awt.Font("宋体", 1, 24)); // NOI18N
        jYourScore.setText("0");
        jYourScore.setEnabled(false);

        jTextField2.setEditable(false);
        jTextField2.setFont(new java.awt.Font("宋体", 1, 14)); // NOI18N
        jTextField2.setText("MoneyTotal:");
        jTextField2.setFocusable(false);

        jYourMoney.setEditable(false);
        jYourMoney.setFont(new java.awt.Font("宋体", 1, 14)); // NOI18N
        jYourMoney.setFocusable(false);

        jYouWin.setEditable(false);
        jYouWin.setFont(new java.awt.Font("宋体", 1, 24)); // NOI18N
        jYouWin.setText(" YOU WIN!!");
        jYouWin.setFocusable(false);
        jYouWin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jYouWinActionPerformed(evt);
            }
        });

        jAIWin.setEditable(false);
        jAIWin.setFont(new java.awt.Font("宋体", 1, 24)); // NOI18N
        jAIWin.setText("  AI WIN!!");
        jAIWin.setFocusable(false);

        jNextRound.setFont(new java.awt.Font("宋体", 1, 24)); // NOI18N
        jNextRound.setText("NEXT ROUND");
        jNextRound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNextRoundActionPerformed(evt);
            }
        });

        jRound.setEditable(false);
        jRound.setFont(new java.awt.Font("宋体", 1, 24)); // NOI18N
        jRound.setText("Round 1");
        jRound.setEnabled(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(layout.createSequentialGroup()
                                .add(212, 212, 212)
                                .add(jTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jYourMoney))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(layout.createSequentialGroup()
                                        .add(jYourCard1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jYourCard2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(jYouBlackJack, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jYouWin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jTextField11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jYourScore, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 84, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(156, 156, 156)
                                .add(jYourCard3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jYourCard4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jYourCard5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(layout.createSequentialGroup()
                                .add(239, 239, 239)
                                .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jAIMoney, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 68, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(27, 27, 27)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(layout.createSequentialGroup()
                                        .add(jAICard3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jAICard4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jAICard5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(layout.createSequentialGroup()
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                            .add(layout.createSequentialGroup()
                                                .add(jAICard1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jAICard2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                                .add(org.jdesktop.layout.GroupLayout.TRAILING, jAIWin)
                                                .add(org.jdesktop.layout.GroupLayout.TRAILING, jAIBlackJack)))
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(jTextField9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 109, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(35, 35, 35)
                                        .add(jAIScore, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                        .add(0, 0, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 404, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jNextRound, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(141, 141, 141)
                        .add(jRound, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(layout.createSequentialGroup()
                        .add(32, 32, 32)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jAICard5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(jAICard1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jAICard4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jAICard3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jAICard2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jAIBlackJack, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jTextField9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jAIScore, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jAIWin, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                            .add(jAIMoney, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                            .add(jTextField1))
                        .add(18, 18, 18)
                        .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jYourCard1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jYourCard5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jYourCard4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jYourCard3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jYourCard2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jYouBlackJack, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jTextField11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jYourScore, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jYouWin, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                            .add(jYourMoney, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                            .add(jTextField2)))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 409, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jRound, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jNextRound, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(53, 53, 53))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField9ActionPerformed
        ;        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField9ActionPerformed

    private void jTextField11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField11ActionPerformed

    private void jYouWinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jYouWinActionPerformed
    ;        // TODO add your handling code here:
    }//GEN-LAST:event_jYouWinActionPerformed

    private void jNextRoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNextRoundActionPerformed

        try {
            InitialBoardsBetweenRounds();
            game.PlayNewRound();

        } catch (InterruptedException ex) {
            Logger.getLogger(BlackJackUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(BlackJackUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jNextRoundActionPerformed

    private void jHitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jHitActionPerformed
        if (round == null) {
            return;
        }
        if (round.isPlayerPhase()) {
            if(game.getPlayer().AmIDouble()){
                //GetOneCardAndStand
                round.PlayerHit();
                //Should not do this, but I am lazy.
                jStandActionPerformed(null);
                return;
            }

            if (!BlackJackRule.AmIBust(game.getPlayer())) {
                round.PlayerHit();
                DisableDouble();
                DisableSurrender();
                //Check GameStatus
                if (BlackJackRule.AmIBust(game.getPlayer())) {
                    //Should not do this, but I am lazy.
                    jStandActionPerformed(null);
                }

            }
        }
    }//GEN-LAST:event_jHitActionPerformed

    private void jTextField10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField10ActionPerformed

    private void jSurrenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSurrenderActionPerformed
        round.HalfMoneyOfRound();
        round.RoundEnd(100);
    }//GEN-LAST:event_jSurrenderActionPerformed

    private void jDoubleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDoubleActionPerformed
        round.DoubleMoneyOfRound();
        game.getPlayer().doDouble(true);
        DisableDouble();
        DisableSurrender();
        DisableStand();
    }//GEN-LAST:event_jDoubleActionPerformed

    private void jStandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jStandActionPerformed
        if (round.isPlayerPhase()) {
            TerminateControlOfPlayer();
            round.AIPhase();
        }
    }//GEN-LAST:event_jStandActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            Log.getInstance().MailLog();
        } catch (MessagingException ex) {
            Logger.getLogger(BlackJackUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            javax.swing.UIManager.LookAndFeelInfo[] installedLookAndFeels = javax.swing.UIManager.getInstalledLookAndFeels();
            for (int idx = 0; idx < installedLookAndFeels.length; idx++) {
                if ("Nimbus".equals(installedLookAndFeels[idx].getName())) {
                    javax.swing.UIManager.setLookAndFeel(installedLookAndFeels[idx].getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BlackJackUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BlackJackUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BlackJackUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BlackJackUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BlackJackUI ui = new BlackJackUI();
                ui.setVisible(true);
                try {
                    ui.DoSomethingAtBegin();
                } catch (MessagingException ex) {
                    Logger.getLogger(BlackJackUI.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField jAIBlackJack;
    private javax.swing.JButton jAICard1;
    private javax.swing.JButton jAICard2;
    private javax.swing.JButton jAICard3;
    private javax.swing.JButton jAICard4;
    private javax.swing.JButton jAICard5;
    private javax.swing.JTextField jAIMoney;
    private javax.swing.JTextField jAIScore;
    private javax.swing.JTextField jAIScore2;
    private javax.swing.JTextField jAIWin;
    private javax.swing.JTextField jBet;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jDouble;
    private javax.swing.JButton jHit;
    private javax.swing.JTextArea jLog;
    private javax.swing.JButton jNextRound;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jRound;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton jStand;
    private javax.swing.JButton jSurrender;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JTextField jYouBlackJack;
    private javax.swing.JTextField jYouWin;
    private javax.swing.JButton jYourCard1;
    private javax.swing.JButton jYourCard2;
    private javax.swing.JButton jYourCard3;
    private javax.swing.JButton jYourCard4;
    private javax.swing.JButton jYourCard5;
    private javax.swing.JTextField jYourMoney;
    private javax.swing.JTextField jYourScore;
    // End of variables declaration//GEN-END:variables

}
