package controller;

import model.Puzzle;
import model.Solution;
import utils.DifficultyLevel;
import view.GameScreen;
import view.GameScreenListener;
import generator.PuzzleGenerator;
import mode.GameMode;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class GameController {
    private final GameScreen view;
    private final PuzzleGenerator puzzleGenerator;
    private final GameMode gameMode;
    private final DifficultyLevel difficulty;
    private Puzzle puzzle;
    private Solution solution;
    private LocalDateTime startTime;
    private int score;
    private javax.swing.Timer gameTimer;

    public GameController(GameScreen view, PuzzleGenerator generator, GameMode mode, DifficultyLevel difficulty) {
        this.view = view;
        this.puzzleGenerator = generator;
        this.gameMode = mode;
        this.difficulty = difficulty;
        setupListeners();
    }

    private void setupListeners() {
        view.setListener(new GameScreenListener() {
            @Override
            public void onCellInput(int row, int col, String value) {
                handleCellInput(row, col, value);
            }

            @Override
            public void onNewGame(String mode, String difficulty) {
                startNewGame();
            }

            @Override
            public void onSolve() {
                solvePuzzle();
            }

            @Override
            public void onHint() {
                provideHint();
            }

            @Override
            public void onCheck() {
                checkPuzzle();
            }

            @Override
            public void onTimerTick() {
                updateTimer();
            }
        });
    }

    public void startNewGame() {
        puzzle = puzzleGenerator.generatePuzzle(difficulty);
        solution = puzzleGenerator.getSolution();
        view.updateBoard(puzzle, solution, gameMode);
        startTime = LocalDateTime.now();
        score = 0;
        view.updateScore(score);
        view.updateStatus("Playing...");
        if (gameTimer != null) gameTimer.stop();
        gameTimer = new Timer(1000, _ -> updateTimer()); // Updated to call updateTimer directly
        gameTimer.start();
    }

    private void handleCellInput(int row, int col, String value) {
        if (value.isEmpty()) {
            puzzle.getBoard().setCell(row, col, 0);
            view.updateCell(row, col, "", Color.BLACK, Color.WHITE, true);
            return;
        }

        int num = Integer.parseInt(value);
        if (gameMode.isValidMove(puzzle, row, col, num)) {
            puzzle.getBoard().setCell(row, col, num);
            score += 10;
            view.updateCell(row, col, value, Color.BLUE, Color.WHITE, true);
            if (gameMode.isPuzzleComplete(puzzle)) {
                view.stopTimer();
                view.showVictoryMessage("Congratulations! You've completed the puzzle!");
                view.updateStatus("Victory!");
            }
        } else {
            score -= 5;
            view.updateCell(row, col, value, Color.RED, Color.WHITE, true);
        }
        view.updateScore(score);
    }

    private void solvePuzzle() {
        if (!view.confirmSolve()) return;
        view.stopTimer();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (!puzzle.isFixed(row, col)) {
                    int value = solution.getCell(row, col);
                    puzzle.getBoard().setCell(row, col, value);
                    view.updateCell(row, col, String.valueOf(value), Color.BLACK, Color.LIGHT_GRAY, false);
                }
            }
        }
        view.updateStatus("Puzzle Solved!");
    }

    private void provideHint() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (!puzzle.isFixed(row, col) && puzzle.getBoard().getCell(row, col) == 0) {
                    int value = gameMode.getHint(puzzle, row, col, solution); // Updated call
                    if (value != 0) {
                        puzzle.getBoard().setCell(row, col, value);
                        view.updateCell(row, col, String.valueOf(value), Color.GREEN, Color.WHITE, true);
                        score -= 2;
                        view.updateScore(score);
                        return;
                    }
                }
            }
        }
    }

    private void checkPuzzle() {
        boolean isValid = gameMode.isSolutionCorrect(puzzle, solution);
        view.updateStatus(isValid ? "Correct so far!" : "There are mistakes!");
    }

    private void updateTimer() {
        Duration duration = Duration.between(startTime, LocalDateTime.now());
        long minutes = duration.toMinutes();
        long seconds = duration.getSeconds() % 60;
        view.updateTime(String.format("%02d:%02d", minutes, seconds));
    }

    public Puzzle getCurrentPuzzle() {
        return puzzle;
    }

    public Solution getCurrentSolution() {
        return solution;
    }
}