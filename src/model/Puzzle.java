package model;

public class Puzzle {
    private final Board board;
    private final boolean[][] fixedCells;
    private final boolean[][] frozenCells;

    public Puzzle(Board board, boolean[][] fixedCells, boolean[][] frozenCells) {
        this.board = board;
        this.fixedCells = fixedCells;
        this.frozenCells = frozenCells;
    }

    public Board getBoard() {
        return board;
    }

    public boolean isFixed(int row, int col) {
        return fixedCells[row][col];
    }

    public void setFixed(int row, int col, boolean fixed) {
        fixedCells[row][col] = fixed;
    }

    public boolean isFrozen(int row, int col) {
        return frozenCells != null && frozenCells[row][col];
    }

    public void setFrozen(int row, int col, boolean frozen) {
        if (frozenCells != null) {
            frozenCells[row][col] = frozen;
        }
    }
}