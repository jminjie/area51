package com.jminjie.minesweeper;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class WinGameDialog extends JDialog{
  private static final long serialVersionUID = 1;
  JLabel message;
  JLabel prompt;
  JTextField name_field;
  JButton OKButton;
  
  JPanel topPan;
  JPanel midPan;
  JPanel OKPan;
  
  final int BORDER = 100;
  
  WinGameDialog(JFrame mainframe, long score){
    super(mainframe, "You Win", true);
    message = new JLabel("You beat " + Minesweeper.current_mode + "D mode in " + score + " seconds", JLabel.CENTER);
    prompt = new JLabel("Enter name:", JLabel.CENTER);
    name_field = new JTextField();
    OKButton = new JButton("OK");
    OKButton.addActionListener(new OKButtonListener());
    
    topPan = new JPanel(new FlowLayout());
    topPan.add(message);
    
    midPan = new JPanel(new GridLayout(2, 1));
    midPan.setBorder(BorderFactory.createEmptyBorder(0, BORDER, 0, BORDER));
    midPan.add(prompt);
    midPan.add(name_field);
    
    OKPan = new JPanel(new FlowLayout());
    OKPan.add(OKButton);
    
    setLayout(new BorderLayout());
    add(topPan, BorderLayout.NORTH);
    add(midPan, BorderLayout.CENTER);
    add(OKPan, BorderLayout.SOUTH);
    
    getRootPane().setDefaultButton(OKButton);
    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    setLocation(350, 200);
    pack();
    setVisible(true);
  }
  
  public class OKButtonListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      if (e.getSource().equals(OKButton)){
        if (getInput().compareTo("") != 0){
          dispose();
        }
      }
    }
  }
  
  public String getInput(){
    return name_field.getText();
  }
}
