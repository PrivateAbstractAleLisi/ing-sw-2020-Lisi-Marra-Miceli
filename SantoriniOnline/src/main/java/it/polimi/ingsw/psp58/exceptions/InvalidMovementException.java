package it.polimi.ingsw.psp58.exceptions;

/**
 * thrown when an invalid movement attempt has been made
 */
public class InvalidMovementException extends Exception {
    public InvalidMovementException(String message) {
        super(message);
    }
}
