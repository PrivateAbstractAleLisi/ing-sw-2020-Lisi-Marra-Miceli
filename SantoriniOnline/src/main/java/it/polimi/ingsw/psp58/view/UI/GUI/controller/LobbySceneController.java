package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.event.gameEvents.lobby.CV_RoomUpdateGameEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.*;

public class LobbySceneController {
    @FXML
    private Label roomPlayersNumberText;
    @FXML
    private Label roomTitleText;

    @FXML
    private Label player1Text, player2Text, player3Text;

    private int numberOfPlayers = 0;
    private int roomSize = 0;

    public void update(CV_RoomUpdateGameEvent event) {
        numberOfPlayers = event.getUsersInRoom().length;
        roomSize = event.getRoomSize();
        List<Label> userLabels;
        Queue<String> usersQueue = new LinkedList<>(Arrays.asList(event.getUsersInRoom()));
        userLabels = fillUsernameLabelsList();

        //set roomName
        roomTitleText.setText(event.getRoomName().toUpperCase());

        //update player counter and room size
        roomPlayersNumberText.setText(numberOfPlayers + "/" + roomSize);
        for (int i = 0; i < 3; i++) {
            if (!usersQueue.isEmpty()) {
                userLabels.get(i).setVisible(true);
                userLabels.get(i).setText(usersQueue.remove().toUpperCase());
            } else {
                userLabels.get(i).setText("");
                userLabels.get(i).setVisible(false);
            }
        }
    }

    private List<Label> fillUsernameLabelsList() {
        return new ArrayList<>() {
            {
                add(player1Text);
                add(player2Text);
                add(player3Text);
            }
        };
    }
}
