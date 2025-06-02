public class SudokuValidator {
    private static final int SIZE = 9;
    private static final int SUBGRID = 3;

    public static boolean isValid(int[][] grid, int row, int col, int num) {
        // Check row
        for (int x = 0; x < SIZE; x++) {
            if (grid[row][x] == num) {
                return false;
            }
        }

        // Check column
        for (int x = 0; x < SIZE; x++) {
            if (grid[x][col] == num) {
                return false;
            }
        }

        // Check sub-grid
        int startRow = row - row % SUBGRID;
        int startCol = col - col % SUBGRID;

        for (int i = 0; i < SUBGRID; i++) {
            for (int j = 0; j < SUBGRID; j++) {
                if (grid[i + startRow][j + startCol] == num) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean isValidSudoku(int[][] grid) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] != 0) {
                    int temp = grid[row][col];
                    grid[row][col] = 0;
                    if (!isValid(grid, row, col, temp)) {
                        grid[row][col] = temp;
                        return false;
                    }
                    grid[row][col] = temp;
                }
            }
        }
        return true;
    }
}