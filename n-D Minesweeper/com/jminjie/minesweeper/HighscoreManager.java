package com.jminjie.minesweeper;

import java.util.*;
import java.io.*;

public class HighscoreManager {
  private ArrayList<Score> scores;
  private final String HIGHSCORE_FILE;
  private final String HIGHSCORE_FILE_PATH = "src/resources/";
  
  ObjectOutputStream outputStream = null;
  ObjectInputStream inputStream = null;
  
  public HighscoreManager(String filename){
    HIGHSCORE_FILE = filename;
    scores = new ArrayList<Score>();
  }
  
  public ArrayList<Score> getScores(){
    loadScoreFile();
    sort();
    return scores;
  }
  
  private void sort(){
    ScoreComparator comparator = new ScoreComparator();
    Collections.sort(scores, comparator);
  }
  
  public void addScore(String name, int score){
    loadScoreFile();
    scores.add(new Score(name, score));
    updateScoreFile();
  }
  
  public void addScore(String name, long score){
    loadScoreFile();
    if (name.length() > 7)
      name = name.substring(0, 6) + "...";
    scores.add(new Score(name, (int)score));
    updateScoreFile();
  }
  
  public void loadScoreFile(){
    try{
      inputStream = new ObjectInputStream(new FileInputStream(HIGHSCORE_FILE_PATH + HIGHSCORE_FILE));
      scores = (ArrayList<Score>) inputStream.readObject();
    }
    catch (FileNotFoundException e){
      System.out.println("No data available");
    }
    catch (Exception e){
      System.out.println("Error in loading state.data: " + e.getMessage());
    }
    finally{
      try{
        if (outputStream != null){
          outputStream.flush();outputStream.close();
        }
      }
      catch (IOException e){
        System.out.println("Error in loading state.data: " + e.getMessage());
      }
    }
  }
  
  public void updateScoreFile(){
    try{
      outputStream = new ObjectOutputStream(new FileOutputStream(HIGHSCORE_FILE_PATH + HIGHSCORE_FILE));
      outputStream.writeObject(scores);
    }
    catch(Exception e){
      System.out.println("Error in updating state.data: " + e.getMessage());
    }
    finally{
      try{
        if (outputStream != null){
          outputStream.flush();
          outputStream.close();
        }
      }
      catch(IOException e){
        System.out.println("Error in updating state.data: " + e.getMessage());
      }
    }
  }
  
  public String getHighscoreString(){
    String highscoreString = "";
    final int max = 10;
    ArrayList<Score> scores;
    scores = getScores();
    
    for (int i = 0; i < Math.min(max, scores.size()); i++){
      highscoreString += (i+1) + ".\t" + scores.get(i).getName() + "\t\t" + scores.get(i).getScore() + "\n";
    }
    if (highscoreString.equals(""))
      return "     No wins recorded     ";
    else
      return highscoreString;
  }
}
