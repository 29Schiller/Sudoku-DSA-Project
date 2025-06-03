package view;

import utils.GameModeType;

import javax.swing.*;
import java.awt.*;

public class WelcomeScreen extends JFrame {
    private JComboBox<String> difficultyBox;
    private JComboBox<GameModeType> modeBox;
    private WelcomeScreenListener listener;

    public WelcomeScreen() {
        setTitle("Sudoku Game - Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        initializeGUI();
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    public void setListener(WelcomeScreenListener listener) {
        this.listener = listener;
    }

    private void initializeGUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Welcome to Sudoku Game!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        JLabel modeLabel = new JLabel("Select Mode:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(modeLabel, gbc);

        modeBox = new JComboBox<>(GameModeType.values());
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(modeBox, gbc);

        JLabel difficultyLabel = new JLabel("Select Difficulty:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(difficultyLabel, gbc);

        String[] difficulties = {"EASY", "MEDIUM", "HARD", "EXPERT"};
        difficultyBox = new JComboBox<>(difficulties);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(difficultyBox, gbc);

        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(_ -> {
            if (listener != null) {
                listener.onStartGame(modeBox.getSelectedItem().toString(), (String) difficultyBox.getSelectedItem());
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(startButton, gbc);
    }
}