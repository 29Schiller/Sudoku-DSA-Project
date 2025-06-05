package utils;

import java.awt.Color;

public final class Constants {
    public static final int BOARD_SIZE = 9;
    public static final int SUBGRID_SIZE = 3;
    public static final String CLASSIC_MODE = "CLASSIC";
    public static final String ICE_MODE = "ICE";
    public static final Color FROZEN_CELL_COLOR = new Color(173, 216, 230);
    public static final Color FIXED_CELL_COLOR = new Color(200, 200, 200);
    public static final Color DEFAULT_CELL_COLOR = Color.WHITE;
    public static final Color ALTERNATE_CELL_COLOR = new Color(240, 240, 240);
    public static final Color ERROR_CELL_COLOR = new Color(255, 200, 200);
    public static final Color HINT_CELL_COLOR = new Color(0, 150, 0);
    public static final Color SOLVE_CELL_COLOR = Color.BLUE;
    public static final String[] DIFFICULTIES = {
        DifficultyLevel.EASY.name(),
        DifficultyLevel.MEDIUM.name(),
        DifficultyLevel.HARD.name(),
        DifficultyLevel.EXPERT.name()
    };

    private Constants() {} // Prevent instantiation
}