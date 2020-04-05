package model.exception;

public class AlreadyExistingPlayerException extends Exception {
    public AlreadyExistingPlayerException(String message) {
        super(message);
    }
}
