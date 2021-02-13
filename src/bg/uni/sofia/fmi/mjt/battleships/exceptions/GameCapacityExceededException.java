package bg.uni.sofia.fmi.mjt.battleships.exceptions;

public class GameCapacityExceededException extends RuntimeException {
    public GameCapacityExceededException(String message) {
        super(message);
    }
}
