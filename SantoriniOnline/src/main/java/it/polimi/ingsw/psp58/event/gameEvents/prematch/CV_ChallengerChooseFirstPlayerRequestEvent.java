package it.polimi.ingsw.psp58.event.gameEvents.prematch;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.model.CardEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CV_ChallengerChooseFirstPlayerRequestEvent extends ViewGameEvent {

    private final String challenger;
    private final List<String> players;
    private final Map<String, CardEnum> cardChosen;

    public CV_ChallengerChooseFirstPlayerRequestEvent(String description, String challenger, List<String> players) {
        super(description);
        this.challenger = challenger;
        this.players = players;
        this.cardChosen = new HashMap<>();
        for (String username : players) {
            cardChosen.put(username, CardEnum.PAN);
        }
    }

    public CV_ChallengerChooseFirstPlayerRequestEvent(String description, String challenger, List<String> players, Map<String, CardEnum> cardChosen) {
        super(description);
        this.challenger = challenger;
        this.players = players;
        this.cardChosen = cardChosen;
    }

    public String getChallenger() {
        return challenger;
    }

    public List<String> getPlayers() {
        return players;
    }

    public Map<String, CardEnum> getCardChosen() {
        return cardChosen;
    }

    public CardEnum cardChosenByPlayer(String username) {
        return cardChosen.get(username);
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }
}
