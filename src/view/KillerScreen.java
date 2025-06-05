package view;

import controller.GameController;
import model.Puzzle;
import model.Solution;
import mode.GameMode;
import model.Cage;
import utils.GameModeType;
import utils.Point;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class KillerScreen extends JFrame implements GameScreen {
    private static final int SIZE = 9;
    private static final int SUBGRID = 3;
    private GameController gameController;
    private JTextField[][] cells;
    private JComboBox<String> difficultyBox;
    private JComboBox<GameModeType> modeBox;
    private JLabel timeLabel, scoreLabel, statusLabel;
    private javax.swing.Timer uiTimer;
    private GameScreenListener listener;

    public KillerScreen() {
        setTitle("Sudoku Game - Killer Mode");
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
        boardPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK)); // Black border, thickness 1 around board

        cells = new JTextField[SIZE][SIZE];

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = new JTextField();
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(new Font("Arial", Font.BOLD, 16));
                cells[row][col].setPreferredSize(new Dimension(40, 40));
                cells[row][col].setBackground(Color.WHITE);
                cells[row][col].setLayout(null); // For absolute positioning of sum labels

                // Subgrid borders: thickness 1, black; cell borders: thickness 0.5, light gray
                float top = (row % SUBGRID == 0) ? 1.0f : 0.5f;
                float left = (col % SUBGRID == 0) ? 1.0f : 0.5f;
                float bottom = (row % SUBGRID == SUBGRID - 1) ? 1.0f : 0.5f;
                float right = (col % SUBGRID == SUBGRID - 1) ? 1.0f : 0.5f;
                Color borderColor = (row % SUBGRID == 0 || col % SUBGRID == 0 ||
                        row % SUBGRID == SUBGRID - 1 || col % SUBGRID == SUBGRID - 1)
                        ? Color.BLACK : Color.LIGHT_GRAY;
                cells[row][col].setBorder(BorderFactory.createMatteBorder(
                        (int) top, (int) left, (int) bottom, (int) right, borderColor));

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

    public void setGameController(GameController controller) {
        this.gameController = controller;
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
                cells[row][col].removeAll(); // Clear any previous sum labels

                // Subgrid borders: thickness 1, black; cell borders: thickness 0.5, light gray
                float top = (row % SUBGRID == 0) ? 1.0f : 0.5f;
                float left = (col % SUBGRID == 0) ? 1.0f : 0.5f;
                float bottom = (row % SUBGRID == SUBGRID - 1) ? 1.0f : 0.5f;
                float right = (col % SUBGRID == SUBGRID - 1) ? 1.0f : 0.5f;
                Color borderColor = (row % SUBGRID == 0 || col % SUBGRID == 0 ||
                        row % SUBGRID == SUBGRID - 1 || col % SUBGRID == SUBGRID - 1)
                        ? Color.BLACK : Color.LIGHT_GRAY;
                Border matteBorder = BorderFactory.createMatteBorder(
                        (int) top, (int) left, (int) bottom, (int) right, borderColor);

                // Apply dashed border inside the matte border for cages
                mode.renderCell(cells[row][col], row, col, puzzle, solution);
                applyCageBorder(row, col, puzzle, matteBorder);
            }
        }
        uiTimer.start();
    }

    private void applyCageBorder(int row, int col, Puzzle puzzle, Border matteBorder) {
        if (puzzle.getCages() == null) {
            cells[row][col].setBorder(matteBorder);
            return;
        }

        for (Cage cage : puzzle.getCages()) {
            for (int i = 0; i < cage.getCells().size(); i++) {
                Point p = cage.getCells().get(i);
                if (p.x() == row && p.y() == col) {
                    // Find the bounding box of the cage
                    int minRow = Integer.MAX_VALUE, maxRow = Integer.MIN_VALUE;
                    int minCol = Integer.MAX_VALUE, maxCol = Integer.MIN_VALUE;
                    for (Point other : cage.getCells()) {
                        minRow = Math.min(minRow, other.x());
                        maxRow = Math.max(maxRow, other.x());
                        minCol = Math.min(minCol, other.y());
                        maxCol = Math.max(maxCol, other.y());
                    }

                    // Determine which sides of the cell need a dashed border (inside the cell border)
                    boolean topDashed = row == minRow;
                    boolean leftDashed = col == minCol;
                    boolean bottomDashed = row == maxRow;
                    boolean rightDashed = col == maxCol;

                    // Apply dashed border inside the matte border
                    float thickness = 1.0f;
                    float length = 0.5f;
                    float spacing = 0.2f;
                    float[] dashPattern = {length, spacing};
                    Border dashedBorder = new DashedBorder(Color.GRAY, thickness, dashPattern, true, topDashed, leftDashed, bottomDashed, rightDashed);
                    cells[row][col].setBorder(new CompoundBorder(matteBorder, dashedBorder));

                    // Add sum label in top-left corner of the first cell in the cage
                    if (i == 0) {
                        JLabel sumLabel = new JLabel(String.valueOf(cage.getSum()));
                        sumLabel.setFont(new Font("Arial", Font.PLAIN, 10));
                        sumLabel.setForeground(Color.GRAY);
                        sumLabel.setBounds(2, 2, 15, 15);
                        cells[row][col].add(sumLabel);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void updateCell(int row, int col, String value, Color foreground, Color background, boolean editable) {
        cells[row][col].setText(value);
        cells[row][col].setForeground(foreground);
        cells[row][col].setBackground(background);
        cells[row][col].setEditable(editable);

        Puzzle puzzle = gameController != null ? gameController.getCurrentPuzzle() : null;
        if (puzzle != null) {
            // Reapply borders
            float top = (row % SUBGRID == 0) ? 1.0f : 0.5f;
            float left = (col % SUBGRID == 0) ? 1.0f : 0.5f;
            float bottom = (row % SUBGRID == SUBGRID - 1) ? 1.0f : 0.5f;
            float right = (col % SUBGRID == SUBGRID - 1) ? 1.0f : 0.5f;
            Color borderColor = (row % SUBGRID == 0 || col % SUBGRID == 0 ||
                    row % SUBGRID == SUBGRID - 1 || col % SUBGRID == SUBGRID - 1)
                    ? Color.BLACK : Color.LIGHT_GRAY;
            Border matteBorder = BorderFactory.createMatteBorder(
                    (int) top, (int) left, (int) bottom, (int) right, borderColor);
            applyCageBorder(row, col, puzzle, matteBorder);
        }
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

    // Custom DashedBorder class
    private static class DashedBorder extends AbstractBorder {
        private final Color color;
        private final float thickness;
        private final float[] dashPattern;
        private final boolean rounded;
        private final boolean topDashed, leftDashed, bottomDashed, rightDashed;

        public DashedBorder(Color color, float thickness, float[] dashPattern, boolean rounded,
                            boolean topDashed, boolean leftDashed, boolean bottomDashed, boolean rightDashed) {
            this.color = color;
            this.thickness = thickness;
            this.dashPattern = dashPattern.clone();
            this.rounded = rounded;
            this.topDashed = topDashed;
            this.leftDashed = leftDashed;
            this.bottomDashed = bottomDashed;
            this.rightDashed = rightDashed;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, dashPattern, 0.0f));

            int inset = (int) (thickness * 2); // Inset to draw inside the outer matte border
            int adjustedX = x + inset;
            int adjustedY = y + inset;
            int adjustedWidth = width - 2 * inset;
            int adjustedHeight = height - 2 * inset;

            if (topDashed) g2d.drawLine(adjustedX, adjustedY, adjustedX + adjustedWidth, adjustedY);
            if (leftDashed) g2d.drawLine(adjustedX, adjustedY, adjustedX, adjustedY + adjustedHeight);
            if (bottomDashed) g2d.drawLine(adjustedX, adjustedY + adjustedHeight, adjustedX + adjustedWidth, adjustedY + adjustedHeight);
            if (rightDashed) g2d.drawLine(adjustedX + adjustedWidth, adjustedY, adjustedX + adjustedWidth, adjustedY + adjustedHeight);

            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets((int) (thickness * 2), (int) (thickness * 2), (int) (thickness * 2), (int) (thickness * 2));
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = (int) (thickness * 2);
            insets.top = (int) (thickness * 2);
            insets.right = (int) (thickness * 2);
            insets.bottom = (int) (thickness * 2);
            return insets;
        }
    }
}