package Modelo;

public class PlayerSessionData {
    private static String localUsername;
    private static int localScore;
    private static String deathTimestamp;

    public static String getLocalUsername() {
        return localUsername;
    }

    public static void setLocalUsername(String localUsername) {
        PlayerSessionData.localUsername = localUsername;
    }

    public static int getLocalScore() {
        return localScore;
    }

    public static void setLocalScore(int localScore) {
        PlayerSessionData.localScore = localScore;
    }

    public static String getDeathTimestamp() {
        return deathTimestamp;
    }

    public static void setDeathTimestamp(String deathTimestamp) {
        PlayerSessionData.deathTimestamp = deathTimestamp;
    }
}