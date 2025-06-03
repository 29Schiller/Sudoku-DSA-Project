package model;

public class Solution {
    private final Board board;

    public Solution(Board board) {
        this.board = new Board(board.getGrid());
    }

    public int getCell(int row, int col) {
        return board.getCell(row, col);
    }

    public Board getBoard() {
        return board;
    }
}