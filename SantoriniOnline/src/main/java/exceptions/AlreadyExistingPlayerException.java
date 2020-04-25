package exceptions;

public class AlreadyExistingPlayerException extends Exception {
    public AlreadyExistingPlayerException(String message) {
        super(message);
    }
}
