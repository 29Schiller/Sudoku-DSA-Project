package validator;

import model.Puzzle;
import model.Cage;
import utils.Point;

public class KillerSudokuValidator implements SudokuValidator {
    private final SudokuValidator standardValidator;

    public KillerSudokuValidator() {
        this.standardValidator = new StandardSudokuValidator();
    }

    @Override
    public boolean isValid(Puzzle puzzle, int row, int col, int value) {
        if (!standardValidator.isValid(puzzle, row, col, value)) return false;

        for (Cage cage : puzzle.getCages()) {
            if (cage.getCells().contains(new Point(row, col))) {
                for (Point p : cage.getCells()) {
                    if (p.x() != row || p.y() != col) {
                        int cellValue = puzzle.getBoard().getCell(p.x(), p.y());
                        if (cellValue == value) return false;
                    }
                }

                int sum = value;
                int filledCells = 1;
                for (Point p : cage.getCells()) {
                    if (p.x() != row || p.y() != col) {
                        int cellValue = puzzle.getBoard().getCell(p.x(), p.y());
                        if (cellValue != 0) {
                            sum += cellValue;
                            filledCells++;
                        }
                    }
                }

                if (sum > cage.getSum()) return false;
                if (filledCells == cage.getCells().size() && sum != cage.getSum()) return false;
            }
        }

        return true;
    }

    @Override
    public boolean isValidSudoku(Puzzle puzzle) {
        if (!standardValidator.isValidSudoku(puzzle)) return false;

        for (Cage cage : puzzle.getCages()) {
            int sum = 0;
            for (Point p : cage.getCells()) {
                int value = puzzle.getBoard().getCell(p.x(), p.y());
                if (value == 0) return true; // Incomplete cage, assume valid for now
                sum += value;
            }
            if (sum != cage.getSum()) return false;
        }

        return true;
    }
}