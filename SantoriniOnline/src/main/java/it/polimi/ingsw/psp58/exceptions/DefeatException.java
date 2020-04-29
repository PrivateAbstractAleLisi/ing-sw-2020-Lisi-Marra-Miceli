package it.polimi.ingsw.psp58.exceptions;

public class DefeatException extends Exception {
    public DefeatException (String playerDefeatedUsername) {
        super(playerDefeatedUsername);
    }
}
