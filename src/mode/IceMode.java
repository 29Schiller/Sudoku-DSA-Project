// In IceMode.java

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

public class IceMode implements GameMode {
    @Override
    public void renderCell(JTextField cell, int row, int col, Puzzle puzzle, Solution solution) {
        if (!puzzle.getBoard().isEmpty(row, col)) {
            // If the cell has an initial value (fixed cell from the puzzle generator)
            cell.setText(String.valueOf(puzzle.getBoard().getCell(row, col)));
            cell.setEditable(false);
            cell.setBackground(Constants.FIXED_CELL_COLOR); // Fixed cells are light gray
            cell.setForeground(Color.BLACK); // Fixed cells text color
            cell.setFont(new Font("Arial", Font.BOLD, 16));
        } else {
            // If the cell is empty
            cell.setText("");
            // Determine if the cell is frozen and set properties accordingly
            if (puzzle.isFrozen(row, col)) {
                cell.setBackground(Constants.FROZEN_CELL_COLOR); // Pale blue for frozen cells
                cell.setEditable(false); // Frozen cells are not editable
                cell.setForeground(Color.DARK_GRAY); // Optional: differentiate text color for frozen cells
                cell.setFont(new Font("Arial", Font.PLAIN, 16));
            } else {
                // Not a fixed cell and not frozen, so it's a regular editable empty cell
                cell.setEditable(true);
                // Set default background based on subgrid for unfrozen, editable cells
                cell.setBackground(((row / Constants.SUBGRID_SIZE + col / Constants.SUBGRID_SIZE) % 2 == 0)
                        ? Constants.ALTERNATE_CELL_COLOR : Constants.DEFAULT_CELL_COLOR);
                cell.setForeground(Color.BLACK);
                cell.setFont(new Font("Arial", Font.PLAIN, 16));
            }
        }
    }

    @Override
    public boolean isValidMove(Puzzle puzzle, Solution solution, SudokuValidator validator, int row, int col, int value) {
        if (value < 1 || value > 9) {
            return false; // Explicit range check
        }
        // Save original value to restore after validation check
        int originalValue = puzzle.getBoard().getCell(row, col);
        puzzle.getBoard().setCell(row, col, 0); // Temporarily clear current cell for validation
        boolean isValid = validator.isValid(puzzle.getBoard(), row, col, value);
        puzzle.getBoard().setCell(row, col, originalValue); // Restore original value

        // Note: This checks local validity (row, column, subgrid).
        // It does not guarantee the move aligns with the unique solution.
        return isValid;
    }

    @Override
    public boolean isPuzzleComplete(Puzzle puzzle, SudokuValidator validator) {
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                if (puzzle.getBoard().isEmpty(row, col)) {
                    return false; // If any cell is empty, puzzle is not complete
                }
            }
        }
        return validator.isValidSudoku(puzzle.getBoard()); // Check if the filled board is a valid Sudoku
    }

    @Override
    public Point getHintCell(Puzzle puzzle, Random random) {
        List<Point> emptyCells = new ArrayList<>();
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for ( int col = 0; col < Constants.BOARD_SIZE; col++) {
                // Only provide hints for empty, non-fixed cells
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
                // Only check user-entered cells (not fixed, not empty)
                if (!puzzle.isFixed(row, col) && !puzzle.getBoard().isEmpty(row, col)) {
                    if (puzzle.getBoard().getCell(row, col) != solution.getCell(row, col)) {
                        errors++;
                    }
                }
            }
        }
        return errors;
    }

    // MODIFIED: Removed the subgrid boundary check in hasAdjacentUserFilledCorrectCellInSubgrid4Dir.
    // This allows a valid adjacent move to unfreeze a cell even if it's outside the same 3x3 subgrid.
    private boolean hasAdjacentUserFilledCorrectCellInSubgrid4Dir(Puzzle puzzle, Solution solution, int row, int col) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Top, bottom, left, right

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (newRow >= 0 && newRow < Constants.BOARD_SIZE &&
                    newCol >= 0 && newCol < Constants.BOARD_SIZE) {

                // The previous check for (newRow, newCol) being within the same 3x3 subgrid has been removed.
                // Now, any 4-directionally adjacent cell (within board limits) can unfreeze.
                if (!puzzle.getBoard().isEmpty(newRow, newCol) && // Is the neighbor filled?
                        !puzzle.isFixed(newRow, newCol) &&           // Is it a user-entered cell (not an initial fixed cell)?
                        puzzle.getBoard().getCell(newRow, newCol) == solution.getCell(newRow, newCol)) { // Is the user-entered value correct?
                    return true;
                }
            }
        }
        return false;
    }

    public void updateFrozenCellState(Puzzle puzzle, Solution solution, int row, int col, JTextField cell) {
        if (puzzle.isFrozen(row, col)){
            boolean shouldUnlock = hasAdjacentUserFilledCorrectCellInSubgrid4Dir(puzzle, solution, row, col);

            if (shouldUnlock) {
                puzzle.setFrozen(row, col, false); // This line is crucial for internal state
                cell.setBackground(((row / Constants.SUBGRID_SIZE + col / Constants.SUBGRID_SIZE) % 2 == 0)
                        ? Constants.ALTERNATE_CELL_COLOR : Constants.DEFAULT_CELL_COLOR);
                cell.setEditable(true);
                cell.setForeground(Color.BLACK);
            } else {
                // If it shouldn't unlock, ensure it visually stays frozen.
                cell.setBackground(Constants.FROZEN_CELL_COLOR);
                cell.setEditable(false);
            }
        }
    }
}