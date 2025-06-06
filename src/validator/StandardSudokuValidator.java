package validator;

import model.Board;
import utils.Constants;

public class StandardSudokuValidator implements SudokuValidator {
    @Override
    public boolean isValid(Board board, int row, int col, int num) {
        if (num < 1 || num > 9) {
            return false; // Explicit range check
        }
        // Check row
        for (int x = 0; x < Constants.BOARD_SIZE; x++) {
            if (board.getCell(row, x) == num) {
                return false;
            }
        }
        // Check column
        for (int x = 0; x < Constants.BOARD_SIZE; x++) {
            if (board.getCell(x, col) == num) {
                return false;
            }
        }
        // Check 3x3 subgrid
        int startRow = row - row % Constants.SUBGRID_SIZE;
        int startCol = col - col % Constants.SUBGRID_SIZE;
        for (int i = 0; i < Constants.SUBGRID_SIZE; i++) {
            for (int j = 0; j < Constants.SUBGRID_SIZE; j++) {
                if (board.getCell(i + startRow, j + startCol) == num) {
                    return false;
                }
            }
        }
        // Note: This method checks local validity (row, column, subgrid).
        // It does not guarantee the move aligns with the puzzle's unique solution.
        return true;
    }

    @Override
    public boolean isValidSudoku(Board board) {
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                if (board.getCell(row, col) != 0) {
                    int temp = board.getCell(row, col);
                    board.setCell(row, col, 0);
                    if (!isValid(board, row, col, temp)) {
                        board.setCell(row, col, temp);
                        return false;
                    }
                    board.setCell(row, col, temp);
                }
            }
        }
        return true;
    }
}