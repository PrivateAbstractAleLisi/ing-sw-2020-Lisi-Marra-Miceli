package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.event.gameEvents.lobby.VC_ConnectionRequestGameEvent;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ConnectionSceneController {

    private GUI gui;
    @FXML
    private TextField ipAddress;

    @FXML
    private TextField username;

    public void connect(){
        gui.setUsername(username.getText());
        gui.setChosenIp(ipAddress.getText());
        System.out.println("IP Address : " + ipAddress.getText() + "\n Username: " +  username.getText());
//        gui.getClient().setIP(ipAddress.getText());
        VC_ConnectionRequestGameEvent event = new VC_ConnectionRequestGameEvent("", ipAddress.getText(), gui.getSocketPort() ,username.getText());
//        gui.sendEvent(event);
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }
}
