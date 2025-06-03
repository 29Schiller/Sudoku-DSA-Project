package controller;

import generator.*;
import mode.*;
import utils.GameModeType;
import utils.DifficultyLevel;
import view.*;

import java.util.HashMap;
import java.util.Map;

public class GameManager {
    private Map<GameModeType, PuzzleGenerator> puzzleGenerators;
    private Map<GameModeType, GameMode> gameModes;
    private Map<GameModeType, GameScreen> gameScreens;
    private WelcomeScreen welcomeScreen;
    private GameController gameController;

    public GameManager() {
        initializeComponents();
    }

    private void initializeComponents() {
        puzzleGenerators = new HashMap<>();
        puzzleGenerators.put(GameModeType.CLASSIC, new ClassicPuzzleGenerator());
        puzzleGenerators.put(GameModeType.ICE, new IcePuzzleGenerator());
        puzzleGenerators.put(GameModeType.KILLER, new KillerPuzzleGenerator());

        gameModes = new HashMap<>();
        gameModes.put(GameModeType.CLASSIC, new ClassicMode());
        gameModes.put(GameModeType.ICE, new IceMode());
        gameModes.put(GameModeType.KILLER, new KillerMode());

        gameScreens = new HashMap<>();
        gameScreens.put(GameModeType.CLASSIC, new ClassicScreen());
        gameScreens.put(GameModeType.ICE, new IceScreen());
        gameScreens.put(GameModeType.KILLER, new KillerScreen());

        welcomeScreen = new WelcomeScreen();
        welcomeScreen.setListener(new WelcomeScreenListener() {
            @Override
            public void onStartGame(String mode, String difficulty) {
                welcomeScreen.setVisible(false);
                GameModeType modeType = GameModeType.valueOf(mode);
                GameScreen screen = gameScreens.get(modeType);
                screen.setVisible(true);
                gameController = new GameController(
                    screen,
                    puzzleGenerators.get(modeType),
                    gameModes.get(modeType),
                    DifficultyLevel.valueOf(difficulty)
                );
                ((IceScreen) gameScreens.get(GameModeType.ICE)).setGameController(gameController);
                ((KillerScreen) gameScreens.get(GameModeType.KILLER)).setGameController(gameController);
                gameController.startNewGame();
            }
        });
    }

    public void start() {
        welcomeScreen.setVisible(true);
    }
}