package com.jminjie.minesweeper;

import javax.swing.JFrame;

public class Minesweeper {  
  
  static String current_mode = "3";
  public static void main(String args[]){
    MainGameFrame3D mainGameFrame = new MainGameFrame3D();
    return;
  }
  
  static void startNewGame(JFrame mainFrame){
    String mode = "";
    NewGameModalDialog newGameModalDialog = new NewGameModalDialog(mainFrame);
    mode = newGameModalDialog.getInput();
    mainFrame.dispose();
    if (mode == "0"){
      MainGameFrame0D mainGameFrame0D = new MainGameFrame0D();
    }
    else if (mode == "1"){
      MainGameFrame1D mainGameFrame1D = new MainGameFrame1D();
    }
    else if (mode == "2"){
      MainGameFrame2D mainGameFrame2D = new MainGameFrame2D();
    }
    else if (mode == "3"){
      MainGameFrame3D mainGameFrame3D = new MainGameFrame3D();
    }
    else if (mode == "4"){
      MainGameFrame4D mainGameFrame4D = new MainGameFrame4D();
    }     
  }
}
