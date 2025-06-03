package model;

import utils.Point;

import java.util.*;

public class Puzzle {
    private final Board board;
    private final Set<Point> fixedCells;
    private final Set<Point> frozenCells;
    private final List<Cage> cages;

    public Puzzle(Board board, Set<Point> fixedCells, Set<Point> frozenCells, List<Cage> cages) {
        this.board = new Board(board.getGrid());
        this.fixedCells = new HashSet<>(fixedCells);
        this.frozenCells = new HashSet<>(frozenCells);
        this.cages = (cages != null) ? new ArrayList<>(cages) : null;
    }

    public Board getBoard() {
        return board;
    }

    public boolean isFixed(int row, int col) {
        return fixedCells.contains(new Point(row, col));
    }

    public boolean isFrozen(int row, int col) {
        return frozenCells.contains(new Point(row, col));
    }

    public void setFrozen(int row, int col, boolean frozen) {
        Point point = new Point(row, col);
        if (frozen) {
            frozenCells.add(point);
        } else {
            frozenCells.remove(point);
        }
    }

    public List<Cage> getCages() {
        return cages != null ? Collections.unmodifiableList(cages) : null;
    }
}