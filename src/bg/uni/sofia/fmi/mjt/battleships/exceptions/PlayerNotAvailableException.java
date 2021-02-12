package bg.uni.sofia.fmi.mjt.battleships.exceptions;

public class PlayerNotAvailableException extends RuntimeException{
    public PlayerNotAvailableException(String message) {
        super(message);
    }
}
