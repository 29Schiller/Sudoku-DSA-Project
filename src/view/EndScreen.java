package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import utils.GameModeType;

public class EndScreen extends JFrame {
    private JComboBox<GameModeType> modeBox;
    private JComboBox<String> difficultyBox;
    private GameStartListener listener;

    public EndScreen(int score, int timeElapsed) {
        setTitle("Sudoku Game - Game Over");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 300));
        initializeUI(score, timeElapsed);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    public void setListener(GameStartListener listener) {
        this.listener = listener;
    }

    private void initializeUI(int score, int timeElapsed) {
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("Game Over!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        statsPanel.add(new JLabel("Score: " + score));
        statsPanel.add(new JLabel("Time: " + formatTime(timeElapsed)));
        add(statsPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        modeBox = UIUtils.createModeComboBox();
        JPanel modePanel = new JPanel();
        modePanel.add(new JLabel("Select Mode:"));
        modePanel.add(modeBox);
        controlPanel.add(modePanel);

        difficultyBox = UIUtils.createDifficultyComboBox();
        JPanel difficultyPanel = new JPanel();
        difficultyPanel.add(new JLabel("Select Difficulty:"));
        difficultyPanel.add(difficultyBox);
        controlPanel.add(difficultyPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton startButton = new JButton("Start New Game");
        startButton.setFont(new Font("Arial", Font.PLAIN, 16));
        startButton.addActionListener(_ -> {
            if (listener != null) {
                String mode = Objects.requireNonNull(modeBox.getSelectedItem()).toString();
                String difficulty = Objects.requireNonNull(difficultyBox.getSelectedItem()).toString();
                listener.onStartGame(mode, difficulty);
            } else {
                JOptionPane.showMessageDialog(this, "Game controller not initialized.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(startButton);

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 16));
        exitButton.addActionListener(_ -> System.exit(0));
        buttonPanel.add(exitButton);

        controlPanel.add(buttonPanel);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }
}