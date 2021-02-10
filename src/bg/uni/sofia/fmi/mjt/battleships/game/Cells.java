package bg.uni.sofia.fmi.mjt.battleships.game;

import java.io.Serial;
import java.io.Serializable;

public enum Cells implements Serializable {
    HIT_EMPTY("-", "-"),
    HIT_SHIP("X", "X"),
    EMPTY("_", "_"),
    SHIP("_", "*");

    @Serial
    private static final long serialVersionUID = 1349480317026438792L;

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
