package it.polimi.ingsw.psp58.event.core;

import it.polimi.ingsw.psp58.event.PlayerDisconnectedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.*;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.*;
import it.polimi.ingsw.psp58.event.gameEvents.match.*;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.*;

/**
 * @author Alessandro Lisi
 * <p>
 * A listener that gets notified when an EventSource object status is changed or when it generates a GameEvent
 */
public interface EventListener {

    /* one handler for each it.polimi.ingsw.sp58.event class */
    void handleEvent(GameEvent event);

    void handleEvent(CV_GameErrorGameEvent event);

    //LOBBY EVENT
    void handleEvent(CC_ConnectionRequestGameEvent event);

    void handleEvent(CV_ConnectionRejectedErrorGameEvent event);

    void handleEvent(CV_RoomSizeRequestGameEvent event);

    void handleEvent(CV_RoomUpdateGameEvent event);

    void handleEvent(VC_ConnectionRequestGameEvent event);

    void handleEvent(VC_RoomSizeResponseGameEvent event);


    //PREMATCH
    void handleEvent(CV_CardChoiceRequestGameEvent event);

    void handleEvent(CV_ChallengerChooseFirstPlayerRequestEvent event);

    void handleEvent(CV_PlayerPlaceWorkerRequestEvent event);

    void handleEvent(CV_ChallengerChosenEvent event);

    void handleEvent(CV_WaitPreMatchGameEvent event);

    void handleEvent(VC_ChallengerCardsChosenEvent event);

    void handleEvent(VC_ChallengerChosenFirstPlayerEvent event);

    void handleEvent(VC_PlayerCardChosenEvent event);

    void handleEvent(VC_PlayerPlacedWorkerEvent event);


    //MATCH
    void handleEvent(CV_CommandRequestEvent event);

    void handleEvent(CV_GameOverEvent event);

    void handleEvent(CV_GameStartedGameEvent event);

    void handleEvent(CV_NewTurnEvent event);

    void handleEvent(CV_IslandUpdateEvent event);

    void handleEvent(CV_WaitMatchGameEvent event);

    void handleEvent(VC_PlayerCommandGameEvent event);

    void handleEvent(PlayerDisconnectedGameEvent event);


}

