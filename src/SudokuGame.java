import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Main GUI class for Sudoku Game
 */
public class SudokuGame extends JFrame implements ActionListener {
    private static final int SIZE = 9;
    private static final int SUBGRID = 3;

    // GUI Components
    private JTextField[][] cells;
    private JButton newGameBtn, solveBtn, hintBtn, checkBtn;
    private JComboBox<String> difficultyBox;
    private JLabel timeLabel, scoreLabel, statusLabel;
    private javax.swing.Timer uiTimer;
    private int timeElapsed;
    private int score;

    // Game Logic
    private GameManager gameManager;
    private int[][] puzzle;
    private int[][] solution;
    private boolean[][] isFixed;

    public SudokuGame(String difficulty) {
        gameManager = GameManager.getInstance();
        initializeGUI();
        difficultyBox.setSelectedItem(difficulty); // Set difficulty from welcome screen
        newGame();
    }

    private void initializeGUI() {
        setTitle("Sudoku Game - Professional Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        createGameBoard();
        createControlPanel();
        createStatusPanel();

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void createGameBoard() {
        JPanel boardPanel = new JPanel(new GridLayout(SIZE, SIZE, 2, 2));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        boardPanel.setBackground(Color.BLACK);

        cells = new JTextField[SIZE][SIZE];

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = new JTextField();
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(new Font("Arial", Font.BOLD, 16));
                cells[row][col].setPreferredSize(new Dimension(40, 40));

                // Color coding for sub-grids
                if (((row / SUBGRID + col / SUBGRID) % 2) == 0) {
                    cells[row][col].setBackground(new Color(240, 240, 240));
                } else {
                    cells[row][col].setBackground(Color.WHITE);
                }

                // Add input validation
                final int r = row, c = col;
                cells[row][col].addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char ch = e.getKeyChar();
                        if (!Character.isDigit(ch) || ch == '0' ||
                                cells[r][c].getText().length() >= 1) {
                            e.consume();
                        }
                    }
                });

                cells[row][col].addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        validateInput(r, c);
                    }
                });

                boardPanel.add(cells[row][col]);
            }
        }

        add(boardPanel, BorderLayout.CENTER);
    }

    private void createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout());

        newGameBtn = new JButton("New Game");
        solveBtn = new JButton("Solve");
        hintBtn = new JButton("Hint");
        checkBtn = new JButton("Check");

        String[] difficulties = { "Fast", "Easy", "Medium", "Hard", "Expert", "Extreme" };
        difficultyBox = new JComboBox<>(difficulties);

        newGameBtn.addActionListener(this);
        solveBtn.addActionListener(this);
        hintBtn.addActionListener(this);
        checkBtn.addActionListener(this);

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

        // Initialize timer
        uiTimer = new javax.swing.Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeElapsed++;
                updateTimeDisplay();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newGameBtn) {
            newGame();
        } else if (e.getSource() == solveBtn) {
            solvePuzzle();
        } else if (e.getSource() == hintBtn) {
            giveHint();
        } else if (e.getSource() == checkBtn) {
            checkSolution();
        }
    }

    private void newGame() {
        String difficulty = (String) difficultyBox.getSelectedItem();
        DifficultyLevel level = DifficultyLevel.valueOf(difficulty.toUpperCase());

        PuzzleGenerator generator = new PuzzleGenerator();
        PuzzlePair puzzlePair = generator.generatePuzzle(level);

        puzzle = puzzlePair.getPuzzle();
        solution = puzzlePair.getSolution();
        isFixed = new boolean[SIZE][SIZE];

        // Reset game state
        timeElapsed = 0;
        score = 1000;
        updateBoard();
        updateTimeDisplay();
        updateScore();
        statusLabel.setText("Game started! Good luck!");

        uiTimer.start();
    }

    private void updateBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (puzzle[row][col] != 0) {
                    cells[row][col].setText(String.valueOf(puzzle[row][col]));
                    cells[row][col].setEditable(false);
                    cells[row][col].setBackground(new Color(200, 200, 200));
                    cells[row][col].setFont(new Font("Arial", Font.BOLD, 16));
                    isFixed[row][col] = true;
                } else {
                    cells[row][col].setText("");
                    cells[row][col].setEditable(true);
                    if (((row / SUBGRID + col / SUBGRID) % 2) == 0) {
                        cells[row][col].setBackground(new Color(240, 240, 240));
                    } else {
                        cells[row][col].setBackground(Color.WHITE);
                    }
                    cells[row][col].setFont(new Font("Arial", Font.PLAIN, 16));
                    isFixed[row][col] = false;
                }
            }
        }
    }

    private void validateInput(int row, int col) {
        if (isFixed[row][col])
            return;

        String text = cells[row][col].getText();
        if (text.isEmpty())
            return;

        try {
            int value = Integer.parseInt(text);
            if (value < 1 || value > 9) {
                cells[row][col].setText("");
                return;
            }

            if (isValidMove(row, col, value)) {
                cells[row][col].setForeground(Color.BLACK);
                puzzle[row][col] = value;

                if (isPuzzleComplete()) {
                    uiTimer.stop();
                    int bonus = Math.max(0, 1000 - timeElapsed);
                    score += bonus;
                    updateScore();
                    statusLabel.setText("Congratulations! Puzzle solved!");
                    JOptionPane.showMessageDialog(this,
                            "Congratulations!\nTime: " + formatTime(timeElapsed) +
                                    "\nScore: " + score,
                            "Victory!", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                cells[row][col].setForeground(Color.RED);
                score = Math.max(0, score - 10);
                updateScore();
                statusLabel.setText("Invalid move! Try again.");
            }
        } catch (NumberFormatException ex) {
            cells[row][col].setText("");
        }
    }

    private boolean isValidMove(int row, int col, int value) {
        int originalValue = puzzle[row][col];
        puzzle[row][col] = 0;

        boolean valid = SudokuValidator.isValid(puzzle, row, col, value);
        puzzle[row][col] = originalValue;

        return valid;
    }

    private boolean isPuzzleComplete() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (puzzle[row][col] == 0)
                    return false;
            }
        }
        return SudokuValidator.isValidSudoku(puzzle);
    }

    private void solvePuzzle() {
        if (JOptionPane.showConfirmDialog(this,
                "This will solve the entire puzzle. Continue?",
                "Solve Puzzle", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            uiTimer.stop();

            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    if (!isFixed[row][col]) {
                        cells[row][col].setText(String.valueOf(solution[row][col]));
                        cells[row][col].setForeground(Color.BLUE);
                    }
                }
            }

            statusLabel.setText("Puzzle solved automatically!");
        }
    }

    private void giveHint() {
        List<Point> emptyCells = new ArrayList<>();

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (!isFixed[row][col] && cells[row][col].getText().isEmpty()) {
                    emptyCells.add(new Point(row, col));
                }
            }
        }

        if (emptyCells.isEmpty()) {
            statusLabel.setText("No empty cells to hint!");
            return;
        }

        Point hint = emptyCells.get(new Random().nextInt(emptyCells.size()));
        int row = hint.x;
        int col = hint.y;

        cells[row][col].setText(String.valueOf(solution[row][col]));
        cells[row][col].setForeground(new Color(0, 150, 0));
        cells[row][col].setEditable(false);
        puzzle[row][col] = solution[row][col];

        score = Math.max(0, score - 50);
        updateScore();
        statusLabel.setText("Hint given! (-50 points)");
    }

    private void checkSolution() {
        int errors = 0;

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (!isFixed[row][col] && !cells[row][col].getText().isEmpty()) {
                    try {
                        int value = Integer.parseInt(cells[row][col].getText());
                        if (value != solution[row][col]) {
                            cells[row][col].setBackground(new Color(255, 200, 200));
                            errors++;
                        } else {
                            if (((row / SUBGRID + col / SUBGRID) % 2) == 0) {
                                cells[row][col].setBackground(new Color(240, 240, 240));
                            } else {
                                cells[row][col].setBackground(Color.WHITE);
                            }
                        }
                    } catch (NumberFormatException ex) {
                        // Empty cell, skip
                    }
                }
            }
        }

        if (errors == 0) {
            statusLabel.setText("All filled cells are correct!");
        } else {
            statusLabel.setText(errors + " error(s) found (highlighted in red)");
            score = Math.max(0, score - errors * 5);
            updateScore();
        }
    }

    private void updateTimeDisplay() {
        timeLabel.setText("Time: " + formatTime(timeElapsed));
    }

    private void updateScore() {
        scoreLabel.setText("Score: " + score);
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    // Inner Point class for compatibility with existing code
    private static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}