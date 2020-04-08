package model.exception;

public class DefeatException extends Exception {
    public DefeatException (String playerDefeated) {
        super(playerDefeated);
    }
}
