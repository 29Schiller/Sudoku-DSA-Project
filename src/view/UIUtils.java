package view;

import utils.Constants;
import utils.GameModeType;
import javax.swing.*;
import java.awt.*;

public class UIUtils {
    public static JComboBox<GameModeType> createModeComboBox() {
        JComboBox<GameModeType> comboBox = new JComboBox<>(GameModeType.values());
        comboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        return comboBox;
    }

    public static JComboBox<String> createDifficultyComboBox() {
        JComboBox<String> comboBox = new JComboBox<>(Constants.DIFFICULTIES);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        return comboBox;
    }
}