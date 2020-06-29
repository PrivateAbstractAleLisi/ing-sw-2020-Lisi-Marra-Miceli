package it.polimi.ingsw.psp58.exceptions;

/**
 * thrown when you try to insert a player that he's already in the game
 */
public class AlreadyExistingPlayerException extends Exception {
    public AlreadyExistingPlayerException(String message) {
        super(message);
    }
}
