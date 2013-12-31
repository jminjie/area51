package com.jminjie.minesweeper;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class HighscoresFrame extends JDialog{
  private static final long serialVersionUID = 1;
  JLabel title;
  JTextArea content;
  
  HighscoresFrame(JFrame mainframe, String in){
    super(mainframe, "Highscores", false);
    title = new JLabel("Highscores for " + Minesweeper.current_mode + "D mode", JLabel.CENTER);
    content = new JTextArea(in);
    content.setTabSize(6);
    content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    content.setOpaque(false);
    
    setLayout(new BorderLayout());
    add(title, BorderLayout.NORTH);
    add(content, BorderLayout.CENTER);
    
    setLocation(475, 150);
    pack();
    setVisible(true);
  }
}
