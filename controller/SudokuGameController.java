package controller;

import generator.ClassicPuzzleGenerator;
import generator.IcePuzzleGenerator;
import generator.PuzzleGenerator;
import mode.ClassicMode;
import mode.IceMode;
import model.Puzzle;
import model.Solution;
import solver.SudokuSolver;
import utils.Constants;
import utils.DifficultyLevel;
import validator.SudokuValidator;
import view.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SudokuGameController implements GameStartListener, GameActionListener, TimerListener {
    private final Map<String, PuzzleGenerator> puzzleGenerators;
    private final Map<String, mode.GameMode> gameModes;
    private final SudokuValidator validator;
    private final SudokuSolver solver;
    private final GameView gameView;
    private final WelcomeScreen welcomeScreen;
    private Puzzle puzzle;
    private Solution solution;
    private int score;
    private int timeElapsed;
    private int hintCount;

    public SudokuGameController(SudokuValidator validator, SudokuSolver solver, GameView gameView, WelcomeScreen welcomeScreen) {
        this.validator = validator;
        this.solver = solver;
        this.gameView = gameView;
        this.welcomeScreen = welcomeScreen;
        puzzleGenerators = new HashMap<>();
        puzzleGenerators.put(Constants.CLASSIC_MODE, new ClassicPuzzleGenerator(validator));
        puzzleGenerators.put(Constants.ICE_MODE, new IcePuzzleGenerator(validator));
        gameModes = new HashMap<>();
        gameModes.put(Constants.CLASSIC_MODE, new ClassicMode());
        gameModes.put(Constants.ICE_MODE, new IceMode());
        initializeListeners();
    }

    public void start() {
        try {
            welcomeScreen.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to start game: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeListeners() {
        gameView.setStartListener(this);
        gameView.setActionListener(this);
        gameView.setTimerListener(this);
        welcomeScreen.setListener(this);
    }

    @Override
    public void onStartGame(String mode, String difficulty) {
        try {
            welcomeScreen.dispose();
            startNewGame(mode, difficulty);
            gameView.setVisible(true);
            gameView.requestFocus();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to start game: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onNewGame(String mode, String difficulty) {
        startNewGame(mode, difficulty); // Update board without disposing GameView
    }

    @Override
    public void onCellInput(int row, int col, String value) {
        if (puzzle.isFixed(row, col)) {
            return;
        }
        // ADDED: Prevent direct input into frozen cells and provide feedback.
        if (puzzle.isFrozen(row, col)) { //
            gameView.showStatus("This cell is frozen and cannot be changed directly."); //
            return; //
        }

        if (value.isEmpty()) {
            puzzle.getBoard().setCell(row, col, 0); //
            gameView.updateCell(row, col, "", Color.BLACK, getDefaultBackground(row, col), true); //
            if (gameView.getMode().equals(Constants.ICE_MODE)) { //
                checkAdjacentFrozenCells(row, col); //
            }
            return;
        }
        int num = Integer.parseInt(value); //
        mode.GameMode gameMode = gameModes.get(gameView.getMode()); //
        puzzle.getBoard().setCell(row, col, num); //

        if (gameMode.isValidMove(puzzle, solution, validator, row, col, num)) { //
            Color background = getDefaultBackground(row, col); //
            Color textColor = Color.BLACK; // Default to black //

            if (solution.getBoard().getCell(row, col) != num) { //
                textColor = Color.RED; //
                background = Constants.ERROR_CELL_COLOR; // Or a specific error color if defined //
                score = Math.max(0, score - 10); //
                gameView.showStatus("Correct number for game rules, but not the solution! Check your moves."); //
            } else {
                gameView.showStatus("Valid move!"); //
            }

            gameView.updateCell(row, col, String.valueOf(num), textColor, background, true); //

            if (gameView.getMode().equals(Constants.ICE_MODE)) { //
                ((IceMode) gameModes.get(Constants.ICE_MODE)).updateFrozenCellState(puzzle, solution, row, col, gameView.cells[row][col]); //
                checkAdjacentFrozenCells(row, col); //
            }

            if (solution.getBoard().getCell(row, col) == num && gameMode.isPuzzleComplete(puzzle, validator)) { //
                gameView.stopTimer(); //
                int bonus = Math.max(0, 1000 - timeElapsed - hintCount * 50); //
                score += bonus; //
                gameView.updateScore(score); //
                gameView.showStatus("Congratulations! Puzzle solved!"); //
                gameView.showVictoryMessage("Congratulations!\nTime: " + formatTime(timeElapsed) + //
                        "\nScore: " + score); //
                gameView.dispose(); //
                EndScreen endScreen = new EndScreen(score, timeElapsed); //
                endScreen.setListener(this); //
                endScreen.setVisible(true); //
            }
        } else {
            score = Math.max(0, score - 10); //
            gameView.updateScore(score); //
            gameView.showStatus("Invalid move! Try again."); //
            gameView.updateCell(row, col, String.valueOf(num), Color.RED, Constants.ERROR_CELL_COLOR, true); //

            puzzle.getBoard().setCell(row, col, 0); //
        }
    }

    private void checkAdjacentFrozenCells(int row, int col) {
        if (!gameView.getMode().equals(Constants.ICE_MODE)) return; //

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        IceMode iceMode = (IceMode) gameModes.get(Constants.ICE_MODE); //

        for (int[] dir : directions) { //
            int newRow = row + dir[0]; //
            int newCol = col + dir[1]; //
            if (newRow >= 0 && newRow < Constants.BOARD_SIZE && //
                    newCol >= 0 && newCol < Constants.BOARD_SIZE) { //
                if (puzzle.isFrozen(newRow, newCol)) { //
                    iceMode.updateFrozenCellState(puzzle, solution, newRow, newCol, gameView.cells[newRow][newCol]); //
                }
            }
        }
    }

    @Override
    public void onSolve() {
        if (gameView.confirmSolve()) { //
            gameView.stopTimer(); //
            int[][] solvedGrid = solver.solve(puzzle.getBoard().getGrid(), gameView.getMode().equals(Constants.ICE_MODE)); //
            for (int row = 0; row < Constants.BOARD_SIZE; row++) { //
                for (int col = 0; col < Constants.BOARD_SIZE; col++) { //
                    if (!puzzle.isFixed(row, col)) { //
                        gameView.updateCell(row, col, String.valueOf(solvedGrid[row][col]), Constants.SOLVE_CELL_COLOR, //
                                getDefaultBackground(row, col), false); //
                    }
                }
            }
            gameView.showStatus("Puzzle solved automatically!"); //
        }
    }

    @Override
    public void onHint() {
        HintSystem.HintGenerator hintGenerator = new HintSystem.HintGenerator(); //
        HintSystem.Hint hint = hintGenerator.getBestHint(puzzle.getBoard().getGrid(), solution.getBoard().getGrid()); //
        if (hint == null) { //
            gameView.showStatus("No hints available!"); //
            return; //
        }
        HintDialog dialog = new HintDialog(gameView, hint, this); //
        dialog.setVisible(true); //
    }

    @Override
    public void onCheck() {
        mode.GameMode gameMode = gameModes.get(gameView.getMode()); //
        int errors = gameMode.checkSolution(puzzle, solution); //
        if (errors == 0) { //
            gameView.showStatus("No errors found! Keep going!"); //
        } else {
            score = Math.max(0, score - 5 * errors); //
            gameView.updateScore(score); //
            gameView.showStatus("Found " + errors + " error(s)! Check your moves."); //
        }
    }

    @Override
    public void onTimerTick() {
        timeElapsed++; //
        gameView.updateTime(formatTime(timeElapsed)); //
    }

    private void startNewGame(String mode, String difficulty) {
        try {
            PuzzleGenerator generator = puzzleGenerators.get(mode); //
            mode.GameMode gameMode = gameModes.get(mode); //
            if (generator == null || gameMode == null) { //
                throw new IllegalArgumentException("Invalid mode: " + mode); //
            }
            System.out.println("Initializing game Mode: " + mode + " - Difficulty: " + difficulty); //
            this.puzzle = generator.generatePuzzle(DifficultyLevel.valueOf(difficulty)); //
            this.solution = generator.getSolution(); //
            this.score = 1000; //
            this.timeElapsed = 0; //
            this.hintCount = 0; //
            gameView.updateBoard(puzzle, solution, gameMode); //
            gameView.updateScore(score); //
            gameView.updateTime(formatTime(timeElapsed)); //
            gameView.showStatus("Game started! Good luck!"); //
            gameView.setTitle("Sudoku Game - " + mode + " (" + difficulty + ")"); //
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(gameView, "Failed to initialize game: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); //
        }
    }

    private Color getDefaultBackground(int row, int col) {
        return ((row / Constants.SUBGRID_SIZE + col / Constants.SUBGRID_SIZE) % 2 == 0) //
                ? Constants.ALTERNATE_CELL_COLOR : Constants.DEFAULT_CELL_COLOR; //
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60; //
        int secs = seconds % 60; //
        return String.format("%02d:%02d", minutes, secs); //
    }

    public void highlightCell(int row, int col, boolean highlight) {
        gameView.highlightCell(row, col, highlight); //
    }

    public void applyCellValue(int row, int col, int value) {
        puzzle.getBoard().setCell(row, col, value); //
        puzzle.setFixed(row, col, true); //
        gameView.applyCellValue(row, col, value); //
    }

    public void decreaseScore(int points) {
        score = Math.max(0, score - points); //
        gameView.updateScore(score); //
    }

    public void incrementHintCount() {
        hintCount++; //
    }

    public void showStatus(String status) {
        gameView.showStatus(status); //
    }
}