package it.polimi.ingsw.psp58.exceptions;

/**
 * @author alelisi
 */
public class InvalidWorkerRemovalException extends Exception {
    public InvalidWorkerRemovalException() {
        super();
    }
    public InvalidWorkerRemovalException(String errorMessage) {
        super(errorMessage);
    }
}
