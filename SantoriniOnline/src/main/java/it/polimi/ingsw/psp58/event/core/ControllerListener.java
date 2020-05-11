package it.polimi.ingsw.psp58.event.core;

import it.polimi.ingsw.psp58.event.gameEvents.lobby.*;
import it.polimi.ingsw.psp58.event.gameEvents.match.VC_PlayerCommandGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_ChallengerCardsChosenEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_ChallengerChosenFirstPlayerEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_PlayerCardChosenEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_PlayerPlacedWorkerEvent;

public interface ControllerListener extends EventListener {

    //LOBBY EVENT

    void handleEvent(CC_ConnectionRequestGameEvent event);

    void handleEvent(CC_NewGameResponseEvent event);

    void handleEvent(VC_ConnectionRequestGameEvent event);

    void handleEvent(VC_NewGameResponseEvent event);

    void handleEvent(VC_RoomSizeResponseGameEvent event);

    //PREMATCH

    void handleEvent(VC_ChallengerCardsChosenEvent event);

    void handleEvent(VC_ChallengerChosenFirstPlayerEvent event);

    void handleEvent(VC_PlayerCardChosenEvent event);

    void handleEvent(VC_PlayerPlacedWorkerEvent event);

    //MATCH

    void handleEvent(VC_PlayerCommandGameEvent event);

}
