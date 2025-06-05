package view;

import java.awt.Color;

import mode.GameMode;
import model.Puzzle;
import model.Solution;

public interface GameScreen {
    void setListener(GameScreenListener listener);
    void updateBoard(Puzzle puzzle, Solution solution, GameMode mode);
    void updateCell(int row, int col, String value, Color foreground, Color background, boolean editable);
    void updateTime(String time);
    void updateScore(int score);
    void updateStatus(String status);
    void showVictoryMessage(String message);
    boolean confirmSolve();
    void stopTimer();
    void setVisible(boolean visible);
    void dispose();
}