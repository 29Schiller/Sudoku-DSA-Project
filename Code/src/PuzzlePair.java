public class PuzzlePair {
    private final int[][] puzzle;
    private final int[][] solution;

    public PuzzlePair(int[][] puzzle, int[][] solution) {
        this.puzzle = puzzle;
        this.solution = solution;
    }

    public int[][] getPuzzle() {
        return puzzle;
    }

    public int[][] getSolution() {
        return solution;
    }
}
