package model;

import utils.Point;

import java.util.List;

public class Cage {
    private final int sum;
    private final List<Point> cells;

    public Cage(int sum, List<Point> cells) {
        this.sum = sum;
        this.cells = List.copyOf(cells); // Ensure immutability
    }

    public int getSum() {
        return sum;
    }

    public List<Point> getCells() {
        return cells;
    }
}