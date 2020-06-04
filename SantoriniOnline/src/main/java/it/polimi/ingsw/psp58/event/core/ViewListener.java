package it.polimi.ingsw.psp58.event.core;


import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_PreGameStartedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_GameErrorGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.CV_PreGameErrorGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.connection.PlayerDisconnectedViewEvent;
import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_GameStartedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.*;
import it.polimi.ingsw.psp58.event.gameEvents.match.*;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.*;
import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_WorkerPlacementGameEvent;

public interface ViewListener extends EventListener {

    void handleEvent(PlayerDisconnectedViewEvent event);

    //LOBBY EVENT
    void handleEvent(CV_ConnectionRejectedErrorGameEvent event);

    void handleEvent(CV_ReconnectionRejectedErrorGameEvent event);

    void handleEvent(CV_NewGameRequestEvent event);

    void handleEvent(CV_RoomSizeRequestGameEvent event);

    void handleEvent(CV_RoomUpdateGameEvent event);


    //PREMATCH
    void handleEvent(CV_PreGameStartedGameEvent event);

    void handleEvent(CV_PreGameErrorGameEvent event);

    void handleEvent(CV_CardChoiceRequestGameEvent event);

    void handleEvent(CV_ChallengerChooseFirstPlayerRequestEvent event);

    void handleEvent(CV_PlayerPlaceWorkerRequestEvent event);

    void handleEvent(CV_ChallengerChosenEvent event);

    void handleEvent(CV_WaitPreMatchGameEvent event);

    //MATCH

    void handleEvent(CV_GameStartedGameEvent event);

    void handleEvent(CV_GameErrorGameEvent event);

    void handleEvent(CV_WorkerPlacementGameEvent event);

    void handleEvent(CV_CommandExecutedGameEvent event);

    void handleEvent(CV_CommandRequestEvent event);

    void handleEvent(CV_GameOverEvent event);

    void handleEvent(CV_NewTurnEvent event);

    void handleEvent(CV_IslandUpdateEvent event);

    void handleEvent(CV_WaitMatchGameEvent event);

    void handleEvent(CV_TurnInfoEvent CVTurnInfoEvent);
}
