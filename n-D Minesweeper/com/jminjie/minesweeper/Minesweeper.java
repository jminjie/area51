package com.jminjie.minesweeper;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.JFrame;

public class Minesweeper {  
  
  static URL hiddenIconURL, pressedHiddenIconURL, mineIconURL, flagIconURL, badFlagIconURL;
  static URL [] numIconURL;
  
  static Image hiddenIcon, pressedHiddenIcon, mineIcon, flagIcon, badFlagIcon;
  static Image [] numIcon;
	
  static String current_mode = "3";
  public static void main(String args[]){
	Toolkit tk = Toolkit.getDefaultToolkit();
	
	hiddenIconURL = tk.getClass().getResource("/Resources/hiddenIcon.png");
	hiddenIcon = tk.createImage(hiddenIconURL);
	tk.prepareImage(hiddenIcon, -1, -1, null);
	  
	pressedHiddenIconURL = tk.getClass().getResource("/Resources/pressedHiddenIcon.png");
	pressedHiddenIcon = tk.createImage(pressedHiddenIconURL);
	tk.prepareImage(pressedHiddenIcon, -1, -1, null);
	  
	mineIconURL = tk.getClass().getResource("/Resources/mineIcon.png");
	mineIcon = tk.createImage(mineIconURL);
	tk.prepareImage(mineIcon, -1, -1, null);
	  
	flagIconURL = tk.getClass().getResource("/Resources/flagIcon.png");
	flagIcon = tk.createImage(flagIconURL);
	tk.prepareImage(flagIcon, -1, -1, null);
	  
	badFlagIconURL = tk.getClass().getResource("/Resources/badFagIcon.png");
	badFlagIcon = tk.createImage(badFlagIconURL);
	tk.prepareImage(badFlagIcon, -1, -1, null);

	numIconURL = new URL[27];
	numIcon = new Image[27];
	for (Integer i = 0; i <= 26; i++){
	  numIconURL[i] = tk.getClass().getResource("/Resources/" + i + "Icon.png");
      numIcon[i] = tk.createImage(numIconURL[i]);
      tk.prepareImage(numIcon[i], -1, -1, null);
	}
    
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
