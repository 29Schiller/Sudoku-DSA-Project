import validator.*;
import solver.SudokuSolver;
import controller.SudokuGameController;
import view.GameView;
import view.WelcomeScreen;
import javax.swing.SwingUtilities;

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