package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.event.gameEvents.lobby.VC_ConnectionRequestGameEvent;
import it.polimi.ingsw.psp58.networking.client.ClientSocket;
import it.polimi.ingsw.psp58.view.UI.CLI.CLI;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * The controller of the starting scene, the scene where the client creates the connection with the server
 */
public class StartingSceneController {
    private GUI gui;

    //LOGIN ELEMENTS
    @FXML
    private TextField ipField;
    @FXML
    private TextField userField;
    @FXML
    private Button connectButton;
    @FXML
    private Text loadText;
    @FXML
    private ProgressBar loadProgress;

    @FXML
    private RadioButton localhostButton;
    @FXML
    private RadioButton onlineServerButton;
    @FXML
    private RadioButton customIPButton;
    @FXML
    private VBox customVBox;
    public Text versionText;

    private String selectedIP;
    private boolean customIP;

    /**
     *
     * @throws IOException IOException
     */
    public void start() throws IOException {
        loadText.setText("");
        loadProgress.setVisible(false);
        loadText.setVisible(false);

        selectedIP = "127.0.0.1";
        customIP = false;
        customVBox.setDisable(true);

        localhostButton.selectedProperty().setValue(true);
        onlineServerButton.selectedProperty().setValue(false);
        customIPButton.selectedProperty().setValue(false);
        versionText.setText("Santorini Online - v." + gui.getGameVersion());
    }

    /**
     * Disables all the fields for the user input
     */
    private void disableAllLoginFields() {
        connectButton.setDisable(true);
        ipField.setDisable(true);
        userField.setDisable(true);
    }

    /**
     * Enables all the fields for the user input
     */
    public void enableAllLoginFields() {
        connectButton.setDisable(false);
        ipField.setDisable(false);
        userField.setDisable(false);
        loadProgress.setProgress(0);
    }

    /**
     * Sets the progress bar to complete
     */
    public void complete() {
        loadProgress.setProgress(1);
    }

    /**
     * Creates the text that will be set under the progress bar
     * @param text the string containing the message
     * @return the message that will be set under the progress bar
     */
    private String updateLoadText(String text) {
        return "< " + text.toLowerCase() + " >";
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    /**
     * Called by the click on the connect button, if the information are it sends the {@link VC_ConnectionRequestGameEvent}, otherwise it shows an error message
     */
    public void onClickEventConnectButton() {
        if (customIP) {
            selectedIP = ipField.getText();
        }
        loadProgress.setVisible(true);
        loadText.setText(updateLoadText("Handling connection request"));
        loadText.setVisible(true);

        loadProgress.setProgress(0.15f);
        disableAllLoginFields();
        loadProgress.setProgress(0.17f);

        String userProposal = userField.getText().toLowerCase();

        boolean localUserIsValid = CLI.checkLocalUsernameAlphaNumeric(userProposal);
        boolean localIpIsValid = CLI.checkValidIP(selectedIP);

        if (localIpIsValid && localUserIsValid) {
            try {
                tryConnection(userProposal);
            } catch (IOException e) {
                gui.showError("Unable to reach the server.");
                enableAllLoginFields();
            }
            gui.setUsername(userProposal.toLowerCase());
        }
        else {

            if(!localIpIsValid) {
                gui.showError("Local IP is invalid");
            }
            else {
                gui.showError("Username is invalid, only alphanumeric chars");
            }
            enableAllLoginFields();
        }
    }

    /**
     * Initialize the {@link ClientSocket} and the connection with the server then sends a {@link VC_ConnectionRequestGameEvent} to the server an
     * @param userProposal the username chosen by the player
     * @throws IOException if it fails to create and initialize the {@link ClientSocket} and the connection with the server
     */
    private void tryConnection(String userProposal) throws IOException {
        //set up the client
        loadText.setText(updateLoadText("establishing connection"));
        ClientSocket client = new ClientSocket(gui, selectedIP, gui.isPingEnabled(), gui);
        client.begin();
        gui.setClient(client);
        VC_ConnectionRequestGameEvent req = new VC_ConnectionRequestGameEvent("connection attempt", "--", 0, userProposal);
        client.sendEvent(req);
        loadProgress.setProgress(0.5f);
        new Thread(client).start();
    }

    /**
     * Sets the ip address to the localhost and disables the {@code ipField}
     */
    public void onClickLocalhostButton() {
        selectedIP = "127.0.0.1";
        customIP = false;
        customVBox.setDisable(true);

        onlineServerButton.selectedProperty().setValue(false);
        customIPButton.selectedProperty().setValue(false);
    }

    /**
     * Sets the ip address to the online AWS server get by the {@code getOnlineServerIP} from the {@link GUI} class and disables the {@code ipField}
     */
    public void onClickOnlineServerButton() {
        selectedIP = gui.getOnlineServerIP();
        customIP = false;
        customVBox.setDisable(true);

        localhostButton.selectedProperty().setValue(false);
        customIPButton.selectedProperty().setValue(false);
    }

    /**
     * Enables the {@code ipField} for input from the user
     */
    public void onClickCustomIPButton() {
        selectedIP = "";
        customIP = true;
        customVBox.setDisable(false);

        localhostButton.selectedProperty().setValue(false);
        onlineServerButton.selectedProperty().setValue(false);
    }
}
