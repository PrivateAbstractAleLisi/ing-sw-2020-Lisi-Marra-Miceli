package controller;

import event.core.EventListener;
import event.core.EventSource;
import event.events.*;
import model.BoardManager;
import model.CardEnum;
import model.exception.InvalidCardException;

import javax.naming.LimitExceededException;
import java.util.*;

import static event.core.ListenerType.VIEW;

public class PreGameController extends EventSource implements EventListener {
    private BoardManager boardManager;
    private String challenger;
    private Room room;
    private Map<String, CardEnum> playersCardsCorrespondence;
    private List<String> turnSequence;

    public PreGameController(BoardManager boardManager, Room room) {
        this.boardManager = boardManager;
        this.room = room;
        this.playersCardsCorrespondence = new HashMap<String, CardEnum>();
        this.turnSequence = new ArrayList<String>();
    }

    public void start() {
        chooseChallenger();
    }

    public void chooseChallenger() {
        //choose the challenger in a random way
        Random random = new Random();
        int number = random.nextInt(room.getSIZE()) + 1;
        challenger = room.getActiveUsers()[number];
        ChallengerChosenEvent event = new ChallengerChosenEvent(challenger, room.getSIZE());
        notifyAllObserverByType(VIEW, event);
        // needed the external attach to the listener
    }

    public Map<String, CardEnum> getPlayersCardsCorrespondence() {
        return playersCardsCorrespondence;
    }

    public List<String> getTurnSequence() {
        return turnSequence;
    }


    @Override
    public void handleEvent(ChallengerCardsChosenEvent event) {
        List<CardEnum> cardsChosen = event.getCardsChosen();
        for (CardEnum card : cardsChosen) {
            try {
                boardManager.selectCard(card);
            } catch (InvalidCardException e) {

            } catch (LimitExceededException e) {

            }
        }
    }

    @Override
    public void handleEvent(PlayerCardChosenEvent event) {
        try {
            boardManager.takeCard(event.getCard());
            playersCardsCorrespondence.put(event.getPlayer(), event.getCard());
        } catch (InvalidCardException e) {

        }
    }

    @Override
    public void handleEvent(ChallengerChosenFirstPlayerEvent event) {
        boardManager.setPlayersCardsCorrespondence(playersCardsCorrespondence);
        String firstPlayer = event.getUsername();
        turnSequence.add(firstPlayer);
        String[] players = room.getActiveUsers();

        //create the turnSequence based on the story
        for (int i = 0; i < players.length; i++) {
            if (players[i].equals(firstPlayer)) {
                if (i == players.length - 1) { //the player chosen is the last in the list of the room
                    int j = 0;
                    while (j < players.length - 1) {
                        turnSequence.add(players[j]);
                        j++;
                    }
                } else { // the player is not the last in the list of the room
                    int j = i;
                    while (j < players.length - 1) { //adds all the player past the chosen player
                        turnSequence.add(players[j]);
                        j++;
                    }
                    if (turnSequence.size() < players.length) { // if there are, adds all the player before the chosen one
                        j = 0;
                        while (j < i) {
                            turnSequence.add(players[j]);
                            j++;
                        }
                    }
                }
            }
        }
    }

    @Override //NO IMPL
    public void handleEvent(ChallengerChosenEvent event) {
        return;
    }

    @Override
    public void handleEvent(GameEvent event) {
        throw new RuntimeException("");
    }

    @Override
    public void handleEvent(RoomSizeResponseGameEvent event) {
        throw new RuntimeException("");
    }

    @Override
    public void handleEvent(RoomUpdateGameEvent event) {

    }

    @Override
    public void handleEvent(ConnectionRequestGameEvent event) {
        throw new RuntimeException("");
    }

    @Override
    public void handleEvent(ConnectionRequestServerGameEvent event) {

    }

    @Override
    public void handleEvent(RoomSizeRequestGameEvent event) {
        throw new RuntimeException("");
    }

    @Override
    public void handleEvent(ConnectionRejectedErrorGameEvent event) {
        throw new RuntimeException("");
    }



}
