package view;

import controller.SudokuGameController;
import view.HintSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HintDialog extends JDialog {
    private final HintSystem.Hint hint;
    private final SudokuGameController gameRef;

    public HintDialog(JFrame parent, HintSystem.Hint hint, SudokuGameController gameRef) {
        super(parent, "Sudoku Hint", true);
        this.hint = hint;
        this.gameRef = gameRef;

        initializeDialog();
    }

    private void initializeDialog() {
        setLayout(new BorderLayout());
        setSize(500, 400);
        setLocationRelativeTo(getParent());

        // Header with technique name
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Content with explanation
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(63, 81, 181));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Technique name
        JLabel titleLabel = new JLabel("Technique: " + hint.getType().getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        // Technique description
        JLabel descLabel = new JLabel(hint.getType().getDescription());
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(new Color(200, 220, 255));

        // Cost
        JLabel costLabel = new JLabel("Cost: " + hint.getCostPoints() + " points");
        costLabel.setFont(new Font("Arial", Font.BOLD, 12));
        costLabel.setForeground(Color.YELLOW);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descLabel, BorderLayout.CENTER);

        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(costLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Quick explanation
        JLabel explanationLabel = new JLabel("<html>" +
                "<b>Quick Hint:</b><br>" +
                hint.getExplanation() + "</html>");
        explanationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        explanationLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));

        // Detailed explanation in text area
        JTextArea detailsArea = new JTextArea(hint.getDetailedSteps());
        detailsArea.setFont(new Font("Arial", Font.PLAIN, 13));
        detailsArea.setEditable(false);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setLineWrap(true);
        detailsArea.setBackground(new Color(248, 249, 250));
        detailsArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(detailsArea);
        scrollPane.setPreferredSize(new Dimension(450, 200));
        scrollPane.setBorder(BorderFactory.createTitledBorder("How to solve this:"));

        panel.add(explanationLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));

        // Show Me button - highlights the cell
        JButton showButton = new JButton("Show Me");
        showButton.setPreferredSize(new Dimension(120, 35));
        showButton.setBackground(new Color(33, 150, 243));
        showButton.setForeground(Color.WHITE);
        showButton.setFocusPainted(false);
        showButton.setFont(new Font("Arial", Font.BOLD, 12));
        showButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHintLocation();
                dispose();
            }
        });

        // Apply Hint button - fills the cell
        JButton applyButton = new JButton("Apply Hint");
        applyButton.setPreferredSize(new Dimension(120, 35));
        applyButton.setBackground(new Color(255, 193, 7));
        applyButton.setForeground(Color.BLACK);
        applyButton.setFocusPainted(false);
        applyButton.setFont(new Font("Arial", Font.BOLD, 12));
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyHint();
                dispose();
            }
        });

        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setBackground(new Color(158, 158, 158));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        panel.add(showButton);
        panel.add(applyButton);
        panel.add(cancelButton);

        return panel;
    }

    private void showHintLocation() {
        // Highlight the hint cell for 5 seconds
        gameRef.highlightCell(hint.getRow(), hint.getCol(), true);

        // Show a brief message
        gameRef.showStatus("Hint location highlighted! Cell R" + (hint.getRow() + 1) +
                "C" + (hint.getCol() + 1) + " = " + hint.getValue());

        // Clear highlight after 5 seconds
        Timer timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameRef.highlightCell(hint.getRow(), hint.getCol(), false);
                gameRef.showStatus("Ready to play!");
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();
    }

    private void applyHint() {
        // Apply the hint to the game
        gameRef.applyCellValue(hint.getRow(), hint.getCol(), hint.getValue());
        gameRef.decreaseScore(hint.getCostPoints());
        gameRef.incrementHintCount();

        // Show feedback
        gameRef.showStatus("Hint applied! -" + hint.getCostPoints() + " points");
    }
}