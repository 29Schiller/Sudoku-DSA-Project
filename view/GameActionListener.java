package view;

public interface GameActionListener {
    void onCellInput(int row, int col, String value);
    void onSolve();
    void onHint();
    void onCheck();
}