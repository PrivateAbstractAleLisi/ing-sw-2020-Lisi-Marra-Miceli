package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import com.google.gson.Gson;
import it.polimi.ingsw.psp58.auxiliary.IslandData;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.CV_PlayerPlaceWorkerRequestEvent;
import it.polimi.ingsw.psp58.view.UI.CLI.utility.IslandUtility;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.BoardSceneController;

public class PlaceWorkerGameState extends GameStateAbs {
    CV_PlayerPlaceWorkerRequestEvent eventArrived;
    public PlaceWorkerGameState(CV_PlayerPlaceWorkerRequestEvent eventArrived) {
        this.eventArrived = eventArrived;
    }


    public void setState(BoardSceneController b) {
        b.updateIsland(islandDataFromJson(eventArrived.getIsland()));
        b.handleWorkerPlacement(eventArrived.getWorkerToPlace());

    }

    private IslandData islandDataFromJson(String islaJson) {
        //display island
        Gson gson = new Gson();
        final IslandData isla = (IslandData) gson.fromJson(islaJson, IslandData.class);

        return isla;

    }

}
