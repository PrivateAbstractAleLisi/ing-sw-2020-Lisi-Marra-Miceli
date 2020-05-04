package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.event.PlayerDisconnectedGameEvent;
import it.polimi.ingsw.psp58.event.core.EventListener;
import it.polimi.ingsw.psp58.event.gameEvents.CV_GameErrorGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.*;
import it.polimi.ingsw.psp58.event.gameEvents.match.*;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.*;
import it.polimi.ingsw.psp58.networking.client.SantoriniClient;
import it.polimi.ingsw.psp58.view.UI.CLI.CLIView;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Time;

public class StartingSceneController {
    private GUI gui;

    //LOGIN ELEMENTS
    public TextField ipField = null;
    public TextField userField = null;
    public Button connectButton = null;
    public Text loadText = null;
    public void start() throws IOException {
        gui.changeScene(gui.getConnectionScene());
        loadText = null;
    }
    public void close(){
        System.exit(1);
    }

    private void disableAllLoginFields () {
        connectButton.setDisable(true);
        ipField.setDisable(true);
        userField.setDisable(true);
    }

    public void enableAllLoginFields () {
        connectButton.setDisable(false);
        ipField.setDisable(false);
        userField.setDisable(false);
    }


    private String updateLoadText(String text) {
        return "< " + text.toLowerCase() + " >";
    }
    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public void onClickEventConnectButton(MouseEvent mouseEvent) {

        loadText.setText(updateLoadText("handling connection request"));
        disableAllLoginFields();
        String userProposal = userField.getText().toLowerCase();
        //TODO estrudere questi 2 metodi dalla cli dato che servono ovunque
        boolean localUserIsValid = CLIView.checkLocalUsernameAlphaNumeric(userProposal);
        boolean localIpIsValid = CLIView.checkValidIP(ipField.getText());


        if (localIpIsValid && localUserIsValid) {
            tryConnection(userProposal);
        }




    }

    private void tryConnection(String userProposal) {
        //set up the client
        loadText.setText(updateLoadText("establishing connection"));
        SantoriniClient client = new SantoriniClient(gui, ipField.getText());
        client.begin();
        VC_ConnectionRequestGameEvent req = new VC_ConnectionRequestGameEvent("connection attempt", "--", 0, userProposal);
        client.sendEvent(req);
        new Thread(client).start();
    }
}
