package it.polimi.ingsw.psp58.exceptions;

/**
 * @author alelisi
 * throws when you try to make an invalid build
 */
public class InvalidBuildException extends Exception {
    public InvalidBuildException(String message) {
        super(message);
    }
}
