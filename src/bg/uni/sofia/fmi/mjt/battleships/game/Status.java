package bg.uni.sofia.fmi.mjt.battleships.game;

import java.io.Serial;
import java.io.Serializable;

public enum Status implements Serializable {
    PENDING("pending"),
    IN_PROGRESS("in progress"),
    FINISHED("finished");

    @Serial
    private static final long serialVersionUID = 2669480397026438792L;

    private final String status;

    Status(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
