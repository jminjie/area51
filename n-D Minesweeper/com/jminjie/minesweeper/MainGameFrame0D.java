package com.jminjie.minesweeper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class MainGameFrame0D extends JFrame{
  private static final long serialVersionUID = 1;
  JPanel mainPanel;
  JPanel auxPanel;
  JPanel timePanel;
  JPanel minesPanel;
  
  JLabel timeLabel;
  JLabel minesLabel;
  
  Timer my_timer;
  
  HighscoreManager highscores0D;
  
  SquareButtonListener squareButtonListener;
  SquareMouseAdapter squareMouseAdapter;
  
  Square squares;
  
  final int NUM_MINES = 0;
  final int AUX_BORDER = 5;
  final Font AUX_FONT = new Font(Font.MONOSPACED, Font.BOLD, 20);
  
  boolean first_click;
  int mines_left;
  int non_mines_left;
  long cur_time;
  public static Random rnd = new Random(System.currentTimeMillis());
  
  MainGameFrame0D(){
    super("Minesweeper");
    setLayout(new BorderLayout());
    first_click = true;
    mines_left = NUM_MINES;
    non_mines_left = 1 - NUM_MINES;
    
    mainPanel = new JPanel();
    mainPanel.setLayout(new FlowLayout());
    squares = new Square();
    
    squareButtonListener = new SquareButtonListener();
    squareMouseAdapter = new SquareMouseAdapter();
    
    squares = new Square();
    mainPanel.add(squares);
    squares.addActionListener(squareButtonListener);
    squares.addMouseListener(squareMouseAdapter);
        
    for (int i = 0; i < NUM_MINES; i++){
      genRandomMine();
    }
    
    auxPanel = new JPanel(new BorderLayout());
    timePanel = new JPanel(new FlowLayout());
    cur_time = 0;
    timeLabel = new JLabel("Time: " + String.valueOf(cur_time));
    timeLabel.setFont(AUX_FONT);
    timePanel.add(timeLabel);
    timePanel.setBorder(BorderFactory.createLineBorder(Color.black));
    
    my_timer = new Timer(1000, new TimerListener());
    auxPanel.add(timePanel, BorderLayout.WEST);
    
    minesPanel = new JPanel(new FlowLayout());
    minesLabel = new JLabel("Mines: " + String.valueOf(mines_left));
    minesLabel.setFont(AUX_FONT);
    minesPanel.add(minesLabel);
    minesPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    auxPanel.add(minesPanel, BorderLayout.EAST);
    
    auxPanel.setBorder(BorderFactory.createEmptyBorder(AUX_BORDER, AUX_BORDER, AUX_BORDER, AUX_BORDER));
    add(auxPanel, BorderLayout.NORTH);
    
    highscores0D = new HighscoreManager("scores0.dat");
    Minesweeper.current_mode = "0";
    
    add(mainPanel, BorderLayout.CENTER);
    setPreferredSize(new Dimension(260, 120));
    pack();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocation(200, 100);
    setVisible(true);
  }
  
  public class TimerListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      cur_time++;
      if (cur_time < 1000)
        timeLabel.setText("Time: " + String.valueOf(cur_time));
    }
  }
  
  void loseGame(){
    my_timer.stop();
    JOptionPane.showMessageDialog(this, "You clicked on a mine!", "Game Over",
        JOptionPane.ERROR_MESSAGE);
    Minesweeper.startNewGame(this);
  }
  
  void winGame(){
    my_timer.stop();
    String player_name = "Jordan";
    WinGameDialog winGameDialog = new WinGameDialog(this, cur_time);
    player_name = winGameDialog.getInput();
    highscores0D.addScore(player_name, cur_time);
    Minesweeper.startNewGame(this);
  }
  
  void genRandomMine(){}
  
  public class SquareButtonListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      my_timer.stop();
      
        if (e.getSource().equals(squares)){
          if (first_click){
              if (!squares.getValue().equals("mine")){
                squares.setValue(String.valueOf(calcAdjacentMines()));
              }
            
            first_click = false;
          }
          clickAction();
        }
      
      my_timer.start();
    }
  }
  
  
  void clickAction(){
    if (squares.getValue().equals("mine")){
      if (first_click){
        squares.setValue("0");
        genRandomMine();
        squares.setEnabled(true);
      }
      else{
        squares.revealValue();
        loseGame();
      }
    }
    squares.revealValue();
    squares.setEnabled(false);
    non_mines_left--;
    if (squares.getValue().equals("0")){              
      doClickAround();
    }
    if (non_mines_left == 0){
      winGame();
    }
  }
  
  void doClickAround(){}
  
  public class SquareMouseAdapter extends MouseAdapter{
    private boolean isLeftPressed;
    private boolean isRightPressed;
    
    public SquareMouseAdapter(){
      isLeftPressed = false;
      isRightPressed = false;
    }
    
    public void mousePressed(MouseEvent e){
      if (SwingUtilities.isLeftMouseButton(e)){
        isLeftPressed = true;
      }
      else if (SwingUtilities.isRightMouseButton(e)){
        isRightPressed = true;
      }
      if (isLeftPressed && isRightPressed){
          if (e.getSource().equals(squares)){
            if (calcAdjacentFlags() == calcAdjacentMines() && !squares.getValue().equals("hidden")){
              doClickAround();
            }
          }
        
      }
    }
    
    
    public void mouseClicked(MouseEvent e){
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && e.getSource().equals(squares)){
          if (calcAdjacentFlags() == calcAdjacentMines() && !squares.getValue().equals("hidden")){
            doClickAround();
          }
        }
      
    }
    
    public void mouseReleased(MouseEvent e){
      if (isLeftPressed && isRightPressed){
        if (SwingUtilities.isLeftMouseButton(e))
          isLeftPressed = false;
        if (SwingUtilities.isRightMouseButton(e))
          isRightPressed = false;
      }
      else{
        if (SwingUtilities.isLeftMouseButton(e))
          isLeftPressed = false;
        if (SwingUtilities.isRightMouseButton(e))
          isRightPressed = false;
        
          if (e.getSource().equals(squares) && SwingUtilities.isLeftMouseButton(e)){
            squares.doClick();
          }
          if (SwingUtilities.isRightMouseButton(e)
             && e.getSource().equals(squares) && squares.hidden){
            if (!squares.flagged){
              mines_left--;
              minesLabel.setText("Mines: " + String.valueOf(mines_left));
            }
            if (squares.flagged){
              mines_left++;
              minesLabel.setText("Mines: " + String.valueOf(mines_left));
            }
            squares.toggleFlag();
          }
          if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown() && e.getSource().equals(squares)){
            if (calcAdjacentFlags() == calcAdjacentMines() && !squares.getValue().equals("hidden")){
              doClickAround();
            }
          }
        
      }
    }
  }
      
    
      
  int calcAdjacentMines(){
    return 0;
  }
  
  int calcAdjacentFlags(){
    return 0;
  }
}
