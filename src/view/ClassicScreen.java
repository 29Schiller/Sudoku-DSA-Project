package view;

import model.Puzzle;
import model.Solution;
import mode.GameMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class ClassicScreen extends GameScreen {
    public ClassicScreen() {
        super("Sudoku Game - Classic Mode");
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

                int top = (row % SUBGRID == 0) ? 3 : 1;
                int left = (col % SUBGRID == 0) ? 3 : 1;
                int bottom = (row % SUBGRID == SUBGRID - 1) ? 3 : 1;
                int right = (col % SUBGRID == SUBGRID - 1) ? 3 : 1;
                Color borderColor = (row % SUBGRID == 0 || col % SUBGRID == 0 ||
                        row % SUBGRID == SUBGRID - 1 || col % SUBGRID == SUBGRID - 1)
                        ? Color.BLACK : Color.GRAY;
                cells[row][col].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, borderColor));

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