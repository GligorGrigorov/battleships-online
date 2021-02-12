package bg.uni.sofia.fmi.mjt.battleships.exceptions;

public class GameNotAvailableException extends RuntimeException{
    public GameNotAvailableException(String message) {
        super(message);
    }
}
