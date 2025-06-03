package view;

import controller.GameController;
import model.Puzzle;
import model.Solution;
import mode.GameMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class IceScreen extends GameScreen {
    private static final Color FROZEN_COLOR = new Color(173, 216, 230); // Pale blue
    private GameController gameController;

    public IceScreen() {
        super("Sudoku Game - Ice Mode");
    }

    public void setGameController(GameController controller) {
        this.gameController = controller;
    }

    @Override
    protected void createGameBoard() {
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

                int top = (row % SUBGRID == 0) ? 3 : 1;
                int left = (col % SUBGRID == 0) ? 3 : 1;
                int bottom = (row % SUBGRID == SUBGRID - 1) ? 3 : 1;
                int right = (col % SUBGRID == SUBGRID - 1) ? 3 : 1;
                Color borderColor = (row % SUBGRID == 0 || col % SUBGRID == 0 ||
                        row % SUBGRID == SUBGRID - 1 || col % SUBGRID == SUBGRID - 1)
                        ? Color.BLACK : Color.GRAY;
                cells[row][col].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, borderColor));

                final int r = row, c = col;
                cells[row][col].addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        if (!cells[r][c].isEditable()) {
                            e.consume();
                            return;
                        }
                        char ch = e.getKeyChar();
                        if (!Character.isDigit(ch) || ch == '0' || !cells[r][c].getText().isEmpty()) {
                            e.consume();
                        }
                    }
                });

                cells[row][col].addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        if (listener != null && cells[r][c].isEditable()) {
                            listener.onCellInput(r, c, cells[r][c].getText());
                            checkAdjacentUnfreeze(r, c);
                        }
                    }
                });

                boardPanel.add(cells[row][col]);
            }
        }

        add(boardPanel, BorderLayout.CENTER);
    }

    private void checkAdjacentUnfreeze(int row, int col) {
        if (gameController == null || cells[row][col].getText().isEmpty()) return;

        int value = Integer.parseInt(cells[row][col].getText());
        Puzzle puzzle = gameController.getCurrentPuzzle();
        Solution solution = gameController.getCurrentSolution();

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Top, bottom, left, right
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (newRow >= 0 && newRow < SIZE && newCol >= 0 && newCol < SIZE &&
                    cells[newRow][newCol].getBackground().equals(FROZEN_COLOR) &&
                    !puzzle.isFixed(newRow, newCol)) {
                if (solution.getCell(row, col) == value) { // Check if input is correct
                    puzzle.setFrozen(newRow, newCol, false); // Unfreeze the cell
                    cells[newRow][newCol].setBackground(Color.WHITE);
                    cells[newRow][newCol].setEditable(true);
                }
            }
        }
    }

    @Override
    public void updateBoard(Puzzle puzzle, Solution solution, GameMode mode) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col].setText("");
                cells[row][col].setEditable(true);
                cells[row][col].setForeground(Color.BLACK);
                cells[row][col].setBackground(puzzle.isFrozen(row, col) && !puzzle.isFixed(row, col) ? FROZEN_COLOR : Color.WHITE);

                int top = (row % SUBGRID == 0) ? 3 : 1;
                int left = (col % SUBGRID == 0) ? 3 : 1;
                int bottom = (row % SUBGRID == SUBGRID - 1) ? 3 : 1;
                int right = (col % SUBGRID == SUBGRID - 1) ? 3 : 1;
                Color borderColor = (row % SUBGRID == 0 || col % SUBGRID == 0 ||
                        row % SUBGRID == SUBGRID - 1 || col % SUBGRID == SUBGRID - 1)
                        ? Color.BLACK : Color.GRAY;
                cells[row][col].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, borderColor));

                mode.renderCell(cells[row][col], row, col, puzzle, solution);
                if (puzzle.isFrozen(row, col) && !puzzle.isFixed(row, col)) {
                    cells[row][col].setEditable(false);
                }
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

        int top = (row % SUBGRID == 0) ? 3 : 1;
        int left = (col % SUBGRID == 0) ? 3 : 1;
        int bottom = (row % SUBGRID == SUBGRID - 1) ? 3 : 1;
        int right = (col % SUBGRID == SUBGRID - 1) ? 3 : 1;
        Color borderColor = (row % SUBGRID == 0 || col % SUBGRID == 0 ||
                row % SUBGRID == SUBGRID - 1 || col % SUBGRID == SUBGRID - 1)
                ? Color.BLACK : Color.GRAY;
        cells[row][col].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, borderColor));
    }
}