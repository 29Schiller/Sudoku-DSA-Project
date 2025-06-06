package solver;

import utils.Constants;
import validator.SudokuValidator;
import model.Board;

public class SudokuSolver {
    private final SudokuValidator validator;

    public SudokuSolver(SudokuValidator validator) {
        this.validator = validator;
    }

    public int[][] solve(int[][] grid, boolean isIceMode) {
        int[][] solvedGrid = new int[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            solvedGrid[i] = grid[i].clone();
        }
        solveSudoku(solvedGrid, 0, 0, isIceMode);
        return solvedGrid;
    }

    private boolean solveSudoku(int[][] grid, int row, int col, boolean isIceMode) {
        if (row >= Constants.BOARD_SIZE) {
            row = 0;
            col++;
            if (col >= Constants.BOARD_SIZE) {
                return true;
            }
        }
        if (!isEmpty(grid, row, col)) {
            return solveSudoku(grid, row + 1, col, isIceMode);
        }
        for (int num = 1; num <= 9; num++) {
            if (validator.isValid(new Board(grid), row, col, num)) {
                grid[row][col] = num;
                if (solveSudoku(grid, row + 1, col, isIceMode)) {
                    return true;
                }
                grid[row][col] = 0;
            }
        }
        return false;
    }

    private boolean isEmpty(int[][] grid, int row, int col) {
        return grid[row][col] == 0;
    }
}