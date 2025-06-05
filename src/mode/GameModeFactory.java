package mode;

public class GameModeFactory {
    public static GameMode createGameMode(String mode) {
        return switch (mode) {
            case "CLASSIC" -> new ClassicMode();
            case "ICE" -> new IceMode();
            case "KILLER" -> new KillerMode();
            default -> throw new IllegalArgumentException("Unknown game mode: " + mode);
        };
    }
}