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

public class MainGameFrame2D extends JFrame{
  private static final long serialVersionUID = 1;
  JPanel mainPanel;
  JPanel auxPanel;
  JPanel timePanel;
  JPanel minesPanel;
  
  JLabel timeLabel;
  JLabel minesLabel;
  
  Timer my_timer;
  
  HighscoreManager highscores2D;
  
  SquareButtonListener squareButtonListener;
  SquareMouseAdapter squareMouseAdapter;
  
  Square[][] squares;
  
  final int ROWS = 16;
  final int COLS = 30;
  final int NUM_MINES = 99;
  final int AUX_BORDER = 5;
  final int PAN_BORDER = 4;
  final Font AUX_FONT = new Font(Font.MONOSPACED, Font.BOLD, 20);
  
  boolean first_click;
  int mines_left;
  int non_mines_left;
  long cur_time;
  public static Random rnd = new Random(System.currentTimeMillis());
  
  MainGameFrame2D(){
    super("Minesweeper");
    setLayout(new BorderLayout());
    first_click = true;
    mines_left = NUM_MINES;
    non_mines_left = ROWS * COLS - NUM_MINES;
    
    mainPanel = new JPanel();
    mainPanel.setLayout(new GridLayout(ROWS, COLS));
    squares = new Square[ROWS][COLS];
    
    squareButtonListener = new SquareButtonListener();
    squareMouseAdapter = new SquareMouseAdapter();
    
    for (int i = 0; i < ROWS; i++){
      for (int j = 0; j < COLS; j++){
        squares[i][j] = new Square();
        mainPanel.add(squares[i][j]);
        squares[i][j].addActionListener(squareButtonListener);
        squares[i][j].addMouseListener(squareMouseAdapter);
      }
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
    
    highscores2D = new HighscoreManager("scores2.dat");
    Minesweeper.current_mode = "2";
   
    mainPanel.setBorder(BorderFactory.createEmptyBorder(PAN_BORDER, PAN_BORDER, PAN_BORDER, PAN_BORDER));
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
    highscores2D.addScore(player_name, cur_time);
    Minesweeper.startNewGame(this);
  }
  
  void genRandomMine(){
    int x = rnd.nextInt(ROWS);
    int y = rnd.nextInt(COLS);
    while (squares[x][y].getValue().equals("mine")){
      x = rnd.nextInt(ROWS);
      y = rnd.nextInt(COLS);
    }
    squares[x][y].setValue("mine");
  }
  
  void genRandomMine(int notI, int notJ){
    int x = rnd.nextInt(ROWS);
    int y = rnd.nextInt(COLS);
    while (squares[x][y].getValue().equals("mine") || (x == notI && y == notJ)){
      x = rnd.nextInt(ROWS);
      y = rnd.nextInt(COLS);
    }
    squares[x][y].setValue("mine");
  }
  
  public class SquareButtonListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      my_timer.stop();
      for (int i = 0; i< ROWS; i++){
        for (int j = 0; j < COLS; j++){
          if (e.getSource().equals(squares[i][j])){
            clickAction(i, j);
            first_click = false;
          }
        }
      }
      my_timer.start();
    }
  }
  
  void calcAllSquares(){
    for (int x = 0; x < ROWS; x++){
      for (int y = 0; y < COLS; y++){
        if (!squares[x][y].getValue().equals("mine")){
          squares[x][y].setValue(String.valueOf(calcAdjacentMines(x, y)));
        }
      }
    }
  }
  
  void clickAction(int i, int j){
    if (squares[i][j].getValue().equals("mine")){
      if (first_click){
        squares[i][j].setValue("0");
        genRandomMine(i, j);
        squares[i][j].setEnabled(true);
        calcAllSquares();
      }
      else{
        squares[i][j].revealValue();
        loseGame();
      }
    }
    squares[i][j].revealValue();
    squares[i][j].setEnabled(false);
    non_mines_left--;
    if (squares[i][j].getValue().equals("0")){              
      doClickAround(i, j);
    }
    if (non_mines_left == 0){
      winGame();
    }
  }
  
  void doClickAround(int i, int j){
    if (i > 0 && squares[i-1][j].hidden && !squares[i-1][j].flagged)
      clickAction(i-1, j);
    if (i > 0 && j > 0 && squares[i-1][j-1].hidden && !squares[i-1][j-1].flagged)
      clickAction(i-1, j-1);
    if (i > 0 && j < COLS - 1 && squares[i-1][j+1].hidden && !squares[i-1][j+1].flagged)
      clickAction(i-1, j+1);
    if (j > 0 && squares[i][j-1].hidden && !squares[i][j-1].flagged)
      clickAction(i, j-1);
    if (j < COLS - 1 && squares[i][j+1].hidden && !squares[i][j+1].flagged)
      clickAction(i, j+1);
    if (i < ROWS - 1 && squares[i+1][j].hidden && !squares[i+1][j].flagged)
      clickAction(i+1, j);
    if (i < ROWS - 1 && j > 0 && squares[i+1][j-1].hidden && !squares[i+1][j-1].flagged)
      clickAction(i+1, j-1);
    if (i < ROWS - 1 && j < COLS - 1 && squares[i+1][j+1].hidden && !squares[i+1][j+1].flagged)
      clickAction(i+1, j+1);
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
          for (int j = 0; j < COLS; j++){
            if (e.getSource().equals(squares[i][j])){
                if (calcAdjacentFlags(i, j) == calcAdjacentMines(i, j) && !squares[i][j].getValue().equals("hidden")){
                  doClickAround(i, j);
              }
            }
          }
        }
      }
    }
    
    public void mouseClicked(MouseEvent e){
        for (int i = 0; i < ROWS; i++){
          for (int j = 0; j < COLS; j++){
            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && e.getSource().equals(squares[i][j])){
              if (calcAdjacentFlags(i, j) == calcAdjacentMines(i, j) && !squares[i][j].getValue().equals("hidden")){
                doClickAround(i, j);
              }
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
          for (int j = 0; j < COLS; j++){
            if (e.getSource().equals(squares[i][j]) && SwingUtilities.isLeftMouseButton(e)){
              squares[i][j].doClick();
            }
            if (SwingUtilities.isRightMouseButton(e)
               && e.getSource().equals(squares[i][j]) && squares[i][j].hidden){
              if (!squares[i][j].flagged){
                mines_left--;
                minesLabel.setText("Mines: " + String.valueOf(mines_left));
              }
              if (squares[i][j].flagged){
                mines_left++;
                minesLabel.setText("Mines: " + String.valueOf(mines_left));
                }
              squares[i][j].toggleFlag();
            }
            
            if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown() && e.getSource().equals(squares[i][j])){
              if (calcAdjacentFlags(i, j) == calcAdjacentMines(i, j) && !squares[i][j].getValue().equals("hidden")){
                doClickAround(i, j);
              }
            }
          }
        }
      }
      }
    }
      
  int calcAdjacentMines(int i, int j){
    int adj = 0;
    if (i > 0 && squares[i - 1][j].getValue().equals("mine"))//up
      adj++;
    if (i > 0 && j > 0 && squares[i - 1][j - 1].getValue().equals("mine"))//up-left
      adj++;
    if (i > 0 && j < COLS - 1 && squares[i - 1][j + 1].getValue().equals("mine"))//up-right
      adj++;
    if (j > 0 && squares[i][j - 1].getValue().equals("mine"))//left
      adj++;
    if (j < COLS - 1 && squares[i][j + 1].getValue().equals("mine"))//right
      adj++;
    if (i < ROWS - 1 && squares[i + 1][j].getValue().equals("mine"))//down
      adj++;
    if (i < ROWS - 1 && j > 0 && squares[i + 1][j - 1].getValue().equals("mine"))//down-left
      adj++;
    if (i < ROWS - 1 && j < COLS - 1 && squares[i + 1][j + 1].getValue().equals("mine"))//down-right
      adj++;
    return adj;
  }

int calcAdjacentFlags(int i, int j){
  int adj = 0;
  if (i > 0 && squares[i - 1][j].flagged)//up
    adj++;
  if (i > 0 && j > 0 && squares[i - 1][j - 1].flagged)//up-left
    adj++;
  if (i > 0 && j < COLS - 1 && squares[i - 1][j + 1].flagged)//up-right
    adj++;
  if (j > 0 && squares[i][j - 1].flagged)//left
    adj++;
  if (j < COLS - 1 && squares[i][j + 1].flagged)//right
    adj++;
  if (i < ROWS - 1 && squares[i + 1][j].flagged)//down
    adj++;
  if (i < ROWS - 1 && j > 0 && squares[i + 1][j - 1].flagged)//down-left
    adj++;
  if (i < ROWS - 1 && j < COLS - 1 && squares[i + 1][j + 1].flagged)//down-right
    adj++;
  return adj;
  }


void revealAllMines(){
  for (int i = 0; i < ROWS; i++){
    for (int j = 0; j < COLS; j++){
      if ((squares[i][j].getValue().equals("mine") && !squares[i][j].flagged)
          || (!squares[i][j].getValue().equals("mine") && squares[i][j].flagged))
        squares[i][j].revealValue();
      }
    }
  }
}
