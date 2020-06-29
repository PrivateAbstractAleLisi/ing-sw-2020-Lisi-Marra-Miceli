package it.polimi.ingsw.psp58.exceptions;

/**
 * thrown when you select a card that is not valid, for example because it's not in the available cards list
 */
public class InvalidCardException extends Exception {
    public InvalidCardException(String message) {
        super(message);
    }
}
