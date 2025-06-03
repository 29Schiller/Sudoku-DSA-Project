import java.util.*;

/**
 * Generates Sudoku puzzles with different difficulty levels
 */
public class PuzzleGenerator {
    private static final int SIZE = 9;
    private Random random = new Random();

    public PuzzlePair generatePuzzle(DifficultyLevel difficulty) {
        int[][] solution = generateCompleteSudoku();
        int[][] puzzle = createPuzzleFromSolution(solution, difficulty.getCellsToFill());

        return new PuzzlePair(puzzle, solution);
    }

    private int[][] generateCompleteSudoku() {
        int[][] grid = new int[SIZE][SIZE];
        solveSudoku(grid);
        return grid;
    }

    private boolean solveSudoku(int[][] grid) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] == 0) {
                    List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
                    Collections.shuffle(numbers);

                    for (int num : numbers) {
                        if (SudokuValidator.isValid(grid, row, col, num)) {
                            grid[row][col] = num;

                            if (solveSudoku(grid)) {
                                return true;
                            }

                            grid[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private int[][] createPuzzleFromSolution(int[][] solution, int cellsToFill) {
        int[][] puzzle = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            puzzle[i] = solution[i].clone();
        }

        int cellsToRemove = SIZE * SIZE - cellsToFill;
        List<Point> positions = new ArrayList<>();

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                positions.add(new Point(row, col));
            }
        }

        Collections.shuffle(positions);

        for (int i = 0; i < cellsToRemove && i < positions.size(); i++) {
            Point pos = positions.get(i);
            puzzle[pos.x][pos.y] = 0;
        }

        return puzzle;
    }

    // Simple Point class
    public static class Point {
        public int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}