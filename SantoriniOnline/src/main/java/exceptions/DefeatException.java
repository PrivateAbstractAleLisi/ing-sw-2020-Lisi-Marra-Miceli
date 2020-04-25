package exceptions;

public class DefeatException extends Exception {
    public DefeatException (String playerDefeatedUsername) {
        super(playerDefeatedUsername);
    }
}
