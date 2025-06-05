package model;

import java.util.ArrayList;
import java.util.List;

import utils.Point;

public class Cage {
    private final int sum;
    private final List<Point> cells;

    public Cage(int sum, List<Point> cells) {
        this.sum = sum;
        this.cells = new ArrayList<>(cells);
    }

    public int getSum() {
        return sum;
    }

    public List<Point> getCells() {
        return new ArrayList<>(cells);
    }
}