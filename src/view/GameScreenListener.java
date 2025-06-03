package view;

public interface GameScreenListener {
    void onCellInput(int row, int col, String value);
    void onNewGame(String mode, String difficulty);
    void onSolve();
    void onHint();
    void onCheck();
    void onTimerTick();
}