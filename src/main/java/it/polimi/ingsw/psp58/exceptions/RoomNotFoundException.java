package it.polimi.ingsw.psp58.exceptions;

/**
 * thrown during the client disconnection process when a room it's not found
 */
public class RoomNotFoundException extends Exception {
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public RoomNotFoundException(String message) {
        super(message);
    }
}
