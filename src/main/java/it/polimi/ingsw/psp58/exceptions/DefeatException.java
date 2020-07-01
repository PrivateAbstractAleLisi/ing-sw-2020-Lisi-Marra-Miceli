package it.polimi.ingsw.psp58.exceptions;

/**
 * thrown when someone is defeated (For example there isn't a worker he can move)
 */
public class DefeatException extends Exception {
    public DefeatException (String playerDefeatedUsername) {
        super(playerDefeatedUsername);
    }
}
