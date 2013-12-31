package com.jminjie.minesweeper;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JButton;


public class Square extends JButton{
  private static final long serialVersionUID = 1;
  private String value;
  boolean hidden;
  boolean flagged;
  //initially "hidden" other options integer between [0, 26] or "mine"
  
  Square(){
    super(new ImageIcon("src/Resources/hiddenIcon.png"));
    setDisabledIcon(new ImageIcon("src/Resources/hiddenIcon.png"));
    value = "hidden";
    hidden = true;
    setBorder(null);
    setPreferredSize(new Dimension(20, 20));
    setPressedIcon(new ImageIcon("src/Resources/pressedHiddenIcon.png"));
    flagged = false;
  }
  
  void setValue(String v){
    if (!(v.equalsIgnoreCase("mine") || v.equalsIgnoreCase("hidden") || (Integer.parseInt(v) >= 0 && Integer.parseInt(v) <= 26))){
      System.out.println("Invalid assignment to Square.value");
      return;
    }
    else{
      value = v;
    }
  }
  
  void toggleFlag(){
    if (!flagged){
      setIcon(new ImageIcon("src/Resources/flagIcon.png"));
      flagged = true;
      setEnabled(false);
      setDisabledIcon(new ImageIcon("src/Resources/flagIcon.png"));
    }
    else{
      setIcon(new ImageIcon("src/Resources/hiddenIcon.png"));
      flagged = false;
      setEnabled(true);
    }
  }
  
  void revealValue(){
    setIcon(new ImageIcon("src/Resources/" + value + "Icon.png"));
    setDisabledIcon(new ImageIcon("src/Resources/" + value + "Icon.png"));
    hidden = false;
    if (flagged && !value.equals("mine")){
      setIcon(new ImageIcon("src/Resources/badFlagIcon.png"));
      setDisabledIcon(new ImageIcon("src/Resources/badFlagIcon.png"));
    }
  }
  
  
  String getValue(){
    return value;
  }
}
