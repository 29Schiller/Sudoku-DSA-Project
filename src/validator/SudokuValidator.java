package validator;

import model.Board;

public interface SudokuValidator {
    boolean isValid(Board board, int row, int col, int value);
    boolean isValidSudoku(Board board);
}