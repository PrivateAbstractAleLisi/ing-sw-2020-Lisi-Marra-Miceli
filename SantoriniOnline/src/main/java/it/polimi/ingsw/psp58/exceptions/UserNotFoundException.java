package it.polimi.ingsw.psp58.exceptions;

/**
 * thrown when trying to find a user in a room but the user doesn't exists
 */
public class UserNotFoundException extends Exception {
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
