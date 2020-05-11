package it.polimi.ingsw.psp58.event.core;


import it.polimi.ingsw.psp58.event.gameEvents.CV_GameErrorGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.PlayerDisconnectedViewEvent;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.*;
import it.polimi.ingsw.psp58.event.gameEvents.match.*;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.*;

public interface ViewListener extends EventListener{

    void handleEvent(CV_GameErrorGameEvent event);

    void handleEvent(PlayerDisconnectedViewEvent event);

    //LOBBY EVENT

    void handleEvent(CV_ConnectionRejectedErrorGameEvent event);

    void handleEvent(CV_ReconnectionRejectedErrorGameEvent event);

    void handleEvent(CV_NewGameRequestEvent event);

    void handleEvent(CV_RoomSizeRequestGameEvent event);

    void handleEvent(CV_RoomUpdateGameEvent event);

    //PREMATCH
    void handleEvent(CV_CardChoiceRequestGameEvent event);

    void handleEvent(CV_ChallengerChooseFirstPlayerRequestEvent event);

    void handleEvent(CV_PlayerPlaceWorkerRequestEvent event);

    void handleEvent(CV_ChallengerChosenEvent event);

    void handleEvent(CV_WaitPreMatchGameEvent event);

    //MATCH
    void handleEvent(CV_CommandRequestEvent event);

    void handleEvent(CV_GameOverEvent event);

    void handleEvent(CV_GameStartedGameEvent event);

    void handleEvent(CV_NewTurnEvent event);

    void handleEvent(CV_IslandUpdateEvent event);

    void handleEvent(CV_WaitMatchGameEvent event);

}
