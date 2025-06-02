import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WelcomeScreen extends JFrame implements ActionListener {
    private JComboBox<String> difficultyBox;
    private JButton startButton, exitButton;

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

    private void initializeUI() {
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("Sudoku Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Control panel
        JPanel controlPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Difficulty selector
        String[] difficulties = {"Easy", "Medium", "Hard", "Expert"};
        difficultyBox = new JComboBox<>(difficulties);
        difficultyBox.setFont(new Font("Arial", Font.PLAIN, 16));
        JPanel difficultyPanel = new JPanel();
        difficultyPanel.add(new JLabel("Select Difficulty:"));
        difficultyPanel.add(difficultyBox);
        controlPanel.add(difficultyPanel);

        // Start button
        startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.PLAIN, 16));
        startButton.addActionListener(this);
        controlPanel.add(startButton);

        // Exit button
        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 16));
        exitButton.addActionListener(this);
        controlPanel.add(exitButton);

        add(controlPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            String selectedDifficulty = (String) difficultyBox.getSelectedItem();
            SwingUtilities.invokeLater(() -> {
                SudokuGame game = new SudokuGame(selectedDifficulty);
                game.setVisible(true);
                dispose(); // Close the welcome screen
            });
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }
}