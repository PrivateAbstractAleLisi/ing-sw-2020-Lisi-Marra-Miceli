package model;

import java.util.Map;

public class TurnManager {
    private Map<Integer, Player> turnSequence;
    private int currentTurn;

    public enum Phases {
        DRAW,
        MOVE,
        BUILD,
        END
    }

    private Phases currentPhase;
    private int numberOfPlayers;
    private Player currentPlayer;
    private BoardManager board;

    public TurnManager(Map<Integer, Player> turnSequence, int numberOfPlayers) {
        this.turnSequence = turnSequence;
        this.currentTurn = 0;
        this.numberOfPlayers = numberOfPlayers;
    }

    public void setTurnSequence(Map<Integer, Player> turnSequence) {
        this.turnSequence = turnSequence;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    private void nextTurn() {
        if (this.currentTurn == numberOfPlayers - 1) {
            this.currentTurn = 0;
        } else {
            this.currentTurn++;
        }
    }

    public String getCurrentPlayer() {
        return this.currentPlayer.getUsername();
    }


}
