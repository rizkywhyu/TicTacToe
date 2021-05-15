package com.example.tictactoe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandingBoard {
    enum Mark {BLANK, X, O};

    public Mark[][] standingBoard = new Mark[3][3];

    private static final Logger log = LoggerFactory.getLogger(StandingBoard.class);

    public void clear() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                standingBoard[i][j] = Mark.BLANK;
            }
        }
    }

    public String markAt(int row, int col) {
        Mark mark = standingBoard[row][col];
        if (mark.equals(Mark.X)) {
            return "X";
        }
        if (mark.equals(Mark.O)) {
            return "O";
        }
        if (mark.equals(Mark.BLANK)) {
            return " ";
        }
        return "#";
    }

    public void move(int row, int col, Mark mark) throws Exception {
        if (standingBoard[row][col] != Mark.BLANK) {
            throw new Exception( "Square @ (" + row + ", " + col + ") is not empty");
        }

        if (mark == Mark.BLANK) {
            throw new IllegalArgumentException("You move "+ row +", "+ col +" is not valid");
        }
        standingBoard[row][col] = mark;
    }

    public boolean draw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (standingBoard[i][j].equals(Mark.BLANK)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean winner(Mark mark) {
        //check for three in a row down
        for (int i = 0; i < 3; i++) {
            boolean win = true;
            for (int j = 0; win && (j < 3); j++) {
                if (standingBoard[i][j] != mark) {
                    win = false;
                }
            }
            if (win) {
                return true;
            }
        }

        //check for three in cross
        for (int j = 0; j < 3; j++) {
            boolean win = true;
            for (int i = 0; win && (i < 3); i++) {
                if (standingBoard[j][i] != mark) {
                    win = false;
                }
            }
            if (win) {
                return true;
            }
        }

        if (standingBoard[0][0] == mark && standingBoard[1][1] == mark && standingBoard[2][2] == mark) {
            return true;
        }

        if (standingBoard[2][0] == mark && standingBoard[1][1] == mark && standingBoard[0][2] == mark) {
            return true;
        }

        return false;
    }
}
