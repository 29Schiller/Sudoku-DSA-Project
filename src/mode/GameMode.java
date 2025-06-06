package mode;

import model.Puzzle;
import model.Solution;
import utils.Point;
import validator.SudokuValidator;

import javax.swing.*;
import java.util.Random;

public interface GameMode {
    void renderCell(JTextField cell, int row, int col, Puzzle puzzle, Solution solution);
    boolean isValidMove(Puzzle puzzle, Solution solution, SudokuValidator validator, int row, int col, int value);
    boolean isPuzzleComplete(Puzzle puzzle, SudokuValidator validator);
    Point getHintCell(Puzzle puzzle, Random random);
    int checkSolution(Puzzle puzzle, Solution solution);
}