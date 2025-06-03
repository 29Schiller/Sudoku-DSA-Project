package mode;

import model.Puzzle;
import model.Solution;

import javax.swing.*;
import java.awt.*;

public interface GameMode {
    void renderCell(JTextField cell, int row, int col, Puzzle puzzle, Solution solution);
    boolean isValidMove(Puzzle puzzle, int row, int col, int value);
    boolean isPuzzleComplete(Puzzle puzzle);
    int getHint(Puzzle puzzle, int row, int col, Solution solution);
    boolean isSolutionCorrect(Puzzle puzzle, Solution solution);
}