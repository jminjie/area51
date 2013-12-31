package com.jminjie.minesweeper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class MainGameFrame1D extends JFrame{
  private static final long serialVersionUID = 1;
  HighscoreManager highscores1D;
  JPanel mainPanel;
  JPanel auxPanel;
  JPanel timePanel;
  JPanel minesPanel;
  
  JLabel timeLabel;
  JLabel minesLabel;
  
  Timer my_timer;
  
  SquareButtonListener squareButtonListener;
  SquareMouseAdapter squareMouseAdapter;
  
  Square[] squares;
  
  final int ROWS = 16;
  final int NUM_MINES = 3;
  final int AUX_BORDER = 5;
  final Font AUX_FONT = new Font(Font.MONOSPACED, Font.BOLD, 20);
  
  boolean first_click;
  int mines_left;
  int non_mines_left;
  long cur_time;
  public static Random rnd = new Random(System.currentTimeMillis());
  
  MainGameFrame1D(){
    super("Minesweeper");
    setLayout(new BorderLayout());
    first_click = true;
    mines_left = NUM_MINES;
    non_mines_left = ROWS - NUM_MINES;
    
    mainPanel = new JPanel();
    mainPanel.setLayout(new GridLayout(1, ROWS));
    squares = new Square[ROWS];
    
    squareButtonListener = new SquareButtonListener();
    squareMouseAdapter = new SquareMouseAdapter();
    
    for (int i = 0; i < ROWS; i++){
        squares[i] = new Square();
        mainPanel.add(squares[i]);
        squares[i].addActionListener(squareButtonListener);
        squares[i].addMouseListener(squareMouseAdapter);
      
    }
    
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
    
    calcAllSquares();
    
    highscores1D = new HighscoreManager("scores1.dat");
    Minesweeper.current_mode = "1";
    
    mainPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    add(mainPanel, BorderLayout.CENTER);
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
    revealAllMines();
    Minesweeper.startNewGame(this);
  }
  
  void winGame(){
    my_timer.stop();
    String player_name = "Jordan";
    WinGameDialog winGameDialog = new WinGameDialog(this, cur_time);
    player_name = winGameDialog.getInput();
    highscores1D.addScore(player_name, cur_time);
    Minesweeper.startNewGame(this);
  }
  
  void genRandomMine(){
    int x = rnd.nextInt(ROWS);
    while (squares[x].getValue().equals("mine")){
      x = rnd.nextInt(ROWS);
    }
    squares[x].setValue("mine");
  }
  
  void genRandomMine(int notI){
    int x = rnd.nextInt(ROWS);
    while (squares[x].getValue().equals("mine") || x == notI){
      x = rnd.nextInt(ROWS);
    }
    squares[x].setValue("mine");
  }
  
  public class SquareButtonListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      my_timer.stop();
      for (int i = 0; i< ROWS; i++){
        if (e.getSource().equals(squares[i])){
          clickAction(i);
          first_click = false;
        }
      }
      my_timer.start();
    }
  }
  
  void calcAllSquares(){
    for (int x = 0; x < ROWS; x++){
      if (!squares[x].getValue().equals("mine")){
        squares[x].setValue(String.valueOf(calcAdjacentMines(x)));
      }
    }
  }
  
  
  void clickAction(int i){
    if (squares[i].getValue().equals("mine")){
      if (first_click){
        squares[i].setValue("0");
        genRandomMine(i);
        squares[i].setEnabled(true);
        calcAllSquares();
      }
      else{
        squares[i].revealValue();
        loseGame();
      }
    }
    squares[i].revealValue();
    squares[i].setEnabled(false);
    non_mines_left--;
    if (squares[i].getValue().equals("0")){              
      doClickAround(i);
    }
    if (non_mines_left == 0){
      winGame();
    }
  }
  
  void doClickAround(int i){
    if (i > 0 && squares[i-1].hidden && !squares[i-1].flagged)
      clickAction(i-1);
    if (squares[i].hidden && !squares[i].flagged)
      clickAction(i);
    if (i < ROWS - 1 && squares[i+1].hidden && !squares[i+1].flagged)
      clickAction(i+1);
  }
  
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
        for(int i = 0; i < ROWS; i++){
          if (e.getSource().equals(squares[i])){
            if (calcAdjacentFlags(i) == calcAdjacentMines(i) && !squares[i].getValue().equals("hidden")){
              doClickAround(i);
            }
          }
        }
      }
    }
    
    
    public void mouseClicked(MouseEvent e){
      for (int i = 0; i < ROWS; i++){
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && e.getSource().equals(squares[i])){
          if (calcAdjacentFlags(i) == calcAdjacentMines(i) && !squares[i].getValue().equals("hidden")){
            doClickAround(i);
          }
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
        for (int i = 0; i < ROWS; i++){
          if (e.getSource().equals(squares[i]) && SwingUtilities.isLeftMouseButton(e)){
            squares[i].doClick();
          }
          if (SwingUtilities.isRightMouseButton(e)
             && e.getSource().equals(squares[i]) && squares[i].hidden){
            if (!squares[i].flagged){
              mines_left--;
              minesLabel.setText("Mines: " + String.valueOf(mines_left));
            }
            if (squares[i].flagged){
              mines_left++;
              minesLabel.setText("Mines: " + String.valueOf(mines_left));
            }
            squares[i].toggleFlag();
          }
          if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown() && e.getSource().equals(squares[i])){
            if (calcAdjacentFlags(i) == calcAdjacentMines(i) && !squares[i].getValue().equals("hidden")){
              doClickAround(i);
            }
          }
        }
      }
    }
  }
      
    
      
  int calcAdjacentMines(int i){
    int adj = 0;
    if (i > 0 && squares[i - 1].getValue().equals("mine"))//up
      adj++;
    if (i < ROWS - 1 && squares[i + 1].getValue().equals("mine"))//down
      adj++;
    return adj;
  }

  int calcAdjacentFlags(int i){
    int adj = 0;
    if (i > 0 && squares[i - 1].flagged)//up
      adj++;
    if (i < ROWS - 1 && squares[i + 1].flagged)//down
      adj++;
    return adj;
    }
  
  
  void revealAllMines(){
    for (int i = 0; i < ROWS; i++){
      if ((squares[i].getValue().equals("mine") && !squares[i].flagged)
          || (!squares[i].getValue().equals("mine") && squares[i].flagged))
        squares[i].revealValue();
    }
  }
}
