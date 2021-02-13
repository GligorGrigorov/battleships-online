package bg.uni.sofia.fmi.mjt.battleships.game;

import java.io.Serial;
import java.io.Serializable;

public enum Status implements Serializable {
    PENDING,
    IN_PROGRESS,
    FINISHED;

    @Serial
    private static final long serialVersionUID = 2669480397026438792L;
}
