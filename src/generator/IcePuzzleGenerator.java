package generator;

import model.*;
import utils.DifficultyLevel;
import utils.Point;

import java.util.*;

public class IcePuzzleGenerator implements PuzzleGenerator {
    private static final int SIZE = 9;
    private final Random random = new Random();
    private Board solutionBoard;
    private Board puzzleBoard;

    @Override
    public Puzzle generatePuzzle(DifficultyLevel difficulty) {
        solutionBoard = new Board();
        puzzleBoard = new Board();
        fillBoard(solutionBoard);
        Set<Point> fixedCells = createPuzzle(difficulty);
        Set<Point> frozenCells = createFrozenCells(fixedCells);
        return new Puzzle(puzzleBoard, fixedCells, frozenCells, null);
    }

    @Override
    public Solution getSolution() {
        return new Solution(solutionBoard);
    }

    private boolean fillBoard(Board board) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board.getCell(row, col) == 0) {
                    List<Integer> numbers = new ArrayList<>();
                    for (int i = 1; i <= SIZE; i++) numbers.add(i);
                    Collections.shuffle(numbers, random);

                    for (int num : numbers) {
                        if (isSafe(board, row, col, num)) {
                            board.setCell(row, col, num);
                            if (fillBoard(board)) return true;
                            board.setCell(row, col, 0);
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isSafe(Board board, int row, int col, int num) {
        for (int x = 0; x < SIZE; x++) {
            if (board.getCell(row, x) == num || board.getCell(x, col) == num) return false;
        }

        int startRow = row - row % 3, startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.getCell(i + startRow, j + startCol) == num) return false;
            }
        }
        return true;
    }

    private Set<Point> createPuzzle(DifficultyLevel difficulty) {
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(solutionBoard.getGrid()[i], 0, puzzleBoard.getGrid()[i], 0, SIZE);
        }

        Set<Point> fixedCells = new HashSet<>();
        int cellsToFill = difficulty.getCellsToFill();
        List<Point> allCells = new ArrayList<>();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                allCells.add(new Point(row, col));
            }
        }
        Collections.shuffle(allCells, random);

        int filled = 0;
        for (Point p : allCells) {
            if (filled >= cellsToFill) break;
            fixedCells.add(p);
            filled++;
        }

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (!fixedCells.contains(new Point(row, col))) {
                    puzzleBoard.setCell(row, col, 0);
                }
            }
        }

        return fixedCells;
    }

    private Set<Point> createFrozenCells(Set<Point> fixedCells) {
        Set<Point> frozenCells = new HashSet<>();
        List<Point> nonFixedCells = new ArrayList<>();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Point p = new Point(row, col);
                if (!fixedCells.contains(p)) {
                    nonFixedCells.add(p);
                }
            }
        }
        Collections.shuffle(nonFixedCells, random);
        int cellsToFreeze = nonFixedCells.size() / 3; // Freeze 1/3 of non-fixed cells
        for (int i = 0; i < cellsToFreeze; i++) {
            frozenCells.add(nonFixedCells.get(i));
        }
        return frozenCells;
    }
}