package view;

import java.util.*;

public class HintSystem {

    public enum HintType {
        NAKED_SINGLE("Naked Single", "Only one number can go in this cell"),
        HIDDEN_SINGLE("Hidden Single", "This number can only go in one place in this row/column/box"),
        DIRECT_HINT("Direct Answer", "Here's the answer for this cell");

        private final String name;
        private final String description;

        HintType(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
    }

    public static class Hint {
        private final HintType type;
        private final int row, col, value;
        private final String explanation;
        private final String detailedSteps;
        private final int costPoints;

        public Hint(HintType type, int row, int col, int value, String explanation,
                    String detailedSteps, int costPoints) {
            this.type = type;
            this.row = row;
            this.col = col;
            this.value = value;
            this.explanation = explanation;
            this.detailedSteps = detailedSteps;
            this.costPoints = costPoints;
        }

        public HintType getType() { return type; }
        public int getRow() { return row; }
        public int getCol() { return col; }
        public int getValue() { return value; }
        public String getExplanation() { return explanation; }
        public String getDetailedSteps() { return detailedSteps; }
        public int getCostPoints() { return costPoints; }
    }

    public static class HintGenerator {

        public Hint getBestHint(int[][] puzzle, int[][] solution) {
            // Try logical techniques first
            Hint hint = findNakedSingle(puzzle);
            if (hint != null) return hint;

            hint = findHiddenSingle(puzzle);
            if (hint != null) return hint;

            // Fall back to direct answer
            return getDirectHint(puzzle, solution);
        }

        private Hint findNakedSingle(int[][] puzzle) {
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    if (puzzle[row][col] == 0) {
                        List<Integer> candidates = getCandidates(puzzle, row, col);
                        if (candidates.size() == 1) {
                            int value = candidates.get(0);

                            String explanation = String.format(
                                    "Cell R%dC%d can only be %d", row + 1, col + 1, value);

                            String detailedSteps = String.format(
                                    "Looking at cell R%dC%d:\n" +
                                            "• Row %d already has: %s\n" +
                                            "• Column %d already has: %s\n" +
                                            "• 3×3 box already has: %s\n" +
                                            "• Only %d is possible in this cell!\n\n" +
                                            "This is called a 'Naked Single' - the most basic Sudoku technique.",
                                    row + 1, col + 1,
                                    row + 1, getRowNumbers(puzzle, row),
                                    col + 1, getColumnNumbers(puzzle, col),
                                    getBoxNumbers(puzzle, row, col),
                                    value
                            );

                            return new Hint(HintType.NAKED_SINGLE, row, col, value,
                                    explanation, detailedSteps, 25);
                        }
                    }
                }
            }
            return null;
        }

        private Hint findHiddenSingle(int[][] puzzle) {
            // Check rows for hidden singles
            for (int row = 0; row < 9; row++) {
                for (int num = 1; num <= 9; num++) {
                    if (!isNumberInRow(puzzle, row, num)) {
                        List<Integer> possibleCols = new ArrayList<>();
                        for (int col = 0; col < 9; col++) {
                            if (puzzle[row][col] == 0 && canPlace(puzzle, row, col, num)) {
                                possibleCols.add(col);
                            }
                        }

                        if (possibleCols.size() == 1) {
                            int col = possibleCols.get(0);

                            String explanation = String.format(
                                    "Number %d can only go in R%dC%d in this row", num, row + 1, col + 1);

                            String detailedSteps = String.format(
                                    "Looking for number %d in row %d:\n" +
                                            "• Checking each empty cell in the row\n" +
                                            "• Cell R%dC%d is the ONLY place where %d can go\n" +
                                            "• All other empty cells are blocked by column or box constraints\n\n" +
                                            "This is called a 'Hidden Single' - %d is 'hidden' as the only possibility.",
                                    num, row + 1, row + 1, col + 1, num, num
                            );

                            return new Hint(HintType.HIDDEN_SINGLE, row, col, num,
                                    explanation, detailedSteps, 30);
                        }
                    }
                }
            }

            // Check columns for hidden singles
            for (int col = 0; col < 9; col++) {
                for (int num = 1; num <= 9; num++) {
                    if (!isNumberInColumn(puzzle, col, num)) {
                        List<Integer> possibleRows = new ArrayList<>();
                        for (int row = 0; row < 9; row++) {
                            if (puzzle[row][col] == 0 && canPlace(puzzle, row, col, num)) {
                                possibleRows.add(row);
                            }
                        }

                        if (possibleRows.size() == 1) {
                            int row = possibleRows.get(0);

                            String explanation = String.format(
                                    "Number %d can only go in R%dC%d in this column", num, row + 1, col + 1);

                            String detailedSteps = String.format(
                                    "Looking for number %d in column %d:\n" +
                                            "• Checking each empty cell in the column\n" +
                                            "• Cell R%dC%d is the ONLY place where %d can go\n" +
                                            "• All other empty cells are blocked by row or box constraints\n\n" +
                                            "This is a 'Hidden Single' in the column.",
                                    num, col + 1, row + 1, col + 1, num
                            );

                            return new Hint(HintType.HIDDEN_SINGLE, row, col, num,
                                    explanation, detailedSteps, 30);
                        }
                    }
                }
            }

            return null;
        }

        private Hint getDirectHint(int[][] puzzle, int[][] solution) {
            List<int[]> emptyCells = getEmptyCells(puzzle);
            if (emptyCells.isEmpty()) return null;

            Random random = new Random();
            int[] cell = emptyCells.get(random.nextInt(emptyCells.size()));
            int row = cell[0], col = cell[1];
            int value = solution[row][col];

            String explanation = String.format("The answer for R%dC%d is %d", row + 1, col + 1, value);
            String detailedSteps = "This is a direct answer.\n\n" +
                    "💡 Try to find logical moves first using techniques like:\n" +
                    "• Naked Singles (cells with only one possibility)\n" +
                    "• Hidden Singles (numbers with only one place to go)\n" +
                    "• Looking for obvious placements\n\n" +
                    "Direct answers should be your last resort!";

            return new Hint(HintType.DIRECT_HINT, row, col, value,
                    explanation, detailedSteps, 100);
        }

        // Helper methods
        private List<Integer> getCandidates(int[][] puzzle, int row, int col) {
            List<Integer> candidates = new ArrayList<>();
            for (int num = 1; num <= 9; num++) {
                if (canPlace(puzzle, row, col, num)) {
                    candidates.add(num);
                }
            }
            return candidates;
        }

        private boolean canPlace(int[][] puzzle, int row, int col, int num) {
            // Check row
            for (int c = 0; c < 9; c++) {
                if (puzzle[row][c] == num) return false;
            }

            // Check column
            for (int r = 0; r < 9; r++) {
                if (puzzle[r][col] == num) return false;
            }

            // Check 3x3 box
            int boxRow = (row / 3) * 3;
            int boxCol = (col / 3) * 3;
            for (int r = boxRow; r < boxRow + 3; r++) {
                for (int c = boxCol; c < boxCol + 3; c++) {
                    if (puzzle[r][c] == num) return false;
                }
            }

            return true;
        }

        private String getRowNumbers(int[][] puzzle, int row) {
            Set<Integer> numbers = new HashSet<>();
            for (int col = 0; col < 9; col++) {
                if (puzzle[row][col] != 0) {
                    numbers.add(puzzle[row][col]);
                }
            }
            return numbers.toString().replace("[", "").replace("]", "");
        }

        private String getColumnNumbers(int[][] puzzle, int col) {
            Set<Integer> numbers = new HashSet<>();
            for (int row = 0; row < 9; row++) {
                if (puzzle[row][col] != 0) {
                    numbers.add(puzzle[row][col]);
                }
            }
            return numbers.toString().replace("[", "").replace("]", "");
        }

        private String getBoxNumbers(int[][] puzzle, int row, int col) {
            Set<Integer> numbers = new HashSet<>();
            int boxRow = (row / 3) * 3;
            int boxCol = (col / 3) * 3;
            for (int r = boxRow; r < boxRow + 3; r++) {
                for (int c = boxCol; c < boxCol + 3; c++) {
                    if (puzzle[r][c] != 0) {
                        numbers.add(puzzle[r][c]);
                    }
                }
            }
            return numbers.toString().replace("[", "").replace("]", "");
        }

        private List<int[]> getEmptyCells(int[][] puzzle) {
            List<int[]> cells = new ArrayList<>();
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    if (puzzle[row][col] == 0) {
                        cells.add(new int[]{row, col});
                    }
                }
            }
            return cells;
        }

        private boolean isNumberInRow(int[][] puzzle, int row, int num) {
            for (int col = 0; col < 9; col++) {
                if (puzzle[row][col] == num) return true;
            }
            return false;
        }

        private boolean isNumberInColumn(int[][] puzzle, int col, int num) {
            for (int row = 0; row < 9; row++) {
                if (puzzle[row][col] == num) return true;
            }
            return false;
        }
    }
}