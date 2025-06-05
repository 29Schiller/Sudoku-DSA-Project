package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mode.GameMode;
import model.Puzzle;
import model.Solution;
import utils.GameModeType;

public class WelcomeScreen extends JFrame implements GameScreen {
    private JComboBox<GameModeType> modeBox;
    private JComboBox<String> difficultyBox;
    private WelcomeScreenListener listener;

    public WelcomeScreen() {
        setTitle("Sudoku Game - Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initializeGUI();
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initializeGUI() {
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("Welcome to Sudoku!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

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

        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(_ -> {
            if (listener != null) {
                listener.onStartGame(modeBox.getSelectedItem().toString(), (String) difficultyBox.getSelectedItem());
            }
        });
        controlPanel.add(new JLabel()); // Spacer
        controlPanel.add(startButton);

        add(controlPanel, BorderLayout.CENTER);
    }

    @Override
    public void setListener(GameScreenListener listener) {
        // Not used for WelcomeScreen, but required by interface
    }

    public void setWelcomeListener(WelcomeScreenListener listener) {
        this.listener = listener;
    }

    @Override
    public void updateBoard(Puzzle puzzle, Solution solution, GameMode mode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateCell(int row, int col, String value, Color foreground, Color background, boolean editable) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateTime(String time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateScore(int score) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateStatus(String status) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void showVictoryMessage(String message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean confirmSolve() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void stopTimer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}