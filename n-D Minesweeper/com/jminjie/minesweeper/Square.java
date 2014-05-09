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
    super(new ImageIcon(Minesweeper.hiddenIcon));
    setDisabledIcon(new ImageIcon(Minesweeper.hiddenIcon));
    value = "hidden";
    hidden = true;
    setBorder(null);
    setPreferredSize(new Dimension(20, 20));
    setPressedIcon(new ImageIcon(Minesweeper.pressedHiddenIcon));
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
      setIcon(new ImageIcon(Minesweeper.flagIcon));
      flagged = true;
      setEnabled(false);
      setDisabledIcon(new ImageIcon(Minesweeper.flagIcon));
    }
    else{
      setIcon(new ImageIcon(Minesweeper.hiddenIcon));
      flagged = false;
      setEnabled(true);
    }
  }
  
  void revealValue(){
	if (value.equals("mine")){
		setIcon(new ImageIcon(Minesweeper.mineIcon));
		setDisabledIcon(new ImageIcon(Minesweeper.mineIcon));
	}
	else{
		setIcon(new ImageIcon(Minesweeper.numIcon[Integer.parseInt(value)]));
		setDisabledIcon(new ImageIcon(Minesweeper.numIcon[Integer.parseInt(value)]));
	}
	
    hidden = false;
    if (flagged && !value.equals("mine")){
      setIcon(new ImageIcon(Minesweeper.badFlagIcon));
      setDisabledIcon(new ImageIcon(Minesweeper.badFlagIcon));
    }
  }
  
  
  String getValue(){
    return value;
  }
}
