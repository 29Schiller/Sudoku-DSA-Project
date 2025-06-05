package view;

import model.Puzzle;
import model.Solution;
import utils.Constants;
import utils.GameModeType;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class GameView extends JFrame {
    public JTextField[][] cells;
    private JComboBox<String> difficultyBox;
    private JComboBox<GameModeType> modeBox;
    private JLabel timeLabel, scoreLabel, statusLabel;
    private javax.swing.Timer uiTimer;
    private GameStartListener startListener;
    private GameActionListener actionListener;
    private TimerListener timerListener;

    public GameView() {
        setTitle("Sudoku Game - Professional Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initializeGUI();
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    public void setStartListener(GameStartListener listener) {
        this.startListener = listener;
    }

    public void setActionListener(GameActionListener listener) {
        this.actionListener = listener;
    }

    public void setTimerListener(TimerListener listener) {
        this.timerListener = listener;
    }

    public String getMode() {
        return Objects.requireNonNull(modeBox.getSelectedItem()).toString();
    }

    private void initializeGUI() {
        createGameBoard();
        createControlPanel();
        createStatusPanel();
    }

    private void createGameBoard() {
        JPanel boardPanel = new JPanel();
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        boardPanel.setBackground(Constants.DEFAULT_CELL_COLOR);
        boardPanel.setPreferredSize(new Dimension(450, 450));
        boardPanel.setLayout(new GridLayout(Constants.BOARD_SIZE, Constants.BOARD_SIZE, 0, 0));

        cells = new JTextField[Constants.BOARD_SIZE][Constants.BOARD_SIZE];

        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                cells[row][col] = new JTextField();
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(new Font("Arial", Font.BOLD, 16));
                cells[row][col].setPreferredSize(new Dimension(40, 40));
                cells[row][col].setBackground(getDefaultBackground(row, col));

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
                        if (actionListener != null) {
                            actionListener.onCellInput(r, c, cells[r][c].getText());
                        }
                    }
                });

                cells[row][col].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (actionListener != null && !cells[r][c].isEditable()) {
                            actionListener.onCellInput(r, c, cells[r][c].getText());
                        }
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

        modeBox = UIUtils.createModeComboBox();
        difficultyBox = UIUtils.createDifficultyComboBox();

        newGameBtn.addActionListener(_ -> {
            if (startListener != null) {
                startListener.onNewGame(Objects.requireNonNull(modeBox.getSelectedItem()).toString(), (String) difficultyBox.getSelectedItem());
            }
        });
        solveBtn.addActionListener(_ -> {
            if (actionListener != null) {
                actionListener.onSolve();
            }
        });
        hintBtn.addActionListener(_ -> {
            if (actionListener != null) {
                actionListener.onHint();
            }
        });
        checkBtn.addActionListener(_ -> {
            if (actionListener != null) {
                actionListener.onCheck();
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
            if (timerListener != null) {
                timerListener.onTimerTick();
            }
        });
    }

    public void updateBoard(Puzzle puzzle, Solution solution, mode.GameMode mode) {
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                cells[row][col].setText("");
                cells[row][col].setEditable(true);
                cells[row][col].setForeground(Color.BLACK);
                cells[row][col].setBackground(getDefaultBackground(row, col));

                int top = (row % Constants.SUBGRID_SIZE == 0) ? 4 : 1;
                int left = (col % Constants.SUBGRID_SIZE == 0) ? 4 : 1;
                int bottom = (row % Constants.SUBGRID_SIZE == Constants.SUBGRID_SIZE - 1) ? 4 : 1;
                int right = (col % Constants.SUBGRID_SIZE == Constants.SUBGRID_SIZE - 1) ? 4 : 1;
                cells[row][col].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));

                mode.renderCell(cells[row][col], row, col, puzzle, solution);
            }
        }
        uiTimer.start();
    }

    public void updateCell(int row, int col, String value, Color foreground, Color background, boolean editable) {
        cells[row][col].setText(value);
        cells[row][col].setForeground(foreground);
        cells[row][col].setBackground(background);
        cells[row][col].setEditable(editable);
    }

    public void highlightCell(int row, int col, boolean highlight) {
        cells[row][col].setBackground(highlight ? Color.YELLOW : getDefaultBackground(row, col));
    }

    public void applyCellValue(int row, int col, int value) {
        cells[row][col].setText(String.valueOf(value));
        cells[row][col].setEditable(false);
        cells[row][col].setForeground(Constants.HINT_CELL_COLOR);
        cells[row][col].setBackground(getDefaultBackground(row, col));
    }

    private Color getDefaultBackground(int row, int col) {
        return ((row / Constants.SUBGRID_SIZE + col / Constants.SUBGRID_SIZE) % 2 == 0)
                ? Constants.ALTERNATE_CELL_COLOR : Constants.DEFAULT_CELL_COLOR;
    }

    public void updateTime(String time) {
        timeLabel.setText("Time: " + time);
    }

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void showStatus(String status) {
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