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

public class MainGameFrame4D extends JFrame{
  private static final long serialVersionUID = 1;
  JPanel [][] floorsPanel;
  JPanel mainPanel;
  JPanel auxPanel;
  JPanel timePanel;
  JPanel minesPanel;
  
  JLabel timeLabel;
  JLabel minesLabel;
  
  Timer my_timer;
  
  HighscoreManager highscores4D;
  
  SquareButtonListener squareButtonListener;
  SquareMouseAdapter squareMouseAdapter;
  
  Square[][][][] squares;
  
  final int ROWS = 8;
  final int COLS = 7;
  final int FLOORS = 6;
  final int CUBES = 3;
  
  final int NUM_MINES = 30;
  final int AUX_BORDER = 7;
  final int PAN_BORDER_X = 4;
  final int PAN_BORDER_Y = 8;
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
  
  MainGameFrame4D(){
    super("Minesweeper");
    setLayout(new BorderLayout());
    first_click = true;
    mines_left = NUM_MINES;
    non_mines_left = ROWS * COLS * FLOORS * CUBES - NUM_MINES;
    
    floorsPanel = new JPanel[FLOORS][CUBES];
    
    for(int c = 0; c < CUBES; c++){
      for (int i = 0; i < FLOORS; i++){
        floorsPanel[i][c] = new JPanel();
        floorsPanel[i][c].setLayout(new GridLayout(ROWS, COLS));
      }
    }
    
    
    squares = new Square[ROWS][COLS][FLOORS][CUBES];
    
    squareButtonListener = new SquareButtonListener();
    squareMouseAdapter = new SquareMouseAdapter();
    
    for(int c = 0; c < CUBES; c++){
      for (int f = 0; f < FLOORS; f++){
        for (int i = 0; i < ROWS; i++){
          for (int j = 0; j < COLS; j++){
            squares[i][j][f][c] = new Square();
            floorsPanel[f][c].add(squares[i][j][f][c]);
            squares[i][j][f][c].addActionListener(squareButtonListener);
            squares[i][j][f][c].addMouseListener(squareMouseAdapter);
          }
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
    
    mainPanel = new JPanel(new GridLayout(CUBES, FLOORS));
    
    for (int c = 0; c < CUBES; c++){
      for (int i = 0; i < FLOORS; i++){
        floorsPanel[i][c].setBorder(BorderFactory.createEmptyBorder(PAN_BORDER_Y, PAN_BORDER_X, PAN_BORDER_Y, PAN_BORDER_X));
        mainPanel.add(floorsPanel[i][c]);
      }
    }
    
    calcAllSquares();
    
    highscores4D = new HighscoreManager("scores4.dat");
    Minesweeper.current_mode = "4";
    
    add(mainPanel, BorderLayout.CENTER);
    pack();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocation(200, 60);
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
    highscores4D.addScore(player_name, cur_time);
    Minesweeper.startNewGame(this);
  }
  
  void genRandomMine(){
    int x0 = rnd.nextInt(ROWS);
    int x1 = rnd.nextInt(COLS);
    int x2 = rnd.nextInt(FLOORS);
    int x3 = rnd.nextInt(CUBES);
    while (squares[x0][x1][x2][x3].getValue().equals("mine")){
      x0 = rnd.nextInt(ROWS);
      x1 = rnd.nextInt(COLS);
      x2 = rnd.nextInt(FLOORS);
      x3 = rnd.nextInt(CUBES);
    }
    squares[x0][x1][x2][x3].setValue("mine");
  }
  
  void genRandomMine(int i, int j, int k, int l){
    int x0 = rnd.nextInt(ROWS);
    int x1 = rnd.nextInt(COLS);
    int x2 = rnd.nextInt(FLOORS);
    int x3 = rnd.nextInt(CUBES);
    while (squares[x0][x1][x2][x3].getValue().equals("mine") || (x0 == i && x1 == j && x2 == k && x3 == l)){
      x0 = rnd.nextInt(ROWS);
      x1 = rnd.nextInt(COLS);
      x2 = rnd.nextInt(FLOORS);
      x3 = rnd.nextInt(CUBES);
    }
    squares[x0][x1][x2][x3].setValue("mine");
  }
  
  void clickAction(int i, int j, int k, int l){
    boolean ended = false;
    if (squares[i][j][k][l].getValue().equals("mine")){
      if (first_click){
        squares[i][j][k][l].setValue("0");
        genRandomMine(i, j, k, l);
        squares[i][j][k][l].setEnabled(true);
        calcAllSquares();
      }
      else{
        ended = true;
        squares[i][j][k][l].revealValue();
        loseGame();
      }
    }
    squares[i][j][k][l].revealValue();
    squares[i][j][k][l].setEnabled(false);
    non_mines_left--;
    if (squares[i][j][k][l].getValue().equals("0")){              
      doClickAround4D(i, j, k, l);
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
            for (int l = 0; l < CUBES; l++){
              if (e.getSource().equals(squares[i][j][k][l])){
                clickAction(i, j, k, l);
                first_click = false;
              }
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
          for (int c = 0; c < CUBES; c++){
            if (!squares[x][y][z][c].getValue().equals("mine")){
              squares[x][y][z][c].setValue(String.valueOf(calcAdjacentMines4D(x, y, z, c)));
            }
          }
        }
      }
    }
  }
  
  void doClickAround4D(int i, int j, int k, int l){
    doClickAround3D(i, j, k, l);
    if (l > 0){
      doClickAround3D(i, j, k, l-1);
      if (squares[i][j][k][l-1].hidden)
        clickAction(i, j, k, l-1);
    }
    if (l < CUBES - 1){
      doClickAround3D(i, j, k, l+1);
      if (squares[i][j][k][l+1].hidden)
        clickAction(i, j, k, l+1);
    }
  }
  
  void doClickAround3D(int i, int j, int k, int l){
    //k floor
    if (i > 0 && squares[i-1][j][k][l].hidden && !squares[i-1][j][k][l].flagged)
      clickAction(i-1, j, k, l);
    if (i > 0 && j > 0 && squares[i-1][j-1][k][l].hidden && !squares[i-1][j-1][k][l].flagged)
      clickAction(i-1, j-1, k, l);
    if (i > 0 && j < COLS - 1 && squares[i-1][j+1][k][l].hidden && !squares[i-1][j+1][k][l].flagged)
      clickAction(i-1, j+1, k, l);
    if (j > 0 && squares[i][j-1][k][l].hidden && !squares[i][j-1][k][l].flagged)
      clickAction(i, j-1, k, l);
    if (j < COLS - 1 && squares[i][j+1][k][l].hidden && !squares[i][j+1][k][l].flagged)
      clickAction(i, j+1, k, l);
    if (i < ROWS - 1 && squares[i+1][j][k][l].hidden && !squares[i+1][j][k][l].flagged)
      clickAction(i+1, j, k, l);
    if (i < ROWS - 1 && j > 0 && squares[i+1][j-1][k][l].hidden && !squares[i+1][j-1][k][l].flagged)
      clickAction(i+1, j-1, k, l);
    if (i < ROWS - 1 && j < COLS - 1 && squares[i+1][j+1][k][l].hidden && !squares[i+1][j+1][k][l].flagged)
      clickAction(i+1, j+1, k, l);
    
    //k-1 floor
    if (k > 0){
      if (i > 0 && squares[i-1][j][k-1][l].hidden && !squares[i-1][j][k-1][l].flagged)
        clickAction(i-1, j, k-1, l);
      if (i > 0 && j > 0 && squares[i-1][j-1][k-1][l].hidden && !squares[i-1][j-1][k-1][l].flagged)
        clickAction(i-1, j-1, k-1, l);
      if (i > 0 && j < COLS - 1 && squares[i-1][j+1][k-1][l].hidden && !squares[i-1][j+1][k-1][l].flagged)
        clickAction(i-1, j+1, k-1, l);
      if (j > 0 && squares[i][j-1][k-1][l].hidden && !squares[i][j-1][k-1][l].flagged)
        clickAction(i, j-1, k-1, l);
      if (j < COLS - 1 && squares[i][j+1][k-1][l].hidden && !squares[i][j+1][k-1][l].flagged)
        clickAction(i, j+1, k-1, l);
      if (i < ROWS - 1 && squares[i+1][j][k-1][l].hidden && !squares[i+1][j][k-1][l].flagged)
        clickAction(i+1, j, k-1, l);
      if (i < ROWS - 1 && j > 0 && squares[i+1][j-1][k-1][l].hidden && !squares[i+1][j-1][k-1][l].flagged)
        clickAction(i+1, j-1, k-1, l);
      if (i < ROWS - 1 && j < COLS - 1 && squares[i+1][j+1][k-1][l].hidden && !squares[i+1][j+1][k-1][l].flagged)
        clickAction(i+1, j+1, k-1, l);
      if (squares[i][j][k-1][l].hidden && !squares[i][j][k-1][l].flagged)
        clickAction(i, j, k-1, l);
    }
    
    //k+1 floor
    if (k < FLOORS - 1){
      if (i > 0 && squares[i-1][j][k+1][l].hidden && !squares[i-1][j][k+1][l].flagged)
        clickAction(i-1, j, k+1, l);
      if (i > 0 && j > 0 && squares[i-1][j-1][k+1][l].hidden && !squares[i-1][j-1][k+1][l].flagged)
        clickAction(i-1, j-1, k+1, l);
      if (i > 0 && j < COLS - 1 && squares[i-1][j+1][k+1][l].hidden&& !squares[i-1][j+1][k+1][l].flagged)
        clickAction(i-1, j+1, k+1, l);
      if (j > 0 && squares[i][j-1][k+1][l].hidden && !squares[i][j-1][k+1][l].flagged)
        clickAction(i, j-1, k+1, l);
      if (j < COLS - 1 && squares[i][j+1][k+1][l].hidden && !squares[i][j+1][k+1][l].flagged)
        clickAction(i, j+1, k+1, l);
      if (i < ROWS - 1 && squares[i+1][j][k+1][l].hidden && !squares[i+1][j][k+1][l].flagged)
        clickAction(i+1, j, k+1, l);
      if (i < ROWS - 1 && j > 0 && squares[i+1][j-1][k+1][l].hidden && !squares[i+1][j-1][k+1][l].flagged)
        clickAction(i+1, j-1, k+1, l);
      if (i < ROWS - 1 && j < COLS - 1 && squares[i+1][j+1][k+1][l].hidden && !squares[i+1][j+1][k+1][l].flagged)
        clickAction(i+1, j+1, k+1, l);
      if (squares[i][j][k+1][l].hidden && !squares[i][j][k+1][l].flagged)
        clickAction(i, j, k+1, l);
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
              for (int c = 0; c < CUBES; c++){
                if (e.getSource().equals(squares[i][j][k][c])){
                    if (calcAdjacentFlags4D(i, j, k, c) == calcAdjacentMines4D(i, j, k, c) && !squares[i][j][k][c].getValue().equals("hidden")){
                      doClickAround4D(i, j, k, c);
                  }
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
            for (int c = 0; c < CUBES; c++){
              if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && e.getSource().equals(squares[i][j][k][c])){
                if (calcAdjacentFlags4D(i, j, k, c) == calcAdjacentMines4D(i, j, k, c) && !squares[i][j][k][c].getValue().equals("hidden")){
                  doClickAround4D(i, j, k, c);
                }
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
            for (int l = 0; l < CUBES; l++){
              if (e.getSource().equals(squares[i][j][k][l])){
                for (int c = l - 1; c <= l + 1; c++){
                  if (c >= 0 && c <= CUBES - 1){
                    for (int f = k - 1; f <= k + 1; f++){
                      if (f >= 0 && f <= FLOORS - 1){
                        squares[i][j][f][c].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR_1));
                        if (HIGHLIGHT_NINE){
                          if (i>0)
                            squares[i-1][j][f][c].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR_2));
                          if (i<ROWS-1)
                            squares[i+1][j][f][c].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR_2));
                          if (j>0)  
                            squares[i][j-1][f][c].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR_2));
                          if (i>0 && j>0)  
                            squares[i-1][j-1][f][c].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR_2));
                          if (i<ROWS-1 && j>0)
                            squares[i+1][j-1][f][c].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR_2));
                          if (j<COLS-1)
                            squares[i][j+1][f][c].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR_2));
                          if (i>0 && j<COLS-1)
                            squares[i-1][j+1][f][c].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR_2));
                          if (i<ROWS-1 && j<COLS-1)
                            squares[i+1][j+1][f][c].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR_2));
                        }
                      }
                    }
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
            for (int l = 0; l < CUBES; l++){
              if (e.getSource().equals(squares[i][j][k][l])){
                for (int c = l - 1; c <= l + 1; c++){
                  if (c >= 0 && c <= CUBES - 1){
                    for (int f = k - 1; f <= k + 1; f++){
                      if (f >= 0 && f <= FLOORS - 1){
                        squares[i][j][f][c].setBorder(null);
                        if (HIGHLIGHT_NINE){
                          if (i>0)
                            squares[i-1][j][f][c].setBorder(null);
                          if (i<ROWS-1)
                            squares[i+1][j][f][c].setBorder(null);
                          if (j>0)
                            squares[i][j-1][f][c].setBorder(null);
                          if (i>0 && j>0)
                            squares[i-1][j-1][f][c].setBorder(null);
                          if (i<ROWS-1 && j>0)
                            squares[i+1][j-1][f][c].setBorder(null);
                          if (j<COLS-1)
                            squares[i][j+1][f][c].setBorder(null);
                          if (i>0 && j<COLS-1)
                            squares[i-1][j+1][f][c].setBorder(null);
                          if (i<ROWS-1 && j<COLS-1)
                            squares[i+1][j+1][f][c].setBorder(null);
                        }
                      }
                    }
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
              for (int l = 0; l < CUBES; l++){
                if (e.getSource().equals(squares[i][j][k][l]) && SwingUtilities.isLeftMouseButton(e)){
                  squares[i][j][k][l].doClick();
                }
                if (SwingUtilities.isRightMouseButton(e)
                   && e.getSource().equals(squares[i][j][k][l]) && squares[i][j][k][l].hidden){
                  if (!squares[i][j][k][l].flagged){
                    mines_left--;
                    minesLabel.setText("Mines: " + String.valueOf(mines_left));
                  }
                  if (squares[i][j][k][l].flagged){
                    mines_left++;
                    minesLabel.setText("Mines: " + String.valueOf(mines_left));
                    }
                  squares[i][j][k][l].toggleFlag();
                }
              
                if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown() && e.getSource().equals(squares[i][j][k][l])){
                  if (calcAdjacentFlags4D(i, j, k, l) == calcAdjacentMines4D(i, j, k, l) && !squares[i][j][k][l].getValue().equals("hidden")){
                    doClickAround4D(i, j, k, l);
                  }
                }
              }
            }
          }
        }
      }
    }
  }
      
  int calcAdjacentMines4D(int i, int j, int k, int l){
    int adj = 0;
    adj += calcAdjacentMines3D(i, j, k, l);
    if (l > 0){
      adj += calcAdjacentMines3D(i, j, k, l-1);
      if (squares[i][j][k][l-1].getValue().equals("mine"))
          adj++;
    }
    if (l < CUBES - 1){
      adj += calcAdjacentMines3D(i, j, k, l+1);
      if (squares[i][j][k][l+1].getValue().equals("mine"))
        adj++;
    }
    return adj;
  }
  
  int calcAdjacentMines3D(int i, int j, int k, int l){
    int adj = 0;
    //k floor
    if (i > 0 && squares[i - 1][j][k][l].getValue().equals("mine"))//up
      adj++;
    if (i > 0 && j > 0 && squares[i - 1][j - 1][k][l].getValue().equals("mine"))//up-left
      adj++;
    if (i > 0 && j < COLS - 1 && squares[i - 1][j + 1][k][l].getValue().equals("mine"))//up-right
      adj++;
    if (j > 0 && squares[i][j - 1][k][l].getValue().equals("mine"))//left
      adj++;
    if (j < COLS - 1 && squares[i][j + 1][k][l].getValue().equals("mine"))//right
      adj++;
    if (i < ROWS - 1 && squares[i + 1][j][k][l].getValue().equals("mine"))//down
      adj++;
    if (i < ROWS - 1 && j > 0 && squares[i + 1][j - 1][k][l].getValue().equals("mine"))//down-left
      adj++;
    if (i < ROWS - 1 && j < COLS - 1 && squares[i + 1][j + 1][k][l].getValue().equals("mine"))//down-right
      adj++;
    
    //k-1 floor
    if (k > 0){
      if (i > 0 && squares[i - 1][j][k - 1][l].getValue().equals("mine"))//up
        adj++;
      if (i > 0 && j > 0 && squares[i - 1][j - 1][k - 1][l].getValue().equals("mine"))//up-left
        adj++;
      if (i > 0 && j < COLS - 1 && squares[i - 1][j + 1][k - 1][l].getValue().equals("mine"))//up-right
        adj++;
      if (j > 0 && squares[i][j - 1][k - 1][l].getValue().equals("mine"))//left
        adj++;
      if (j < COLS - 1 && squares[i][j + 1][k - 1][l].getValue().equals("mine"))//right
        adj++;
      if (i < ROWS - 1 && squares[i + 1][j][k - 1][l].getValue().equals("mine"))//down
        adj++;
      if (i < ROWS - 1 && j > 0 && squares[i + 1][j - 1][k - 1][l].getValue().equals("mine"))//down-left
        adj++;
      if (i < ROWS - 1 && j < COLS - 1 && squares[i + 1][j + 1][k - 1][l].getValue().equals("mine"))//down-right
        adj++;
      if (squares[i][j][k - 1][l].getValue().equals("mine"))//down-right
        adj++;
    }
    
    //k+1 floor
    if (k < FLOORS - 1){
      if (i > 0 && squares[i - 1][j][k + 1][l].getValue().equals("mine"))//up
        adj++;
      if (i > 0 && j > 0 && squares[i - 1][j - 1][k + 1][l].getValue().equals("mine"))//up-left
        adj++;
      if (i > 0 && j < COLS - 1 && squares[i - 1][j + 1][k + 1][l].getValue().equals("mine"))//up-right
        adj++;
      if (j > 0 && squares[i][j - 1][k + 1][l].getValue().equals("mine"))//left
        adj++;
      if (j < COLS - 1 && squares[i][j + 1][k + 1][l].getValue().equals("mine"))//right
        adj++;
      if (i < ROWS - 1 && squares[i + 1][j][k + 1][l].getValue().equals("mine"))//down
        adj++;
      if (i < ROWS - 1 && j > 0 && squares[i + 1][j - 1][k + 1][l].getValue().equals("mine"))//down-left
        adj++;
      if (i < ROWS - 1 && j < COLS - 1 && squares[i + 1][j + 1][k + 1][l].getValue().equals("mine"))//down-right
        adj++;
      if (squares[i][j][k + 1][l].getValue().equals("mine"))//down-right
        adj++;
    }
    return adj;
  }

  
  int calcAdjacentFlags4D(int i, int j, int k, int l){
    int adj = 0;
    adj += calcAdjacentFlags3D(i, j, k, l);
    if (l > 0){
      adj += calcAdjacentFlags3D(i, j, k, l-1);
      if (squares[i][j][k][l-1].flagged)
        adj++;
    }
    if (l < CUBES - 1){
      adj += calcAdjacentFlags3D(i, j, k, l+1);
      if (squares[i][j][k][l+1].flagged)
        adj++;
    }
    return adj;
  }
  
  int calcAdjacentFlags3D(int i, int j, int k, int l){
    int adj = 0;
    //k floor
    if (i > 0 && squares[i - 1][j][k][l].flagged)//up
      adj++;
    if (i > 0 && j > 0 && squares[i - 1][j - 1][k][l].flagged)//up-left
      adj++;
    if (i > 0 && j < COLS - 1 && squares[i - 1][j + 1][k][l].flagged)//up-right
      adj++;
    if (j > 0 && squares[i][j - 1][k][l].flagged)//left
      adj++;
    if (j < COLS - 1 && squares[i][j + 1][k][l].flagged)//right
      adj++;
    if (i < ROWS - 1 && squares[i + 1][j][k][l].flagged)//down
      adj++;
    if (i < ROWS - 1 && j > 0 && squares[i + 1][j - 1][k][l].flagged)//down-left
      adj++;
    if (i < ROWS - 1 && j < COLS - 1 && squares[i + 1][j + 1][k][l].flagged)//down-right
      adj++;
    
    //k-1 floor
    if (k > 0){
      if (i > 0 && squares[i - 1][j][k - 1][l].flagged)//up
        adj++;
      if (i > 0 && j > 0 && squares[i - 1][j - 1][k - 1][l].flagged)//up-left
        adj++;
      if (i > 0 && j < COLS - 1 && squares[i - 1][j + 1][k - 1][l].flagged)//up-right
        adj++;
      if (j > 0 && squares[i][j - 1][k - 1][l].flagged)//left
        adj++;
      if (j < COLS - 1 && squares[i][j + 1][k - 1][l].flagged)//right
        adj++;
      if (i < ROWS - 1 && squares[i + 1][j][k - 1][l].flagged)//down
        adj++;
      if (i < ROWS - 1 && j > 0 && squares[i + 1][j - 1][k - 1][l].flagged)//down-left
        adj++;
      if (i < ROWS - 1 && j < COLS - 1 && squares[i + 1][j + 1][k - 1][l].flagged)//down-right
        adj++;
      if (squares[i][j][k - 1][l].flagged)//down-right
        adj++;
    }
    
    //k+1 floor
    if (k < FLOORS - 1){
      if (i > 0 && squares[i - 1][j][k + 1][l].flagged)//up
        adj++;
      if (i > 0 && j > 0 && squares[i - 1][j - 1][k + 1][l].flagged)//up-left
        adj++;
      if (i > 0 && j < COLS - 1 && squares[i - 1][j + 1][k + 1][l].flagged)//up-right
        adj++;
      if (j > 0 && squares[i][j - 1][k + 1][l].flagged)//left
        adj++;
      if (j < COLS - 1 && squares[i][j + 1][k + 1][l].flagged)//right
        adj++;
      if (i < ROWS - 1 && squares[i + 1][j][k + 1][l].flagged)//down
        adj++;
      if (i < ROWS - 1 && j > 0 && squares[i + 1][j - 1][k + 1][l].flagged)//down-left
        adj++;
      if (i < ROWS - 1 && j < COLS - 1 && squares[i + 1][j + 1][k + 1][l].flagged)//down-right
        adj++;
      if (squares[i][j][k + 1][l].flagged)//down-right
        adj++;
    }
    return adj;
  }
  
  
  void revealAllMines(){
    for (int i = 0; i < ROWS; i++){
      for (int j = 0; j < COLS; j++){
        for (int k = 0; k < FLOORS; k++){
          for (int l = 0; l < CUBES; l++){
            if ((squares[i][j][k][l].getValue().equals("mine") && !squares[i][j][k][l].flagged)
                || (!squares[i][j][k][l].getValue().equals("mine") && squares[i][j][k][l].flagged))
              squares[i][j][k][l].revealValue();
          }
        }
      }
    }
  }
}
