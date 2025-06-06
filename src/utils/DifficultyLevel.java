package utils;

public enum DifficultyLevel {
    EASY(47),
    MEDIUM(35),
    HARD(25),
    EXPERT(17);

    private final int cellsToFill;

    DifficultyLevel(int cellsToFill) {
        this.cellsToFill = cellsToFill;
    }

    public int getCellsToFill() {
        return cellsToFill;
    }
}