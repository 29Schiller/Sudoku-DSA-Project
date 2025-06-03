package mode;

import model.Puzzle;
import model.Solution;
import validator.SudokuValidator;
import validator.KillerSudokuValidator;

import javax.swing.*;
import java.awt.*;

public class KillerMode implements GameMode {
    private final SudokuValidator validator;

    public KillerMode() {
        this.validator = new KillerSudokuValidator();
    }

    @Override
    public void renderCell(JTextField cell, int row, int col, Puzzle puzzle, Solution solution) {
        if (puzzle.isFixed(row, col)) {
            int value = puzzle.getBoard().getCell(row, col);
            cell.setText(String.valueOf(value));
            cell.setEditable(false);
            cell.setBackground(new Color(200, 200, 200));
        }
    }

    @Override
    public boolean isValidMove(Puzzle puzzle, int row, int col, int value) {
        return validator.isValid(puzzle, row, col, value);
    }

    @Override
    public boolean isPuzzleComplete(Puzzle puzzle) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (puzzle.getBoard().getCell(row, col) == 0) return false;
            }
        }
        return validator.isValidSudoku(puzzle);
    }

    @Override
    public int getHint(Puzzle puzzle, int row, int col, Solution solution) {
        return solution.getCell(row, col);
    }

    @Override
    public boolean isSolutionCorrect(Puzzle puzzle, Solution solution) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int puzzleValue = puzzle.getBoard().getCell(row, col);
                int solutionValue = solution.getCell(row, col);
                if (puzzleValue != 0 && puzzleValue != solutionValue) return false;
            }
        }
        return true;
    }
}