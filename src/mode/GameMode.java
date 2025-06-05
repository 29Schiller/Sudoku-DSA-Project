package mode;

import javax.swing.JTextField;

import model.Puzzle;
import model.Solution;

public interface GameMode {
    void renderCell(JTextField cell, int row, int col, Puzzle puzzle, Solution solution);
    boolean isValidMove(Puzzle puzzle, int row, int col, int value);
    boolean isPuzzleComplete(Puzzle puzzle);
    boolean isSolutionCorrect(Puzzle puzzle, Solution solution);
    int getHint(Puzzle puzzle, int row, int col, Solution solution);
}