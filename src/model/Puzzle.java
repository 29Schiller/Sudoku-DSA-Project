package model;

import java.util.ArrayList;
import java.util.List;

public class Puzzle {
    private final Board board;
    private final boolean[][] fixedCells;
    private final boolean[][] frozenCells;
    private List<Cage> cages;

    public Puzzle(Board board) {
        this.board = board;
        this.fixedCells = new boolean[9][9];
        this.frozenCells = new boolean[9][9];
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
        return frozenCells[row][col];
    }

    public void setFrozen(int row, int col, boolean frozen) {
        frozenCells[row][col] = frozen;
    }

    public void setCages(List<Cage> cages) {
        this.cages = cages;
    }

    public List<Cage> getCages() {
        return cages != null ? new ArrayList<>(cages) : null;
    }
}