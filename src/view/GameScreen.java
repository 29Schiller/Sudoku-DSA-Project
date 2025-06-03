package view;

import controller.GameController;
import model.Puzzle;
import model.Solution;
import utils.GameModeType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public abstract class GameScreen extends JFrame {
    protected static final int SIZE = 9;
    protected static final int SUBGRID = 3;
    protected JTextField[][] cells;
    protected JComboBox<String> difficultyBox;
    protected JComboBox<GameModeType> modeBox;
    protected JLabel timeLabel, scoreLabel, statusLabel;
    protected javax.swing.Timer uiTimer;
    protected GameScreenListener listener;

    public GameScreen(String title) {
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initializeGUI();
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    public void setListener(GameScreenListener listener) {
        this.listener = listener;
    }

    protected void initializeGUI() {
        createGameBoard();
        createControlPanel();
        createStatusPanel();
    }

    protected abstract void createGameBoard();

    private void createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout());

        JButton newGameBtn = new JButton("New Game");
        JButton solveBtn = new JButton("Solve");
        JButton hintBtn = new JButton("Hint");
        JButton checkBtn = new JButton("Check");

        String[] difficulties = {"EASY", "MEDIUM", "HARD", "EXPERT"};
        difficultyBox = new JComboBox<>(difficulties);
        modeBox = new JComboBox<>(GameModeType.values());

        newGameBtn.addActionListener(_ -> {
            if (listener != null) {
                listener.onNewGame(Objects.requireNonNull(modeBox.getSelectedItem()).toString(), (String) difficultyBox.getSelectedItem());
            }
        });
        solveBtn.addActionListener(_ -> {
            if (listener != null) {
                listener.onSolve();
            }
        });
        hintBtn.addActionListener(_ -> {
            if (listener != null) {
                listener.onHint();
            }
        });
        checkBtn.addActionListener(_ -> {
            if (listener != null) {
                listener.onCheck();
            }
        });

        controlPanel.add(new JLabel("Mode:"));
        controlPanel.add(modeBox);
        controlPanel.add(new JLabel("Difficulty:"));
        controlPanel.add(difficultyBox);
        controlPanel.add(newGameBtn);
        controlPanel.add(hintBtn);
        controlPanel.add(checkBtn);
        controlPanel.add(solveBtn);

        add(controlPanel, BorderLayout.NORTH);
    }

    private void createStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout());

        timeLabel = new JLabel("Time: 00:00");
        scoreLabel = new JLabel("Score: 0");
        statusLabel = new JLabel("Ready to play!");

        statusPanel.add(timeLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(scoreLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(statusLabel);

        add(statusPanel, BorderLayout.SOUTH);

        uiTimer = new javax.swing.Timer(1000, _ -> {
            if (listener != null) {
                listener.onTimerTick();
            }
        });
    }

    public abstract void updateBoard(Puzzle puzzle, Solution solution, mode.GameMode mode);

    public abstract void updateCell(int row, int col, String value, Color foreground, Color background, boolean editable);

    public void updateTime(String time) {
        timeLabel.setText("Time: " + time);
    }

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateStatus(String status) {
        statusLabel.setText(status);
    }

    public void showVictoryMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Victory!", JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean confirmSolve() {
        return JOptionPane.showConfirmDialog(this,
                "This will solve the entire puzzle. Continue?",
                "Solve Puzzle", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public void stopTimer() {
        uiTimer.stop();
    }
}