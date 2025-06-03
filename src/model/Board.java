package model;

public class Board {
    private static final int SIZE = 9;
    private final int[][] grid;

    public Board() {
        this.grid = new int[SIZE][SIZE];
    }

    public Board(int[][] grid) {
        this.grid = new int[SIZE][SIZE];
        if (grid != null && grid.length == SIZE) {
            for (int i = 0; i < grid.length; i++) {
                if (grid[i] != null && grid[i].length == SIZE) {
                    this.grid[i] = grid[i].clone();
                }
            }
        }
    }

    public int getCell(int row, int col) {
        return grid[row][col];
    }

    public void setCell(int row, int col, int value) {
        grid[row][col] = value;
    }

    public int[][] getGrid() {
        return grid;
    }

    public boolean isEmpty(int row, int col) {
        return grid[row][col] == 0;
    }
}