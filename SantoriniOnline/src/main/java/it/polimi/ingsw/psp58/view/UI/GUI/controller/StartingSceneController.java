package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.event.gameEvents.lobby.VC_ConnectionRequestGameEvent;
import it.polimi.ingsw.psp58.networking.client.SantoriniClient;
import it.polimi.ingsw.psp58.view.UI.CLI.CLIView;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;

public class StartingSceneController {
    private GUI gui;

    //LOGIN ELEMENTS
    @FXML
    private  TextField ipField = null;
    @FXML
    private  TextField userField = null;
    @FXML
    private  Button connectButton = null;
    @FXML
    private  Text loadText = null;
    @FXML
    private  ProgressBar loadProgress = null;


    public void start() throws IOException {

        loadText.setText("");
    }

    public void close() {
        System.exit(1);
    }

    private void disableAllLoginFields() {
        connectButton.setDisable(true);
        ipField.setDisable(true);
        userField.setDisable(true);
    }

    public void enableAllLoginFields() {
        connectButton.setDisable(false);
        ipField.setDisable(false);
        userField.setDisable(false);
    }

    public void complete () {
        loadProgress.setProgress(1);
    }
    private String updateLoadText(String text) {
        return "< " + text.toLowerCase() + " >";
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public void onClickEventConnectButton() {
        loadText.setText(updateLoadText("handling connection request"));

        loadProgress.setProgress(0.15f);
        disableAllLoginFields();
        loadProgress.setProgress(0.17f);

        String userProposal = userField.getText().toLowerCase();
        //TODO estrudere questi 2 metodi dalla cli dato che servono ovunque
        boolean localUserIsValid = CLIView.checkLocalUsernameAlphaNumeric(userProposal);
        boolean localIpIsValid = CLIView.checkValidIP(ipField.getText());

        if (localIpIsValid && localUserIsValid) {
            tryConnection(userProposal);
            gui.setUsername(userProposal.toLowerCase());
        }
    }

    private void tryConnection(String userProposal) {
        //set up the client
        loadText.setText(updateLoadText("establishing connection"));
        SantoriniClient client = new SantoriniClient(gui, ipField.getText(), gui.isPingEnabled());
        client.begin();
        gui.setClient(client);
        VC_ConnectionRequestGameEvent req = new VC_ConnectionRequestGameEvent("connection attempt", "--", 0, userProposal);
        client.sendEvent(req);
        loadProgress.setProgress(0.5f);
        new Thread(client).start();
    }
}
