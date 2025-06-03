package validator;

import model.Puzzle;

public interface SudokuValidator {
    boolean isValid(Puzzle puzzle, int row, int col, int value);
    boolean isValidSudoku(Puzzle puzzle);
}