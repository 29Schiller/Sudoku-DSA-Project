package generator;

import model.Board;
import model.Puzzle;
import model.Solution;
import utils.Constants;
import utils.DifficultyLevel;
import utils.Point;
import validator.SudokuValidator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class IcePuzzleGenerator implements PuzzleGenerator {
    private final Random random = new Random();
    private final ClassicPuzzleGenerator classicGenerator;
    private Solution solution;

    public IcePuzzleGenerator(SudokuValidator validator) {
        this.classicGenerator = new ClassicPuzzleGenerator(validator);
    }

    @Override
    public Puzzle generatePuzzle(DifficultyLevel difficulty) {
        Puzzle classicPuzzle = classicGenerator.generatePuzzle(difficulty);
        this.solution = classicGenerator.getSolution();
        Board puzzleBoard = classicPuzzle.getBoard();
        boolean[][] fixedCells = new boolean[Constants.BOARD_SIZE][Constants.BOARD_SIZE];

        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                fixedCells[row][col] = puzzleBoard.getCell(row, col) != 0;
            }
        }
        boolean[][] frozenCells = new boolean[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        int cellsToFreeze = (int) ((Constants.BOARD_SIZE * Constants.BOARD_SIZE - difficulty.getCellsToFill()) * 0.2);
        List<Point> candidateFrozenCells = new ArrayList<>();
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {

                if (puzzleBoard.isEmpty(row, col) &&
                        hasAdjacentEmptyFillableCellInSubgrid4Dir(puzzleBoard, fixedCells, row, col)) { // ONLY this condition remains
                    candidateFrozenCells.add(new Point(row, col));
                }
            }
        }
        Collections.shuffle(candidateFrozenCells, random);
        for (int i = 0; i < Math.min(cellsToFreeze, candidateFrozenCells.size()); i++) {
            Point p = candidateFrozenCells.get(i);
            frozenCells[p.x][p.y] = true;
        }
        return new Puzzle(puzzleBoard, fixedCells, frozenCells);
    }

    private boolean hasAdjacentEmptyFillableCellInSubgrid4Dir(Board board, boolean[][] fixedCells, int row, int col) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Top, bottom, left, right
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (newRow >= 0 && newRow < Constants.BOARD_SIZE &&
                    newCol >= 0 && newCol < Constants.BOARD_SIZE) {
                if (board.isEmpty(newRow, newCol) && !fixedCells[newRow][newCol]) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Solution getSolution() {
        return solution;
    }

}