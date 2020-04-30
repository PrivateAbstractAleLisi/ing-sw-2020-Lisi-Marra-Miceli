package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.event.core.ListenerType;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.VC_ConnectionRequestGameEvent;
import it.polimi.ingsw.psp58.networking.client.SantoriniClient;
import it.polimi.ingsw.psp58.view.UI.GUI.Message;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ConnectionSceneController {

    private GUI gui;
    @FXML
    private TextField ipAddress;

    @FXML
    private TextField username;

    public void connect(){

        System.out.println("IP Address : " + ipAddress.getText() + "\n Username: " +  username.getText());
        if (ipAddress.getText().matches("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")){
            if (username.getText().matches("^[a-zA-Z0-9]+$") && username.getText().length()>3 && username.getText().length()<15){
                //set up the information in the gui
                gui.setUsername(username.getText());
                gui.setChosenIp(ipAddress.getText());

                //set up the client and starts it
                SantoriniClient client = new SantoriniClient(gui, ipAddress.getText());
                gui.setClient(client);
                client.begin();

                //send the event to the server
                VC_ConnectionRequestGameEvent event = new VC_ConnectionRequestGameEvent("connection request", ipAddress.getText(), gui.getSocketPort() ,username.getText());
                gui.sendEvent(event);

                new Thread(client).start();
            }
            else {
                Message.show("Username must be at least 3 alpha numeric characters!");
            }
        }
        else {
            Message.show("Invalid IP Address!");
        }
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }
}
