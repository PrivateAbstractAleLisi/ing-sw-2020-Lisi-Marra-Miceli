package exceptions;

public class InvalidCardException extends Exception {
    public InvalidCardException(String message) {
        super(message);
    }
}
