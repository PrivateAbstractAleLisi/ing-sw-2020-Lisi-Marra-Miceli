package it.polimi.ingsw.psp58.exceptions;

/**
 * thrown when trying to add a player with no available rooms in the lobby
 */
public class NotFreeRoomAvailableException extends Exception {
    public NotFreeRoomAvailableException(String message) {
        super(message);
    }
}
