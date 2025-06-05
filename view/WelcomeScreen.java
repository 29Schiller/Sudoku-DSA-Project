package view;

import utils.Constants;
import utils.GameModeType;

import javax.swing.*;
import java.awt.*;

public class WelcomeScreen extends JFrame {
    private JComboBox<GameModeType> modeBox;
    private JComboBox<String> difficultyBox;
    private GameStartListener listener;

    public WelcomeScreen() {
        setTitle("Sudoku Game - Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 300));
        initializeUI();
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    public void setListener(GameStartListener listener) {
        this.listener = listener;
    }

    private void initializeUI() {
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("Sudoku Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel(new GridLayout(4, 1, 10, 10));
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

        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.PLAIN, 16));
        startButton.addActionListener(_ -> {
            if (listener != null) {
                String mode = modeBox.getSelectedItem().toString();
                Object difficulty = difficultyBox.getSelectedItem();
                listener.onStartGame(mode, (String) difficulty);
            } else {
                JOptionPane.showMessageDialog(this, "Game controller not initialized.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        controlPanel.add(startButton);

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 16));
        exitButton.addActionListener(_ -> System.exit(0));
        controlPanel.add(exitButton);

        add(controlPanel, BorderLayout.CENTER);
    }
}