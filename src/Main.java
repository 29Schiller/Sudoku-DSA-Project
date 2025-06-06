import javax.swing.SwingUtilities;

import controller.SudokuGameController;
import solver.SudokuSolver;
import validator.StandardSudokuValidator;
import validator.SudokuValidator;
import view.GameView;
import view.WelcomeScreen;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SudokuValidator validator = new StandardSudokuValidator();
            SudokuSolver solver = new SudokuSolver(validator);
            GameView gameView = new GameView();
            WelcomeScreen welcomeScreen = new WelcomeScreen();
            new SudokuGameController(validator, solver, gameView, welcomeScreen).start();
        });
    }
}