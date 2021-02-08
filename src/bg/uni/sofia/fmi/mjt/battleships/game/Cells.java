package bg.uni.sofia.fmi.mjt.battleships.game;

public enum Cells {
    HIT_EMPTY("-", "-"),
    HIT_SHIP("X", "X"),
    EMPTY("_", "_"),
    SHIP("_", "*");

    private final String enemySymbol;
    private final String yourSymbol;

    Cells(String enemySymbol, String yourSymbol) {
        this.enemySymbol = enemySymbol;
        this.yourSymbol = yourSymbol;
    }

    public String getEnemySymbol() {
        return enemySymbol;
    }

    public String getYourSymbol() {
        return yourSymbol;
    }
}
