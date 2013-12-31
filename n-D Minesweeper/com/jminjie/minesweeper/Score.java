package com.jminjie.minesweeper;

import java.io.Serializable;

public class Score implements Serializable{
  private static final long serialVersionUID = 1;
  private int score;
  private String name;
  
  public int getScore(){
    return score;
  }
  
  public String getName(){
    return name;
  }
  
  public Score(String n, int s){
    name = n;
    score = s;
  }
}
