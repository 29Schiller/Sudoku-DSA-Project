package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mode.GameMode;
import model.Puzzle;
import model.Solution;
import utils.GameModeType;

public class ClassicScreen extends JFrame implements GameScreen {
    private static final int SIZE = 9;
    private static final int SUBGRID = 3;
    private JTextField[][] cells;
    private JComboBox<String> difficultyBox;
    private JComboBox<GameModeType> modeBox;
    private JLabel timeLabel, scoreLabel, statusLabel;
    private javax.swing.Timer uiTimer;
    private GameScreenListener listener;

    public ClassicScreen() {
        setTitle("Sudoku Game - Classic Mode");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initializeGUI();
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initializeGUI() {
        createGameBoard();
        createControlPanel();
        createStatusPanel();
    }

    private void createGameBoard() {
        JPanel boardPanel = new JPanel(new GridLayout(SIZE, SIZE, 0, 0));
        boardPanel.setBackground(Color.WHITE);
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        cells = new JTextField[SIZE][SIZE];

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = new JTextField();
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(new Font("Arial", Font.BOLD, 16));
                cells[row][col].setPreferredSize(new Dimension(40, 40));
                cells[row][col].setBackground(Color.WHITE);

                int top = (row % SUBGRID == 0) ? 4 : 1;
                int left = (col % SUBGRID == 0) ? 4 : 1;
                int bottom = (row % SUBGRID == SUBGRID - 1) ? 4 : 1;
                int right = (col % SUBGRID == SUBGRID - 1) ? 4 : 1;
                
                cells[row][col].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.GRAY));

                final int r = row, c = col;
                cells[row][col].addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char ch = e.getKeyChar();
                        if (!Character.isDigit(ch) || ch == '0' || !cells[r][c].getText().isEmpty()) {
                            e.consume();
                        }
                    }
                });

                cells[row][col].addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        if (listener != null) listener.onCellInput(r, c, cells[r][c].getText());
                    }
                });

                boardPanel.add(cells[row][col]);
            }
        }

        add(boardPanel, BorderLayout.CENTER);
    }

    private void createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout());

        JButton newGameBtn = new JButton("New Game");
        JButton solveBtn = new JButton("Solve");
        JButton hintBtn = new JButton("Hint");
        JButton checkBtn = new JButton("Check");

        String[] difficulties = {"EASY", "MEDIUM", "HARD", "EXPERT"};
        difficultyBox = new JComboBox<>(difficulties);
        modeBox = new JComboBox<>(GameModeType.values());

        newGameBtn.addActionListener(e -> {
            if (listener != null) {
                listener.onNewGame(modeBox.getSelectedItem().toString(), (String) difficultyBox.getSelectedItem());
            }
        });
        solveBtn.addActionListener(e -> {
            if (listener != null) {
                listener.onSolve();
            }
        });
        hintBtn.addActionListener(e -> {
            if (listener != null) {
                listener.onHint();
            }
        });
        checkBtn.addActionListener(e -> {
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

        uiTimer = new javax.swing.Timer(1000, e -> {
            if (listener != null) {
                listener.onTimerTick();
            }
        });
    }

    @Override
    public void setListener(GameScreenListener listener) {
        this.listener = listener;
    }

    @Override
    public void updateBoard(Puzzle puzzle, Solution solution, GameMode mode) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col].setText("");
                cells[row][col].setEditable(true);
                cells[row][col].setForeground(Color.BLACK);
                cells[row][col].setBackground(Color.WHITE);

                int top = (row % SUBGRID == 0) ? 4 : 1;
                int left = (col % SUBGRID == 0) ? 4 : 1;
                int bottom = (row % SUBGRID == SUBGRID - 1) ? 4 : 1;
                int right = (col % SUBGRID == SUBGRID - 1) ? 4 : 1;
                
                cells[row][col].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.GRAY));

                mode.renderCell(cells[row][col], row, col, puzzle, solution);
            }
        }
        uiTimer.start();
    }

    @Override
    public void updateCell(int row, int col, String value, Color foreground, Color background, boolean editable) {
        cells[row][col].setText(value);
        cells[row][col].setForeground(foreground);
        cells[row][col].setBackground(background);
        cells[row][col].setEditable(editable);

        int top = (row % SUBGRID == 0) ? 4 : 1;
        int left = (col % SUBGRID == 0) ? 4 : 1;
        int bottom = (row % SUBGRID == SUBGRID - 1) ? 4 : 1;
        int right = (col % SUBGRID == SUBGRID - 1) ? 4 : 1;

        cells[row][col].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.GRAY));
    }

    @Override
    public void updateTime(String time) {
        timeLabel.setText("Time: " + time);
    }

    @Override
    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    @Override
    public void updateStatus(String status) {
        statusLabel.setText(status);
    }

    @Override
    public void showVictoryMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Victory!", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public boolean confirmSolve() {
        return JOptionPane.showConfirmDialog(this,
                "This will solve the entire puzzle. Continue?",
                "Solve Puzzle", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    @Override
    public void stopTimer() {
        uiTimer.stop();
    }
}