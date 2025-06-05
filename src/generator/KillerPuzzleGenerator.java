package generator;

import model.Puzzle;
import model.Solution;
import model.Board;
import model.Cage;
import utils.DifficultyLevel;
import utils.Point;

import java.util.*;

public class KillerPuzzleGenerator implements PuzzleGenerator {
    private static final int SIZE = 9;
    private final Random random = new Random();
    private Solution solution;
    private Puzzle puzzle;

    @Override
    public Puzzle generatePuzzle(DifficultyLevel difficulty) {
        puzzle = new Puzzle(new Board(new int[SIZE][SIZE])); // Initialize puzzle first
        solution = generateSolution();
        puzzle = new Puzzle(new Board(solution.getGrid())); // Reassign puzzle with the solution grid
        int cellsToRemove = switch (difficulty) {
            case EASY -> 40;
            case MEDIUM -> 45;
            case HARD -> 50;
            case EXPERT -> 55;
        };
        removeCells(cellsToRemove);
        if (difficulty == DifficultyLevel.EXPERT) {
            generateCages();
        }
        return puzzle;
    }

    @Override
    public Solution getSolution() {
        return solution;
    }

    private Solution generateSolution() {
        int[][] grid = new int[SIZE][SIZE];
        fillDiagonalBoxes(grid);
        fillRemaining(grid, 0, 3);
        return new Solution(grid);
    }

    private void fillDiagonalBoxes(int[][] grid) {
        for (int box = 0; box < SIZE; box += 3) {
            fillBox(grid, box, box);
        }
    }

    private void fillBox(int[][] grid, int row, int col) {
        List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Collections.shuffle(numbers);
        int idx = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                grid[row + i][col + j] = numbers.get(idx++);
                puzzle.setFixed(row + i, col + j, true);
            }
        }
    }

    private boolean fillRemaining(int[][] grid, int row, int col) {
        if (col >= SIZE && row < SIZE - 1) {
            row++;
            col = 0;
        }
        if (row >= SIZE && col >= SIZE) return true;
        if (row < 3 && col < 3) return fillRemaining(grid, row, col + 1);
        if (row < 3 && col < 6 && row >= 3) return fillRemaining(grid, row, col + 1);
        if (row >= 3 && col < 3 && col >= 3) return fillRemaining(grid, row, col + 1);
        if (row >= 3 && col >= 3 && row < 6 && col < 6) return fillRemaining(grid, row, col + 1);

        List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Collections.shuffle(numbers);
        for (int num : numbers) {
            if (isSafe(grid, row, col, num)) {
                grid[row][col] = num;
                puzzle.setFixed(row, col, true);
                if (fillRemaining(grid, row, col + 1)) return true;
                grid[row][col] = 0;
                puzzle.setFixed(row, col, false);
            }
        }
        return false;
    }

    private boolean isSafe(int[][] grid, int row, int col, int num) {
        for (int x = 0; x < SIZE; x++) {
            if (grid[row][x] == num || grid[x][col] == num) return false;
        }
        int startRow = row - row % 3, startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[i + startRow][j + startCol] == num) return false;
            }
        }
        return true;
    }

    private void removeCells(int count) {
        List<Point> cells = new ArrayList<>();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (puzzle.isFixed(row, col)) cells.add(new Point(row, col));
            }
        }
        Collections.shuffle(cells);
        for (int i = 0; i < count && !cells.isEmpty(); i++) {
            Point p = cells.remove(0);
            puzzle.getBoard().setCell(p.x(), p.y(), 0);
            puzzle.setFixed(p.x(), p.y(), false);
        }
    }

    private void generateCages() {
        List<Cage> cages = new ArrayList<>();
        boolean[][] used = new boolean[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (!used[row][col] && !puzzle.isFixed(row, col)) {
                    Cage cage = generateCage(row, col, used);
                    if (cage != null) cages.add(cage);
                }
            }
        }
        puzzle.setCages(cages);
    }

    private Cage generateCage(int startRow, int startCol, boolean[][] used) {
        List<Point> cells = new ArrayList<>();
        cells.add(new Point(startRow, startCol));
        used[startRow][startCol] = true;
        int size = random.nextInt(3) + 2; // Cage size between 2 and 4
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        while (cells.size() < size) {
            Point last = cells.get(cells.size() - 1);
            List<int[]> shuffledDirections = new ArrayList<>(Arrays.asList(directions));
            Collections.shuffle(shuffledDirections);
            boolean added = false;
            for (int[] dir : shuffledDirections) {
                int newRow = last.x() + dir[0], newCol = last.y() + dir[1];
                if (newRow >= 0 && newRow < SIZE && newCol >= 0 && newCol < SIZE &&
                        !used[newRow][newCol] && !puzzle.isFixed(newRow, newCol)) {
                    cells.add(new Point(newRow, newCol));
                    used[newRow][newCol] = true;
                    added = true;
                    break;
                }
            }
            if (!added) break;
        }

        if (cells.size() < 2) return null;

        int sum = 0;
        for (Point p : cells) {
            sum += solution.getCell(p.x(), p.y());
        }
        return new Cage(sum, cells);
    }
}