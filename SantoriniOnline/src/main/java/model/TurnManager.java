package model;

import java.util.Map;

import static model.TurnPhases.*;

//TODO Spostato fuori, come su worker, va bene?

public class TurnManager {

    private Map<Integer, Player> turnSequence;
    private int currentTurn;

    private TurnPhases currentPhase;
    private int numberOfPlayers;
    private Player currentPlayer;
    private BoardManager board;

    public TurnManager(Map<Integer, Player> turnSequence, int numberOfPlayers) {
        this.turnSequence = turnSequence;
        this.currentTurn = 0;
        this.numberOfPlayers = numberOfPlayers;

    }

    //TODO da rimuovere (c'è costruttore)? un giocatore che è eliminato è spettatore o esce?
    /**
     * sets a turnSequence.
     * @param turnSequence adds a [TURN_NUMBER, PLAYER] map representing for each player its position in the turn rotation
     */
    public void setTurnSequence(Map<Integer, Player> turnSequence) {
        this.turnSequence = turnSequence;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public String getCurrentPlayerUser() {
        return this.currentPlayer.getUsername();
    }

    private void nextTurn() {
        if (this.currentTurn == numberOfPlayers - 1) { //Ri inizia il giro
            this.currentTurn = 0;

        } else {
            this.currentTurn++;
        }
        currentPlayer = turnSequence.get(currentTurn);

        //imposta il turno alla prima fase

    }

    public void nextPhase (){
        currentPhase = currentPhase.next();
        if (currentPhase == END) {
            nextTurn();
        }
    }

    public TurnPhases getCurrentPhase() {
        return currentPhase;
    }


// TODO SAREBBE DA FARE NEL CONTROLLER? SERVE USER INPUT
    //OPPURE TIENE SOLO TRACCIA DEI TURNI in c
    /*
    public void handleCurrentTurn() {
        Card currentCard = currentPlayer.getCard();
        while (currentPhase != END) {
            switch (currentPhase) {
                //in prometheus
                case PRE_BUILD:
                    String playingWith = (String) currentCard.getClass().toString(); // 🐖
                    //TODO da ampliare
                    if (playingWith == )
                case MOVE:
                    if (currentPlayer.getBehaviour().getMovementsRemaining()==0) {
                        nextPhase();
                    }

                    System.out.println("DEBUG: " + currentPlayer.getUsername() + " in phase " + MOVE.toString());
                    break;
                case BUILD:



                    nextPhase();

                    break;
                case MOVE_SECOND_TIME:
                    if (currentPlayer.getBehaviour().getMovementsRemaining()==0) {
                        nextPhase();
                    }
                    break;
                case CHECK_IF_MOVED_ONTO_DIFF_CELL:
                    break;
            }
        }

        if (currentPhase == END) {
            System.out.println("DEBUG: turno finito");
            nextPhase();
        } */






}
