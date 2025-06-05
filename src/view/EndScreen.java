package view;

import utils.GameModeType;

import javax.swing.*;

import mode.GameMode;
import model.Puzzle;
import model.Solution;

import java.awt.*;
import java.util.Objects;

public class EndScreen extends JFrame implements GameScreen {
    private JComboBox<GameModeType> modeBox;
    private JComboBox<String> difficultyBox;
    private EndScreenListener listener;

    public EndScreen(String result, int score, String time) {
        setTitle("Sudoku Game - Game Over");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initializeGUI(result, score, time);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initializeGUI(String result, int score, String time) {
        // Result Panel
        JPanel resultPanel = new JPanel();
        resultPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel resultLabel = new JLabel(result);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 24));
        resultPanel.add(resultLabel);
        add(resultPanel, BorderLayout.NORTH);

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        statsPanel.add(new JLabel("Score:"));
        statsPanel.add(new JLabel(String.valueOf(score)));
        statsPanel.add(new JLabel("Time:"));
        statsPanel.add(new JLabel(time));
        add(statsPanel, BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        controlPanel.add(new JLabel("Mode:"));
        modeBox = new JComboBox<>(GameModeType.values());
        controlPanel.add(modeBox);

        controlPanel.add(new JLabel("Difficulty:"));
        String[] difficulties = {"EASY", "MEDIUM", "HARD", "EXPERT"};
        difficultyBox = new JComboBox<>(difficulties);
        controlPanel.add(difficultyBox);

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(_ -> {
            if (listener != null) {
                listener.onNewGame(Objects.requireNonNull(modeBox.getSelectedItem()).toString(), (String) difficultyBox.getSelectedItem());
            }
        });
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(_ -> System.exit(0));
        controlPanel.add(newGameButton);
        controlPanel.add(exitButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    public void setEndScreenListener(EndScreenListener listener) {
        this.listener = listener;
    }

    @Override
    public void setListener(GameScreenListener listener) {

    }

    @Override
    public void updateBoard(Puzzle puzzle, Solution solution, GameMode mode) {

    }

    @Override
    public void updateCell(int row, int col, String value, Color foreground, Color background, boolean editable) {

    }

    @Override
    public void updateTime(String time) {

    }

    @Override
    public void updateScore(int score) {

    }

    @Override
    public void updateStatus(String status) {

    }

    @Override
    public void showVictoryMessage(String message) {

    }

    @Override
    public boolean confirmSolve() {
        return false;
    }

    @Override
    public void stopTimer() {

    }
}