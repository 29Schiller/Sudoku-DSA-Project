package validator;

import model.Puzzle;

public class StandardSudokuValidator implements SudokuValidator {
    private static final int SIZE = 9;

    @Override
    public boolean isValid(Puzzle puzzle, int row, int col, int value) {
        int[][] grid = puzzle.getBoard().getGrid();

        for (int x = 0; x < SIZE; x++) {
            if (grid[row][x] == value && x != col) return false;
            if (grid[x][col] == value && x != row) return false;
        }

        int startRow = row - row % 3, startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[i + startRow][j + startCol] == value && (i + startRow != row || j + startCol != col)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean isValidSudoku(Puzzle puzzle) {
        int[][] grid = puzzle.getBoard().getGrid();

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int value = grid[row][col];
                if (value != 0) {
                    grid[row][col] = 0;
                    if (!isValid(puzzle, row, col, value)) {
                        grid[row][col] = value;
                        return false;
                    }
                    grid[row][col] = value;
                }
            }
        }

        return true;
    }
}