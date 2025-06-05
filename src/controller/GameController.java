package controller;

import model.Puzzle;
import model.Solution;
import utils.DifficultyLevel;
import view.*;
import generator.PuzzleGenerator;
import generator.ClassicPuzzleGenerator;
import generator.IcePuzzleGenerator;
import generator.KillerPuzzleGenerator;
import mode.GameMode;
import mode.GameModeFactory;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class GameController {
    private WelcomeScreen welcomeScreen;
    private GameScreen currentGameScreen;
    private EndScreen endScreen;
    private GameMode gameMode;
    private String currentMode; // New field to store the current game mode as a string
    private DifficultyLevel difficulty;
    private Puzzle puzzle;
    private Solution solution;
    private LocalDateTime startTime;
    private int score;
    private javax.swing.Timer gameTimer;
    private String currentTime;

    public GameController() {
        showWelcomeScreen();
    }

    private PuzzleGenerator getPuzzleGenerator(String mode) {
        return switch (mode) {
            case "CLASSIC" -> new ClassicPuzzleGenerator();
            case "ICE" -> new IcePuzzleGenerator();
            case "KILLER" -> new KillerPuzzleGenerator();
            default -> throw new IllegalArgumentException("Unknown game mode: " + mode);
        };
    }

    private void showWelcomeScreen() {
        if (currentGameScreen != null) currentGameScreen.dispose();
        if (endScreen != null) endScreen.dispose();
        welcomeScreen = new WelcomeScreen();
        welcomeScreen.setWelcomeListener(new WelcomeScreenListener() {
            @Override
            public void onStartGame(String mode, String difficulty) {
                startNewGame(mode, DifficultyLevel.valueOf(difficulty));
            }
        });
        welcomeScreen.setVisible(true);
    }

    private void showGameScreen(String mode, DifficultyLevel difficulty) {
        welcomeScreen.dispose();
        if (endScreen != null) endScreen.dispose();

        this.currentMode = mode; // Store the current mode
        gameMode = GameModeFactory.createGameMode(mode);
        this.difficulty = difficulty;

        switch (mode) {
            case "CLASSIC" -> currentGameScreen = new ClassicScreen();
            case "ICE" -> {
                currentGameScreen = new IceScreen();
                ((IceScreen) currentGameScreen).setGameController(this);
            }
            case "KILLER" -> {
                currentGameScreen = new KillerScreen();
                ((KillerScreen) currentGameScreen).setGameController(this);
            }
            default -> throw new IllegalArgumentException("Unknown game mode: " + mode);
        }

        setupGameScreenListeners();
        startNewGame();
        currentGameScreen.setVisible(true);
    }

    private void showEndScreen(String result) {
        currentGameScreen.stopTimer();
        currentGameScreen.dispose();
        endScreen = new EndScreen(result, score, currentTime);
        endScreen.setEndScreenListener(new EndScreenListener() {
            @Override
            public void onNewGame(String mode, String difficulty) {
                startNewGame(mode, DifficultyLevel.valueOf(difficulty));
            }
        });
        endScreen.setVisible(true);
    }

    private void setupGameScreenListeners() {
        currentGameScreen.setListener(new GameScreenListener() {
            @Override
            public void onCellInput(int row, int col, String value) {
                handleCellInput(row, col, value);
            }

            @Override
            public void onNewGame(String mode, String difficulty) {
                startNewGame(mode, DifficultyLevel.valueOf(difficulty));
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

    private void startNewGame(String mode, DifficultyLevel difficulty) {
        showGameScreen(mode, difficulty);
    }

    private void startNewGame() {
        PuzzleGenerator generator = getPuzzleGenerator(currentMode); // Use currentMode to get the generator
        puzzle = generator.generatePuzzle(difficulty);
        solution = generator.getSolution();
        currentGameScreen.updateBoard(puzzle, solution, gameMode);
        startTime = LocalDateTime.now();
        score = 100; // Start with 100 points
        currentGameScreen.updateScore(score);
        currentGameScreen.updateStatus("Playing...");
        if (gameTimer != null) gameTimer.stop();
        gameTimer = new Timer(1000, e -> updateTimer());
        gameTimer.start();
    }

    private void handleCellInput(int row, int col, String value) {
        if (value.isEmpty()) {
            puzzle.getBoard().setCell(row, col, 0);
            currentGameScreen.updateCell(row, col, "", Color.BLACK, Color.WHITE, true);
            return;
        }

        int num = Integer.parseInt(value);
        if (gameMode.isValidMove(puzzle, row, col, num)) {
            puzzle.getBoard().setCell(row, col, num);
            score += 10;
            currentGameScreen.updateCell(row, col, value, Color.BLUE, Color.WHITE, true);
            if (gameMode.isPuzzleComplete(puzzle)) {
                showEndScreen("Congratulations! You've completed the puzzle!");
            }
        } else {
            score -= 5;
            currentGameScreen.updateCell(row, col, value, Color.RED, Color.WHITE, true);
        }
        currentGameScreen.updateScore(score);
        if (score <= 0) {
            showEndScreen("Game Over! You've run out of points.");
        }
    }

    private void solvePuzzle() {
        if (!currentGameScreen.confirmSolve()) return;
        currentGameScreen.stopTimer();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (!puzzle.isFixed(row, col)) {
                    int value = solution.getCell(row, col);
                    puzzle.getBoard().setCell(row, col, value);
                    currentGameScreen.updateCell(row, col, String.valueOf(value), Color.BLACK, Color.LIGHT_GRAY, false);
                }
            }
        }
        currentGameScreen.updateStatus("Puzzle Solved!");
        showEndScreen("Puzzle Solved!");
    }

    private void provideHint() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (!puzzle.isFixed(row, col) && puzzle.getBoard().getCell(row, col) == 0) {
                    int value = gameMode.getHint(puzzle, row, col, solution);
                    if (value != 0) {
                        puzzle.getBoard().setCell(row, col, value);
                        currentGameScreen.updateCell(row, col, String.valueOf(value), Color.GREEN, Color.WHITE, true);
                        score -= 2;
                        currentGameScreen.updateScore(score);
                        if (score <= 0) {
                            showEndScreen("Game Over! You've run out of points.");
                        }
                        return;
                    }
                }
            }
        }
    }

    private void checkPuzzle() {
        boolean isValid = gameMode.isSolutionCorrect(puzzle, solution);
        currentGameScreen.updateStatus(isValid ? "Correct so far!" : "There are mistakes!");
    }

    private void updateTimer() {
        Duration duration = Duration.between(startTime, LocalDateTime.now());
        long minutes = duration.toMinutes();
        long seconds = duration.getSeconds() % 60;
        currentTime = String.format("%02d:%02d", minutes, seconds);
        currentGameScreen.updateTime(currentTime);
    }

    public Puzzle getCurrentPuzzle() {
        return puzzle;
    }

    public Solution getCurrentSolution() {
        return solution;
    }
}