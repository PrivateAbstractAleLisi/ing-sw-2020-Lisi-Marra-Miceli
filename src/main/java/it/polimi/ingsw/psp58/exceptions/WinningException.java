package it.polimi.ingsw.psp58.exceptions;

/**
 * thrown when someone wins the match
 */
public class WinningException extends Throwable {
    public WinningException(String message) {
        super(message);
    }
}
