package view;

public interface GameStartListener {
    void onStartGame(String mode, String difficulty);
    void onNewGame(String mode, String difficulty);
}