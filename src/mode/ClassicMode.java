package mode;

import model.Puzzle;
import model.Solution;
import utils.Constants;
import utils.Point;
import validator.SudokuValidator;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClassicMode implements GameMode {
    @Override
    public void renderCell(JTextField cell, int row, int col, Puzzle puzzle, Solution solution) {
        if (!puzzle.getBoard().isEmpty(row, col)) {
            cell.setText(String.valueOf(puzzle.getBoard().getCell(row, col)));
            cell.setEditable(false);
            cell.setBackground(Constants.FIXED_CELL_COLOR);
            cell.setFont(new Font("Arial", Font.BOLD, 16));
        } else {
            cell.setText("");
            cell.setEditable(true);
            cell.setFont(new Font("Arial", Font.PLAIN, 16));
        }
    }

    @Override
    public boolean isValidMove(Puzzle puzzle, Solution solution, SudokuValidator validator, int row, int col, int value) {
        if (value < 1 || value > 9) {
            return false; // Explicit range check
        }
        int originalValue = puzzle.getBoard().getCell(row, col);
        puzzle.getBoard().setCell(row, col, 0);
        boolean isValid = validator.isValid(puzzle.getBoard(), row, col, value);
        puzzle.getBoard().setCell(row, col, originalValue);
        // Note: This checks local validity (row, column, subgrid).
        // It does not guarantee the move aligns with the unique solution.
        return isValid;
    }

    @Override
    public boolean isPuzzleComplete(Puzzle puzzle, SudokuValidator validator) {
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                if (puzzle.getBoard().isEmpty(row, col)) {
                    return false;
                }
            }
        }
        return validator.isValidSudoku(puzzle.getBoard());
    }

    @Override
    public Point getHintCell(Puzzle puzzle, Random random) {
        List<Point> emptyCells = new ArrayList<>();
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                if (!puzzle.isFixed(row, col) && puzzle.getBoard().isEmpty(row, col)) {
                    emptyCells.add(new Point(row, col));
                }
            }
        }
        return emptyCells.isEmpty() ? null : emptyCells.get(random.nextInt(emptyCells.size()));
    }

    @Override
    public int checkSolution(Puzzle puzzle, Solution solution) {
        int errors = 0;
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                if (!puzzle.isFixed(row, col) && !puzzle.getBoard().isEmpty(row, col)) {
                    if (puzzle.getBoard().getCell(row, col) != solution.getCell(row, col)) {
                        errors++;
                    }
                }
            }
        }
        return errors;
    }
}