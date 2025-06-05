package model;

public class Solution {
    private final int[][] grid;

    public Solution(int[][] grid) {
        this.grid = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.grid[i][j] = grid[i][j];
            }
        }
    }

    public int getCell(int row, int col) {
        return grid[row][col];
    }

    public int[][] getGrid() {
        return grid;
    }
}