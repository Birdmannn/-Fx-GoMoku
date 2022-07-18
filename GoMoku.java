package com.example.gomoku;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class GoMoku extends Application {

    GoMokuBoard board;

    private static Button newGameButton;
    private static Button resignButton;
    private static Label message;

    public void start(Stage stage) {
        message = new Label("Click board to start.");
        message.setStyle("-fx-font-size: 11pt; -fx-text-fill: lightgray");

        newGameButton = new Button("New Game");
        newGameButton.setOnAction(e -> board.doNewGame());
        resignButton = new Button("Resign");
        resignButton.setOnAction(e -> board.doResign());

        board = new GoMokuBoard();
        board.doNewGame();

        board.setOnMouseClicked(e -> board.mousePressed(e));

        board.relocate(30,30);
        newGameButton.relocate(370,120);
        resignButton.relocate(370,200);
        message.relocate(30,380);

        resignButton.setManaged(false);
        resignButton.resize(100,30);
        newGameButton.setManaged(false);
        newGameButton.resize(100,30);

        Pane root = new Pane();

        root.setPrefWidth(500);
        root.setPrefHeight(450);

        //Add the child nodes to the Pane and set up the rest of the GUI

        root.getChildren().addAll(board, newGameButton, resignButton, message);
        root.setStyle("-fx-background-color: darkgreen; -fx-border-color: darkred; -fx-border-width: 3px");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Go Moku");
        stage.show();
    }




    //-------------------------------------Nested Classes--------------------------------------------------

    public static class GoMokuData {

        static final int WHITE = 1, BLACK = 2;
        private final int[][] board;
        private int winRow, winCol, winRow2, winCol2;
        public static int arrayCount;                      //board[r][c] is the contents of row r, column c.

        GoMokuData() {
            board = new int[13][13];                        //create an array to store five values of winData.
        }
        int pieceAt(int row, int col) {
            return board[row][col];
        }

        void setData(int row, int col, int color) {
            board[row][col] = color;
        }
        public int getStartRow() {
            return winRow;
        }

        public int getStartCol() {
            return winCol;
        }

        public int getEndRow() {
            return winRow2;
        }

        public int getEndCol() {
            return winCol2;
        }

        private int cP(int currentPlayer, int row, int col, int dirX, int dirY) {
            int ct = 1;
            int r, c;



            r = row + dirX;
            c = col + dirY;
            while(r >= 0 && r < 13 && c >= 0 && c < 13 && board[r][c] == currentPlayer) {


                ct++;
                r += dirX;
                c += dirY;


            }
            winRow =  r - dirX;
            winCol = c - dirY;

            r = row - dirX;
            c = col - dirY;
            while(r >= 0 && r < 13 && c >= 0 && c < 13 && board[r][c] == currentPlayer) {

                ct++;
                r -= dirX;
                c -= dirY;
            }
            winRow2 = r + dirX;
            winCol2 = c + dirY;

            return ct;

        }

        public boolean checkGame(int cp, int r, int c) {

           if(cP(cp,r,c,0,1) >= 5)
               return true;
           else if(cP(cp,r,c,1,0) >=5)
               return true;
           else if(cP(cp,r,c,1,1) >= 5)
               return true;
           else if(cP(cp,r,c,-1,1) >= 5)
               return true;

           return false;
        }

        public boolean checkStatus(int currentPlayer, int row, int col) {
            boolean value =  checkGame(currentPlayer,row, col);
            for (int[] ints : board) {
                for (int anInt : ints) {
                    if (anInt != 0)
                        arrayCount++;
                }
            }
            return arrayCount == (13 * 13) || value;
        }

    }
    public static class GoMokuBoard extends Canvas {

        GoMokuData board;
        static boolean gameInProgress, newGame;
        public int currentPlayer;
        int selectedRow, selectedCol;
        static final int EMPTY = 0, WHITE = 1, BLACK = 2;
        boolean value;

        GoMokuBoard() {
            super(312,312);
        }

        public void doNewGame() {
            board = new GoMokuData();
            newGame = true;
            currentPlayer = WHITE;
            gameInProgress = true;
            drawBoard();
            message.setText("White plays first.");
        }
        public void doResign() {
            gameInProgress = false;
            gameStatus(0);
        }


        public void drawBoard() {

            GraphicsContext g = getGraphicsContext2D();
            g.setFont(Font.font(18));
            g.setStroke(Color.BLACK);
            g.setLineWidth(2);



            value = board.checkStatus(currentPlayer,selectedRow,selectedCol);
            //Draw the board..
            for(int row = 0; row < 13; row++) {
                for(int col = 0; col < 13; col++) {
                    g.setFill(Color.SANDYBROWN);
                    g.fillRect(row*24, col*24, 24, 24);
                    g.strokeRect(row*24, col*24, 24, 24);
                    if(board.pieceAt(row,col) == GoMokuData.WHITE) {
                        g.setFill(Color.WHITE);
                        g.strokeOval(row*24+3,col*24+3,18,18);
                        g.fillOval(row*24+3,col*24+3,18,18);
                    }
                    else if(board.pieceAt(row,col) == GoMokuData.BLACK) {
                        g.setFill(Color.BLACK);
                        g.strokeOval(row*24+3,col*24+3,18,18);
                        g.fillOval(row*24+3,col*24+3,18,18);
                    }
                }

            }

            if(value) {
                gameInProgress = false;
                if(GoMokuData.arrayCount == (13 * 13))
                    currentPlayer = 0;
                else {
                    g.setLineWidth(1);
                    g.setStroke(Color.GREEN);
                    g.strokeLine(board.getStartRow()*24+12,board.getStartCol()*24+12, board.getEndRow()*24+12, board.getEndCol()*24+12);
                }
            }
            else {
                if(currentPlayer == WHITE) {
                    if(newGame) {
                        currentPlayer = WHITE;
                        newGame = false;
                    }
                    else {
                        currentPlayer = BLACK;
                    }
                }
                else
                    currentPlayer = WHITE;

            }

            gameStatus(currentPlayer);
        }



        public void mousePressed(MouseEvent evt) {
            if(!gameInProgress)
                return;
            double x = evt.getX();
            double y = evt.getY();
            int xx = (int)((x - 2) / 24);
            int yy = (int)((y - 2) / 24);

            selectedRow = Math.abs(xx);
            selectedCol = Math.abs(yy);
            if(board.pieceAt(selectedRow,selectedCol) != EMPTY)
                return;
            board.setData(selectedRow,selectedCol,currentPlayer);

            drawBoard();

        }


        public void gameStatus(int currentPlayer) {
            if(!gameInProgress) {
               if(currentPlayer == EMPTY) {
                   message.setText("Start New Game.");
               }
               else if(currentPlayer == WHITE) {
                   newGameButton.setDisable(false);
                   message.setText("Game Ended. WHITE Wins.");
               }
               else {
                   message.setText("Game ended. BLACK Wins");
               }
               newGameButton.setDisable(false);
               resignButton.setDisable(true);
            }
            else {
                if(currentPlayer == WHITE) {
                    message.setText("White's turn.");
                }
                else if(currentPlayer == BLACK) {
                    message.setText("Black's turn.");
                }
                resignButton.setDisable(false);
                newGameButton.setDisable(true);
            }


        }
    }

    public static void main(String[] args) {
        launch();
    }
}