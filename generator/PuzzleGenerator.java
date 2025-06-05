package generator;

import model.Puzzle;
import model.Solution;
import utils.DifficultyLevel;

public interface PuzzleGenerator {
    Puzzle generatePuzzle(DifficultyLevel difficulty);
    Solution getSolution();
}