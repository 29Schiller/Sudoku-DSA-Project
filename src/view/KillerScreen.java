package view;

import controller.GameController;
import model.Puzzle;
import model.Solution;
import mode.GameMode;
import model.Cage;
import utils.Point;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.*;

public class KillerScreen extends GameScreen {
    private GameController gameController;

    public KillerScreen() {
        super("Sudoku Game - Killer Mode");
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
                cells[row][col].setLayout(null); // For absolute positioning of sum labels

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

    @Override
    public void updateBoard(Puzzle puzzle, Solution solution, GameMode mode) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col].setText("");
                cells[row][col].setEditable(true);
                cells[row][col].setForeground(Color.BLACK);
                cells[row][col].setBackground(Color.WHITE);
                cells[row][col].removeAll(); // Clear any previous sum labels

                int top = (row % SUBGRID == 0) ? 3 : 1;
                int left = (col % SUBGRID == 0) ? 3 : 1;
                int bottom = (row % SUBGRID == SUBGRID - 1) ? 3 : 1;
                int right = (col % SUBGRID == SUBGRID - 1) ? 3 : 1;
                Color borderColor = (row % SUBGRID == 0 || col % SUBGRID == 0 ||
                        row % SUBGRID == SUBGRID - 1 || col % SUBGRID == SUBGRID - 1)
                        ? Color.BLACK : Color.GRAY;
                cells[row][col].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, borderColor));

                mode.renderCell(cells[row][col], row, col, puzzle, solution);
                applyCageBorder(row, col, puzzle);
            }
        }
        uiTimer.start();
    }

    private void applyCageBorder(int row, int col, Puzzle puzzle) {
        if (puzzle.getCages() == null) return;

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

                    // Apply dotted gray border if this cell is on the edge
                    int topThickness = 1, leftThickness = 1, bottomThickness = 1, rightThickness = 1;
                    Color topColor = Color.GRAY, leftColor = Color.GRAY, bottomColor = Color.GRAY, rightColor = Color.GRAY;

                    if (row == minRow) topThickness = 2;
                    if (col == minCol) leftThickness = 2;
                    if (row == maxRow) bottomThickness = 2;
                    if (col == maxCol) rightThickness = 2;

                    if (row % SUBGRID == 0) { topThickness = 3; topColor = Color.BLACK; }
                    if (col % SUBGRID == 0) { leftThickness = 3; leftColor = Color.BLACK; }
                    if (row % SUBGRID == SUBGRID - 1) { bottomThickness = 3; bottomColor = Color.BLACK; }
                    if (col % SUBGRID == SUBGRID - 1) { rightThickness = 3; rightColor = Color.BLACK; }

                    float[] dash = {2.0f};
                    Border dashedBorder = BorderFactory.createDashedBorder(Color.GRAY, 1.0F,1F,0.5F, true ); // Fixed: Use correct overload
                    Border matteBorder = BorderFactory.createMatteBorder(topThickness, leftThickness, bottomThickness, rightThickness, Color.BLACK);
                    cells[row][col].setBorder(new CompoundBorder(dashedBorder, matteBorder));

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
            applyCageBorder(row, col, puzzle);
        }
    }
}