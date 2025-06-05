package utils;

public enum GameModeType {
    CLASSIC,
    ICE;

    @Override
    public String toString() {
        return name();
    }
}