package bg.uni.sofia.fmi.mjt.battleships.game;

public enum Status {
    PENDING("pending"),
    IN_PROGRESS("in progress"),
    FINISHED("finished");
    private final String status;

    Status(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
