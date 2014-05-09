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

public class MainGameFrame3D extends JFrame{
  private static final long serialVersionUID = 1;
  JPanel [] mainPanelFloor;
  JPanel mainPanel;
  JPanel auxPanel;
  JPanel timePanel;
  JPanel minesPanel;
  
  JLabel timeLabel;
  JLabel minesLabel;
  
  Timer my_timer;
  
  HighscoreManager highscores3D;
  
  SquareButtonListener squareButtonListener;
  SquareMouseAdapter squareMouseAdapter;
  
  Square[][][] squares;
  
  final int ROWS = 20;
  final int COLS = 7;
  final int FLOORS = 6;
  
  final int NUM_MINES = 50;
  final int AUX_BORDER = 7;
  final int PAN_BORDER = 4;
  final Font AUX_FONT = new Font(Font.MONOSPACED, Font.BOLD, 20);
  final int MAX_TIMER = 10000;
  final boolean HIGHLIGHT_NINE = false;
  final Color HIGHLIGHT_COLOR_1 = Color.red;
  final Color HIGHLIGHT_COLOR_2 = Color.black;
  
  boolean first_click;
  int mines_left;
  int non_mines_left;
  long cur_time;
  public static Random rnd = new Random(System.currentTimeMillis());
  
  MainGameFrame3D(){
    super("Minesweeper");
        
    setLayout(new BorderLayout());
    first_click = true;
    mines_left = NUM_MINES;
    non_mines_left = ROWS * COLS * FLOORS - NUM_MINES;
    
    mainPanelFloor = new JPanel[FLOORS];
    
    for (int i = 0; i < FLOORS; i++){
      mainPanelFloor[i] = new JPanel();
      mainPanelFloor[i].setLayout(new GridLayout(ROWS, COLS));
    }
    
    squares = new Square[ROWS][COLS][FLOORS];
    
    squareButtonListener = new SquareButtonListener();
    squareMouseAdapter = new SquareMouseAdapter();
    
    for (int f = 0; f < FLOORS; f++){
      for (int i = 0; i < ROWS; i++){
        for (int j = 0; j < COLS; j++){
          squares[i][j][f] = new Square();
          mainPanelFloor[f].add(squares[i][j][f]);
          squares[i][j][f].addActionListener(squareButtonListener);
          squares[i][j][f].addMouseListener(squareMouseAdapter);
        }
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
    
    mainPanel = new JPanel(new FlowLayout());
    
    for (int i = 0; i < FLOORS; i ++){
      mainPanelFloor[i].setBorder(BorderFactory.createEmptyBorder(PAN_BORDER, PAN_BORDER, PAN_BORDER, PAN_BORDER));
      mainPanel.add(mainPanelFloor[i]);
    }
    
    calcAllSquares();
    
    highscores3D = new HighscoreManager("scores3.dat");
    Minesweeper.current_mode = "3";
    
    add(mainPanel, BorderLayout.CENTER);
    pack();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocation(200, 100);
    setVisible(true);
  }
  
  public class TimerListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      cur_time++;
      if (cur_time < MAX_TIMER)
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
    highscores3D.addScore(player_name, cur_time);
    Minesweeper.startNewGame(this);
  }
  
  void genRandomMine(){
    int x = rnd.nextInt(ROWS);
    int y = rnd.nextInt(COLS);
    int z = rnd.nextInt(FLOORS);
    while (squares[x][y][z].getValue().equals("mine")){
      x = rnd.nextInt(ROWS);
      y = rnd.nextInt(COLS);
      z = rnd.nextInt(FLOORS);
    }
    squares[x][y][z].setValue("mine");
  }
  
  void genRandomMine(int i, int j, int k){
    int x = rnd.nextInt(ROWS);
    int y = rnd.nextInt(COLS);
    int z = rnd.nextInt(FLOORS);
    while (squares[x][y][z].getValue().equals("mine") || (x == i && y == j && z == k)){
      x = rnd.nextInt(ROWS);
      y = rnd.nextInt(COLS);
      z = rnd.nextInt(FLOORS);
    }
    squares[x][y][z].setValue("mine");
  }
  
  void clickAction(int i, int j, int k){
    boolean ended = false;
    if (squares[i][j][k].getValue().equals("mine")){
      if (first_click){
        squares[i][j][k].setValue("0");
        genRandomMine(i, j, k);
        squares[i][j][k].setEnabled(true);
        calcAllSquares();
      }
      else{
        ended = true;
        squares[i][j][k].revealValue();
        loseGame();
      }
    }
    squares[i][j][k].revealValue();
    squares[i][j][k].setEnabled(false);
    non_mines_left--;
    if (squares[i][j][k].getValue().equals("0")){              
      doClickAround(i, j, k);
    }
    if (non_mines_left == 0 && !ended){
      winGame();
    }
  }
  
  public class SquareButtonListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      my_timer.stop();
      for (int i = 0; i< ROWS; i++){
        for (int j = 0; j < COLS; j++){
          for (int k = 0; k < FLOORS; k++){
            if (e.getSource().equals(squares[i][j][k])){
              clickAction(i, j, k);
              first_click = false;
            }
          }
        }
      }
      my_timer.start();
    }
  }
  
  void calcAllSquares(){
    for (int x = 0; x < ROWS; x++){
      for (int y = 0; y < COLS; y++){
        for (int z = 0; z < FLOORS; z++){
          if (!squares[x][y][z].getValue().equals("mine")){
            squares[x][y][z].setValue(String.valueOf(calcAdjacentMines(x, y, z)));
          }
        }
      }
    }
  }
  
  void doClickAround(int i, int j, int k){
    //k floor
    if (i > 0 && squares[i-1][j][k].hidden && !squares[i-1][j][k].flagged)
      clickAction(i-1, j, k);
    if (i > 0 && j > 0 && squares[i-1][j-1][k].hidden && !squares[i-1][j-1][k].flagged)
      clickAction(i-1, j-1, k);
    if (i > 0 && j < COLS - 1 && squares[i-1][j+1][k].hidden && !squares[i-1][j+1][k].flagged)
      clickAction(i-1, j+1, k);
    if (j > 0 && squares[i][j-1][k].hidden && !squares[i][j-1][k].flagged)
      clickAction(i, j-1, k);
    if (j < COLS - 1 && squares[i][j+1][k].hidden && !squares[i][j+1][k].flagged)
      clickAction(i, j+1, k);
    if (i < ROWS - 1 && squares[i+1][j][k].hidden && !squares[i+1][j][k].flagged)
      clickAction(i+1, j, k);
    if (i < ROWS - 1 && j > 0 && squares[i+1][j-1][k].hidden && !squares[i+1][j-1][k].flagged)
      clickAction(i+1, j-1, k);
    if (i < ROWS - 1 && j < COLS - 1 && squares[i+1][j+1][k].hidden && !squares[i+1][j+1][k].flagged)
      clickAction(i+1, j+1, k);
    
    //k-1 floor
    if (k > 0){
      if (i > 0 && squares[i-1][j][k-1].hidden && !squares[i-1][j][k-1].flagged)
        clickAction(i-1, j, k-1);
      if (i > 0 && j > 0 && squares[i-1][j-1][k-1].hidden && !squares[i-1][j-1][k-1].flagged)
        clickAction(i-1, j-1, k-1);
      if (i > 0 && j < COLS - 1 && squares[i-1][j+1][k-1].hidden&& !squares[i-1][j+1][k-1].flagged)
        clickAction(i-1, j+1, k-1);
      if (j > 0 && squares[i][j-1][k-1].hidden && !squares[i][j-1][k-1].flagged)
        clickAction(i, j-1, k-1);
      if (j < COLS - 1 && squares[i][j+1][k-1].hidden && !squares[i][j+1][k-1].flagged)
        clickAction(i, j+1, k-1);
      if (i < ROWS - 1 && squares[i+1][j][k-1].hidden && !squares[i+1][j][k-1].flagged)
        clickAction(i+1, j, k-1);
      if (i < ROWS - 1 && j > 0 && squares[i+1][j-1][k-1].hidden && !squares[i+1][j-1][k-1].flagged)
        clickAction(i+1, j-1, k-1);
      if (i < ROWS - 1 && j < COLS - 1 && squares[i+1][j+1][k-1].hidden && !squares[i+1][j+1][k-1].flagged)
        clickAction(i+1, j+1, k-1);
      if (squares[i][j][k-1].hidden && !squares[i][j][k-1].flagged)
        clickAction(i, j, k-1);
    }
    
    //k+1 floor
    if (k < FLOORS - 1){
      if (i > 0 && squares[i-1][j][k+1].hidden && !squares[i-1][j][k+1].flagged)
        clickAction(i-1, j, k+1);
      if (i > 0 && j > 0 && squares[i-1][j-1][k+1].hidden && !squares[i-1][j-1][k+1].flagged)
        clickAction(i-1, j-1, k+1);
      if (i > 0 && j < COLS - 1 && squares[i-1][j+1][k+1].hidden&& !squares[i-1][j+1][k+1].flagged)
        clickAction(i-1, j+1, k+1);
      if (j > 0 && squares[i][j-1][k+1].hidden && !squares[i][j-1][k+1].flagged)
        clickAction(i, j-1, k+1);
      if (j < COLS - 1 && squares[i][j+1][k+1].hidden && !squares[i][j+1][k+1].flagged)
        clickAction(i, j+1, k+1);
      if (i < ROWS - 1 && squares[i+1][j][k+1].hidden && !squares[i+1][j][k+1].flagged)
        clickAction(i+1, j, k+1);
      if (i < ROWS - 1 && j > 0 && squares[i+1][j-1][k+1].hidden && !squares[i+1][j-1][k+1].flagged)
        clickAction(i+1, j-1, k+1);
      if (i < ROWS - 1 && j < COLS - 1 && squares[i+1][j+1][k+1].hidden && !squares[i+1][j+1][k+1].flagged)
        clickAction(i+1, j+1, k+1);
      if (squares[i][j][k+1].hidden && !squares[i][j][k+1].flagged)
        clickAction(i, j, k+1);
    }
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
            for (int k = 0; k < FLOORS; k++){
              if (e.getSource().equals(squares[i][j][k])){
                  if (calcAdjacentFlags(i, j, k) == calcAdjacentMines(i, j, k) && !squares[i][j][k].getValue().equals("hidden")){
                    doClickAround(i, j, k);
                }
              }
            }
          }
        }
      }
    }
    
    public void mouseClicked(MouseEvent e){
        for (int i = 0; i < ROWS; i++){
          for (int j = 0; j < COLS; j++){
            for (int k = 0; k < FLOORS; k++){
              if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && e.getSource().equals(squares[i][j][k])){
                if (calcAdjacentFlags(i, j, k) == calcAdjacentMines(i, j, k) && !squares[i][j][k].getValue().equals("hidden")){
                  doClickAround(i, j, k);
              }
            }
          }
        }
      }
    }
    
    public void mouseEntered(MouseEvent e){
      for (int i = 0; i < ROWS; i++){
        for (int j = 0; j < COLS; j++){
          for (int k = 0; k < FLOORS; k++){
            if (e.getSource().equals(squares[i][j][k])){
              for (int f = k - 1; f <= k + 1; f++){
                if (f >= 0 && f <= FLOORS - 1){
                  squares[i][j][f].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR_1));
                  if (HIGHLIGHT_NINE){
                    if (i>0)
                      squares[i-1][j][f].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR_2));
                    if (i<ROWS-1)
                      squares[i+1][j][f].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR_2));
                    if (j>0)  
                      squares[i][j-1][f].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR_2));
                    if (i>0 && j>0)  
                      squares[i-1][j-1][f].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR_2));
                    if (i<ROWS-1 && j>0)
                      squares[i+1][j-1][f].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR_2));
                    if (j<COLS-1)
                      squares[i][j+1][f].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR_2));
                    if (i>0 && j<COLS-1)
                      squares[i-1][j+1][f].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR_2));
                    if (i<ROWS-1 && j<COLS-1)
                      squares[i+1][j+1][f].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR_2));
                  }
                }
              }
            }
          }
        }
      }
    }
    
    public void mouseExited(MouseEvent e){
      for (int i = 0; i < ROWS; i++){
        for (int j = 0; j < COLS; j++){
          for (int k = 0; k < FLOORS; k++){
            if (e.getSource().equals(squares[i][j][k])){
              for (int f = k - 1; f <= k + 1; f++){
                if (f >= 0 && f <= FLOORS - 1){
                  squares[i][j][f].setBorder(null);
                  if (HIGHLIGHT_NINE){
                    if (i>0)
                      squares[i-1][j][f].setBorder(null);
                    if (i<ROWS-1)
                      squares[i+1][j][f].setBorder(null);
                    if (j>0)
                      squares[i][j-1][f].setBorder(null);
                    if (i>0 && j>0)
                      squares[i-1][j-1][f].setBorder(null);
                    if (i<ROWS-1 && j>0)
                      squares[i+1][j-1][f].setBorder(null);
                    if (j<COLS-1)
                      squares[i][j+1][f].setBorder(null);
                    if (i>0 && j<COLS-1)
                      squares[i-1][j+1][f].setBorder(null);
                    if (i<ROWS-1 && j<COLS-1)
                      squares[i+1][j+1][f].setBorder(null);
                  }
                }
              }
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
            for (int k = 0; k < FLOORS; k++){
              if (e.getSource().equals(squares[i][j][k]) && SwingUtilities.isLeftMouseButton(e)){
                squares[i][j][k].doClick();
              }
              if (SwingUtilities.isRightMouseButton(e)
                 && e.getSource().equals(squares[i][j][k]) && squares[i][j][k].hidden){
                if (!squares[i][j][k].flagged){
                  mines_left--;
                  minesLabel.setText("Mines: " + String.valueOf(mines_left));
                }
                if (squares[i][j][k].flagged){
                  mines_left++;
                  minesLabel.setText("Mines: " + String.valueOf(mines_left));
                  }
                squares[i][j][k].toggleFlag();
              }
              
              if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown() && e.getSource().equals(squares[i][j][k])){
                if (calcAdjacentFlags(i, j, k) == calcAdjacentMines(i, j, k) && !squares[i][j][k].getValue().equals("hidden")){
                  doClickAround(i, j, k);
                }
              }
            }
          }
        }
      }
    }
  }
      
  int calcAdjacentMines(int i, int j, int k){
    int adj = 0;
    //k floor
    if (i > 0 && squares[i - 1][j][k].getValue().equals("mine"))//up
      adj++;
    if (i > 0 && j > 0 && squares[i - 1][j - 1][k].getValue().equals("mine"))//up-left
      adj++;
    if (i > 0 && j < COLS - 1 && squares[i - 1][j + 1][k].getValue().equals("mine"))//up-right
      adj++;
    if (j > 0 && squares[i][j - 1][k].getValue().equals("mine"))//left
      adj++;
    if (j < COLS - 1 && squares[i][j + 1][k].getValue().equals("mine"))//right
      adj++;
    if (i < ROWS - 1 && squares[i + 1][j][k].getValue().equals("mine"))//down
      adj++;
    if (i < ROWS - 1 && j > 0 && squares[i + 1][j - 1][k].getValue().equals("mine"))//down-left
      adj++;
    if (i < ROWS - 1 && j < COLS - 1 && squares[i + 1][j + 1][k].getValue().equals("mine"))//down-right
      adj++;
    
    //k-1 floor
    if (k > 0){
      if (i > 0 && squares[i - 1][j][k - 1].getValue().equals("mine"))//up
        adj++;
      if (i > 0 && j > 0 && squares[i - 1][j - 1][k - 1].getValue().equals("mine"))//up-left
        adj++;
      if (i > 0 && j < COLS - 1 && squares[i - 1][j + 1][k - 1].getValue().equals("mine"))//up-right
        adj++;
      if (j > 0 && squares[i][j - 1][k - 1].getValue().equals("mine"))//left
        adj++;
      if (j < COLS - 1 && squares[i][j + 1][k - 1].getValue().equals("mine"))//right
        adj++;
      if (i < ROWS - 1 && squares[i + 1][j][k - 1].getValue().equals("mine"))//down
        adj++;
      if (i < ROWS - 1 && j > 0 && squares[i + 1][j - 1][k - 1].getValue().equals("mine"))//down-left
        adj++;
      if (i < ROWS - 1 && j < COLS - 1 && squares[i + 1][j + 1][k - 1].getValue().equals("mine"))//down-right
        adj++;
      if (squares[i][j][k - 1].getValue().equals("mine"))//down-right
        adj++;
    }
    
    //k+1 floor
    if (k < FLOORS - 1){
      if (i > 0 && squares[i - 1][j][k + 1].getValue().equals("mine"))//up
        adj++;
      if (i > 0 && j > 0 && squares[i - 1][j - 1][k + 1].getValue().equals("mine"))//up-left
        adj++;
      if (i > 0 && j < COLS - 1 && squares[i - 1][j + 1][k + 1].getValue().equals("mine"))//up-right
        adj++;
      if (j > 0 && squares[i][j - 1][k + 1].getValue().equals("mine"))//left
        adj++;
      if (j < COLS - 1 && squares[i][j + 1][k + 1].getValue().equals("mine"))//right
        adj++;
      if (i < ROWS - 1 && squares[i + 1][j][k + 1].getValue().equals("mine"))//down
        adj++;
      if (i < ROWS - 1 && j > 0 && squares[i + 1][j - 1][k + 1].getValue().equals("mine"))//down-left
        adj++;
      if (i < ROWS - 1 && j < COLS - 1 && squares[i + 1][j + 1][k + 1].getValue().equals("mine"))//down-right
        adj++;
      if (squares[i][j][k + 1].getValue().equals("mine"))//down-right
        adj++;
    }
    return adj;
  }

  int calcAdjacentFlags(int i, int j, int k){
    int adj = 0;
    //k floor
    if (i > 0 && squares[i - 1][j][k].flagged)//up
      adj++;
    if (i > 0 && j > 0 && squares[i - 1][j - 1][k].flagged)//up-left
      adj++;
    if (i > 0 && j < COLS - 1 && squares[i - 1][j + 1][k].flagged)//up-right
      adj++;
    if (j > 0 && squares[i][j - 1][k].flagged)//left
      adj++;
    if (j < COLS - 1 && squares[i][j + 1][k].flagged)//right
      adj++;
    if (i < ROWS - 1 && squares[i + 1][j][k].flagged)//down
      adj++;
    if (i < ROWS - 1 && j > 0 && squares[i + 1][j - 1][k].flagged)//down-left
      adj++;
    if (i < ROWS - 1 && j < COLS - 1 && squares[i + 1][j + 1][k].flagged)//down-right
      adj++;
    
    //k-1 floor
    if (k > 0){
      if (i > 0 && squares[i - 1][j][k - 1].flagged)//up
        adj++;
      if (i > 0 && j > 0 && squares[i - 1][j - 1][k - 1].flagged)//up-left
        adj++;
      if (i > 0 && j < COLS - 1 && squares[i - 1][j + 1][k - 1].flagged)//up-right
        adj++;
      if (j > 0 && squares[i][j - 1][k - 1].flagged)//left
        adj++;
      if (j < COLS - 1 && squares[i][j + 1][k - 1].flagged)//right
        adj++;
      if (i < ROWS - 1 && squares[i + 1][j][k - 1].flagged)//down
        adj++;
      if (i < ROWS - 1 && j > 0 && squares[i + 1][j - 1][k - 1].flagged)//down-left
        adj++;
      if (i < ROWS - 1 && j < COLS - 1 && squares[i + 1][j + 1][k - 1].flagged)//down-right
        adj++;
      if (squares[i][j][k - 1].flagged)//down-right
        adj++;
    }
    
    //k+1 floor
    if (k < FLOORS - 1){
      if (i > 0 && squares[i - 1][j][k + 1].flagged)//up
        adj++;
      if (i > 0 && j > 0 && squares[i - 1][j - 1][k + 1].flagged)//up-left
        adj++;
      if (i > 0 && j < COLS - 1 && squares[i - 1][j + 1][k + 1].flagged)//up-right
        adj++;
      if (j > 0 && squares[i][j - 1][k + 1].flagged)//left
        adj++;
      if (j < COLS - 1 && squares[i][j + 1][k + 1].flagged)//right
        adj++;
      if (i < ROWS - 1 && squares[i + 1][j][k + 1].flagged)//down
        adj++;
      if (i < ROWS - 1 && j > 0 && squares[i + 1][j - 1][k + 1].flagged)//down-left
        adj++;
      if (i < ROWS - 1 && j < COLS - 1 && squares[i + 1][j + 1][k + 1].flagged)//down-right
        adj++;
      if (squares[i][j][k + 1].flagged)//down-right
        adj++;
    }
    return adj;
  }
  
  void revealAllMines(){
    for (int i = 0; i < ROWS; i++){
      for (int j = 0; j < COLS; j++){
        for (int k = 0; k < FLOORS; k++){
          if ((squares[i][j][k].getValue().equals("mine") && !squares[i][j][k].flagged)
              || (!squares[i][j][k].getValue().equals("mine") && squares[i][j][k].flagged))
            squares[i][j][k].revealValue();
        }
      }
    }
  }
}


