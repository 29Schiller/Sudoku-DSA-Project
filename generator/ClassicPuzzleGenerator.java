package generator;

import model.Board;
import model.Puzzle;
import model.Solution;
import utils.Constants;
import utils.DifficultyLevel;
import utils.Point;
import validator.SudokuValidator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ClassicPuzzleGenerator implements PuzzleGenerator {
    private final Random random = new Random();
    private final SudokuValidator validator;
    private Solution solution;

    public ClassicPuzzleGenerator(SudokuValidator validator) {
        this.validator = validator;
    }

    @Override
    public Puzzle generatePuzzle(DifficultyLevel difficulty) {
        Board solutionBoard = generateCompleteSudoku();
        this.solution = new Solution(solutionBoard);
        Board puzzleBoard = createPuzzleFromSolution(solutionBoard, difficulty.getCellsToFill());
        boolean[][] fixedCells = new boolean[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                fixedCells[row][col] = puzzleBoard.getCell(row, col) != 0;
            }
        }
        return new Puzzle(puzzleBoard, fixedCells, null);
    }

    @Override
    public Solution getSolution() {
        return solution;
    }

    private Board generateCompleteSudoku() {
        Board board = new Board();
        solveSudoku(board);
        return board;
    }

    private boolean solveSudoku(Board board) {
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                if (board.isEmpty(row, col)) {
                    List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
                    Collections.shuffle(numbers, random);
                    for (int num : numbers) {
                        if (validator.isValid(board, row, col, num)) {
                            board.setCell(row, col, num);
                            if (solveSudoku(board)) {
                                return true;
                            }
                            board.setCell(row, col, 0);
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private Board createPuzzleFromSolution(Board solution, int cellsToFill) {
        Board puzzle = new Board(solution.getGrid());
        int cellsToRemove = Constants.BOARD_SIZE * Constants.BOARD_SIZE - cellsToFill;
        List<Point> positions = new ArrayList<>();
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                positions.add(new Point(row, col));
            }
        }
        Collections.shuffle(positions, random);

        for (int i = 0; i < cellsToRemove && i < positions.size(); i++) {
            Point pos = positions.get(i);
            int value = puzzle.getCell(pos.x, pos.y);
            puzzle.setCell(pos.x, pos.y, 0);
            if (!hasUniqueSolution(puzzle)) {
                puzzle.setCell(pos.x, pos.y, value); // Revert if multiple solutions
            }
        }
        return puzzle;
    }

    private boolean hasUniqueSolution(Board board) {
        int[][] grid = board.getGrid();
        int solutions = countSolutions(grid, 0, 0);
        return solutions == 1;
    }

    private int countSolutions(int[][] grid, int row, int col) {
        if (row >= Constants.BOARD_SIZE) {
            row = 0;
            col++;
            if (col >= Constants.BOARD_SIZE) {
                return 1;
            }
        }

        if (!isEmpty(grid, row, col)) {
            return countSolutions(grid, row + 1, col);
        }

        int count = 0;
        for (int num = 1; num <= 9; num++) {
            if (validator.isValid(new Board(grid), row, col, num)) {
                grid[row][col] = num;
                count += countSolutions(grid, row + 1, col);
                grid[row][col] = 0;
                if (count > 1) {
                    return count; // Early exit if multiple solutions found
                }
            }
        }
        return count;
    }

    private boolean isEmpty(int[][] grid, int row, int col) {
        return grid[row][col] == 0;
    }
}