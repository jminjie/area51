package com.jminjie.minesweeper;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class NewGameModalDialog extends JDialog{
  private static final long serialVersionUID = 1;
  final int BUTTON_BORDER_Y = 7;
  final int BUTTON_BORDER_X = 20;
  
  JButton button0D, button1D, button2D, button3D, button4D, buttonHighscores, buttonQuit;
  JPanel panel0D, panel1D, panel2D, panel3D, panel4D, panelHighscores, panelQuit;
  JPanel mainPanel;
  String mode;
  HighscoreManager highscoreManager;
  HighscoresFrame highscoresFrame;
  ButtonListener buttonListener;
  JFrame mainFrame;
  boolean highscoresShowing;
  
  
  public NewGameModalDialog(JFrame main){
    super(main, "New Game", true);
    mainFrame = main;
    highscoresShowing = false;
    buttonListener = new ButtonListener();
    highscoreManager = new HighscoreManager("scores" + Minesweeper.current_mode + ".dat");
    
    button0D = new JButton("0 Dimensional");
    button0D.addActionListener(buttonListener);
    button1D = new JButton("1 Dimensional");
    button1D.addActionListener(buttonListener);
    button2D = new JButton("2 Dimensional");
    button2D.addActionListener(buttonListener);
    button3D = new JButton("3 Dimensional");
    button3D.addActionListener(buttonListener);
    button4D = new JButton("4 Dimensional");
    button4D.addActionListener(buttonListener);
    buttonHighscores = new JButton("Highscores");
    buttonHighscores.addActionListener(buttonListener);
    buttonQuit = new JButton("Quit");
    buttonQuit.addActionListener(buttonListener);
    
    panel0D = new JPanel(new BorderLayout());
    panel0D.add(button0D);
    panel0D.setBorder(BorderFactory.createEmptyBorder(BUTTON_BORDER_Y, BUTTON_BORDER_X, BUTTON_BORDER_Y, BUTTON_BORDER_X));
    panel1D = new JPanel(new BorderLayout());
    panel1D.add(button1D);
    panel1D.setBorder(BorderFactory.createEmptyBorder(BUTTON_BORDER_Y, BUTTON_BORDER_X, BUTTON_BORDER_Y, BUTTON_BORDER_X));
    panel2D = new JPanel(new BorderLayout());
    panel2D.add(button2D);
    panel2D.setBorder(BorderFactory.createEmptyBorder(BUTTON_BORDER_Y, BUTTON_BORDER_X, BUTTON_BORDER_Y, BUTTON_BORDER_X));
    panel3D = new JPanel(new BorderLayout());
    panel3D.add(button3D);
    panel3D.setBorder(BorderFactory.createEmptyBorder(BUTTON_BORDER_Y, BUTTON_BORDER_X, BUTTON_BORDER_Y, BUTTON_BORDER_X));
    panel4D = new JPanel(new BorderLayout());
    panel4D.add(button4D);
    panel4D.setBorder(BorderFactory.createEmptyBorder(BUTTON_BORDER_Y, BUTTON_BORDER_X, BUTTON_BORDER_Y, BUTTON_BORDER_X));
    panelHighscores = new JPanel(new BorderLayout());
    panelHighscores.add(buttonHighscores);
    panelHighscores.setBorder(BorderFactory.createEmptyBorder(BUTTON_BORDER_Y, BUTTON_BORDER_X, BUTTON_BORDER_Y, BUTTON_BORDER_X));
    panelQuit = new JPanel(new BorderLayout());
    panelQuit.add(buttonQuit);
    panelQuit.setBorder(BorderFactory.createEmptyBorder(BUTTON_BORDER_Y, BUTTON_BORDER_X, BUTTON_BORDER_Y, BUTTON_BORDER_X));
    
    mainPanel = new JPanel(new GridLayout(7, 1));
    mainPanel.add(panel0D);
    mainPanel.add(panel1D);
    mainPanel.add(panel2D);
    mainPanel.add(panel3D);
    mainPanel.add(panel4D);
    mainPanel.add(panelHighscores);
    mainPanel.add(panelQuit);
    
    setLayout(new BorderLayout());
    add(mainPanel, BorderLayout.CENTER);
    
    JLabel title = new JLabel("Select mode", JLabel.CENTER);
    title.setBorder(BorderFactory.createEmptyBorder(BUTTON_BORDER_Y, 0,0,0));
    
    add(title, BorderLayout.NORTH);
    
    mode = "";
    pack();
    setLocation(300, 150);
    setVisible(true);
  }
  
  public class ButtonListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      if (e.getSource().equals(button0D)){
        mode = "0";
        setVisible(false);
      }
      if (e.getSource().equals(button1D)){
        mode = "1";
        setVisible(false);
      }
      if (e.getSource().equals(button2D)){
        mode = "2";
        setVisible(false);
      }
      if (e.getSource().equals(button3D)){
        mode = "3";
        setVisible(false);
      }
      if (e.getSource().equals(button4D)){
        mode = "4";
        setVisible(false);
      }
      if (e.getSource().equals(buttonHighscores)){
        if (highscoresShowing == false){
          highscoresFrame = new HighscoresFrame(mainFrame, highscoreManager.getHighscoreString());
          highscoresShowing = true;
        }
        else{
          highscoresFrame.dispose();
          highscoresShowing = false;
        }
      }
      if (e.getSource().equals(buttonQuit)){
        System.exit(0);
      }
    }
  }
  
  public String getInput(){
    return mode;
  }
}
