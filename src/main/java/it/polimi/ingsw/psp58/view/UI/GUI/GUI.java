package it.polimi.ingsw.psp58.view.UI.GUI;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.connection.PlayerDisconnectedViewEvent;
import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_GameStartedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_PreGameStartedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_SpectatorGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_WorkerPlacementGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.*;
import it.polimi.ingsw.psp58.event.gameEvents.match.*;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.*;
import it.polimi.ingsw.psp58.networking.client.ClientSocket;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;

/**
 * Main GUI class with JavaFX scenes and controllers.
 */
public class GUI extends Application implements ViewListener {
    private Stage stage;

    private static final int SOCKET_PORT = 7557;
    private static final String GAME_VERSION = "2.0.0";
    private static final String ONLINE_SERVER_IP = "23.23.52.127";

    private boolean enablePing = true;
    private boolean alreadyDisconnectedRecently = false;

    private String username;

    private ClientSocket client;

    private Scene startingScene;
    private StartingSceneController startingSceneController;

    private Scene lobbyScene;
    private LobbySceneController lobbySceneController;

    private Scene roomSizeScene;
    private Stage roomSizeStage;
    private RoomSizeSceneController roomSizeController;

    private Scene boardScene;
    private BoardSceneController boardSceneController;

    private Scene preGameScene;
    private PreGameSceneController preGameSceneController;

    private Scene outcomeScene;
    private OutcomeSceneController outcomeSceneController;


    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * Shows the message passed as parameter to screen and return the {@code stage} to the {@code startingScene}
     *
     * @param message the string of the message to print
     */
    public void disconnectionHandle(String message) {
        if (!alreadyDisconnectedRecently) {
            showError("You've been disconnected from the server." + (message.isEmpty() ? "" : " " + message));

            restartStartingScene();
        }
    }


    /**
     * The first method called by the main one. Loads the {@code startingScene}, {@code startingSceneController},
     * {@code lobbyScene} and {@code lobbySceneController} and show the starting one.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        List<String> args = getParameters().getRaw();

        if (args != null && !args.isEmpty()) {
            for (String currentArgument : args) {
                if (currentArgument.equals("-ping off")) {
                    enablePing = false;
                }
            }
        }

        stage = primaryStage;
        stage.setResizable(false);

        setNewStartingScene();

        //set up the lobby scene and controller
        FXMLLoader lobbySceneLoader = new FXMLLoader(
                getClass().getResource("/scenes/LobbyScene.fxml"));
        lobbyScene = new Scene(lobbySceneLoader.load());
        lobbySceneController = lobbySceneLoader.getController();

        //starts with the startingScene
        stage.setTitle("Santorini Online");
        stage.setScene(startingScene);

        stage.show();
        stage.setOnCloseRequest(e -> closeApp());
    }

    private void setNewStartingScene() throws IOException {
        //set up the starting scene and controller
        FXMLLoader loaderStartingScene = new FXMLLoader(
                getClass().getResource("/scenes/StartingScene.fxml"));
        startingScene = new Scene(loaderStartingScene.load());

        startingSceneController = loaderStartingScene.getController();
        startingSceneController.setGui(this);
        startingSceneController.start();
    }

    /**
     * Restart the starting scene
     */
    public void restartStartingScene(){
        try {
            setNewStartingScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
        changeScene(startingScene);
    }

    @Override
    public void handleEvent(CV_RoomSizeRequestGameEvent event) {
        resetDisconnectionBoolean();
        try {
            prepareRoomSizeRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startingSceneController.complete();
    }

    /**
     * Load the {@code roomSizeScene} and {@code roomSizeSceneController}, creates a modal stage with that scene and shows it.
     *
     * @throws IOException if it fails to load the scene and the controller.
     */
    public void prepareRoomSizeRequest() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/scenes/RoomSizeScene.fxml"));
        roomSizeScene = new Scene(loader.load());
        roomSizeStage = new Stage();
        roomSizeStage.setScene(roomSizeScene);
        roomSizeStage.initModality(Modality.APPLICATION_MODAL);
        roomSizeStage.initStyle(StageStyle.UNDECORATED);
        roomSizeStage.resizableProperty().setValue(Boolean.FALSE);
        roomSizeController = loader.getController();
        roomSizeStage.initOwner(stage);
        roomSizeStage.show();

        roomSizeController.setGui(this);
    }

    /**
     * Sends the {@link VC_RoomSizeResponseGameEvent} containing the size of the room
     *
     * @param result the size of the room
     */
    public void roomSizeResponse(int result) {
        roomSizeStage.close();
        System.out.println(result);
        VC_RoomSizeResponseGameEvent responseEvent = new VC_RoomSizeResponseGameEvent("", result);
        sendEvent(responseEvent);
    }

    /**
     * Changes the actual scene of the stage to the passed as parameter
     *
     * @param scene the scene to be set on the primary stage
     */
    public void changeScene(Scene scene) {
        stage.setTitle("Santorini Online");
        stage.setScene(scene);
        stage.setResizable(scene.equals(preGameScene));
        stage.show();
    }

    /**
     * Loads the {@code preGameScene} and the {@code preGameSceneController}
     *
     * @throws IOException if it fails to load the scene or the controller
     */
    private void setNewPreGameScene() throws IOException {
        //set up the pregame scene and controller
        FXMLLoader preGameSceneLoader = new FXMLLoader(
                getClass().getResource("/scenes/PreGameScene.fxml"));
        preGameScene = new Scene(preGameSceneLoader.load());
        preGameSceneController = preGameSceneLoader.getController();
        preGameSceneController.setGui(this);
    }

    /**
     * Loads the {@code boardScene} and the {@code boardSceneController}
     *
     * @throws IOException if it fails to load the scene or the controller
     */
    private void setNewBoardGameScene() throws IOException {
        //set up the board scene and controller
        FXMLLoader boardLoader = new FXMLLoader(
                getClass().getResource("/scenes/BoardScene.fxml"));
        boardScene = new Scene(boardLoader.load());
        boardSceneController = boardLoader.getController();
        boardSceneController.setGui(this);
    }

    /**
     * Loads the {@code outcomeScene} and the {@code outcomeSceneController}
     *
     * @throws IOException if it fails to load the scene or the controller
     */
    private void setOutcomeScene() throws IOException {
        //set up the starting scene and controller
        FXMLLoader outcomeSceneLoader = new FXMLLoader(
                getClass().getResource("/scenes/OutcomeScene.fxml"));
        outcomeScene = new Scene(outcomeSceneLoader.load());

        outcomeSceneController = outcomeSceneLoader.getController();
    }

    /**
     * Send the event {@link ControllerGameEvent} passed as parameter using the {@code sendEvent} method on the {@link ClientSocket}
     *
     * @param event the event that needs to be sent
     */
    public void sendEvent(ControllerGameEvent event) {
        client.sendEvent(event);
    }

    /**
     * Sets the local username of the player
     *
     * @param username the name of the local player
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the name of the local player
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the local {@link ClientSocket} instance of the player
     *
     * @param client the client of the local player
     */
    public void setClient(ClientSocket client) {
        this.client = client;
    }

    /**
     * @return the ClientSocket
     */
    public ClientSocket getClient() {
        return client;
    }

    /**
     * @return true if the client sends ping to the server, false otherwise
     */
    public boolean isPingEnabled() {
        return enablePing;
    }


    public int getSocketPort() {
        return SOCKET_PORT;
    }

    /**
     * @return the {@code startingScene}
     */
    public Scene getStartingScene() {
        return startingScene;
    }

    /**
     * @return the {@code startingSceneController}
     */
    public StartingSceneController getStartingSceneController() {
        return startingSceneController;
    }

    /**
     * @return the {@code stage}
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * @return the version of the game
     */
    public String getGameVersion() {
        return GAME_VERSION;
    }

    /**
     * Sets the {@code alreadyDisconnectedRecently} to false
     */
    public void resetDisconnectionBoolean() {
        alreadyDisconnectedRecently = false;
    }

    /**
     * Handles the {@link CV_ConnectionRejectedErrorGameEvent} showing a {@link ErrorPopUp} message
     */
    @Override
    public void handleEvent(CV_ConnectionRejectedErrorGameEvent event) {

        getStartingSceneController().enableAllLoginFields();
        //notify the error on screen
        //Debug error pop up
        new ErrorPopUp().show(event.getErrorMessage(), stage);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Show a {@link ErrorPopUp} with the error message
     */
    @Override
    public void handleEvent(CV_ReconnectionRejectedErrorGameEvent event) {
        showError("Please wait few seconds and retry.");
    }

    /**
     * Show the error message passed through parameter with a {@link ErrorPopUp}
     *
     * @param message the string to display as a error message
     */
    public void showError(String message) {
        new ErrorPopUp().show(message, stage);
    }

    /**
     * Shows the error message related to the game phase
     * @param event the event received from the server containing the error to display
     */
    @Override
    public void handleEvent(CV_GameErrorGameEvent event) {
        showError(event.getEventDescription());
    }

    /**
     * Handles the {@link PlayerDisconnectedViewEvent} calling the {@code disconnectionHandle} method 
     * @param event {@link PlayerDisconnectedViewEvent} saying that a player has disconnected from the server
     */
    @Override
    public void handleEvent(PlayerDisconnectedViewEvent event) {
        //Message.show(event.getDisconnectedUsername() + event.getReason(), stage); old message pop up
        disconnectionHandle(event.getDisconnectedUsername() + event.getReason());
        alreadyDisconnectedRecently = true;
    }

    /**
     * Show to the player the room is in changing the scene to the {@code lobbyScene}
     * @param event the {@link CV_RoomUpdateGameEvent} just received by the server containing the actual room the player is in
     */
    @Override
    public void handleEvent(CV_RoomUpdateGameEvent event) {
        resetDisconnectionBoolean();
        System.out.println("Room Update received");
        lobbySceneController.update(event);
        if (!stage.getScene().equals(lobbyScene)) {
            changeScene(lobbyScene);
        }
    }

    /**
     * Handles the event passed as parameter calling the {@code update} method on the {@link PreGameSceneController}
     *
     * @param event the {@link CV_PreGameStartedGameEvent} just received by the server saying that the pregame phase has started
     */
    @Override
    public void handleEvent(CV_PreGameStartedGameEvent event) {
        System.out.println("Pregame started, Challenger: " + event.getChallenger());

        try {
            setNewPreGameScene();
        } catch (IOException e) {
            e.printStackTrace();
        }

        preGameSceneController.update(event);

        stage.setResizable(true);
        changeScene(preGameScene);
    }

    /**
     * Show an {@link ErrorPopUp} with the error contained in the event
     *
     * @param event the {@link CV_PreGameErrorGameEvent} just received by the server with an error message during the pregame phase
     */
    @Override
    public void handleEvent(CV_PreGameErrorGameEvent event) {
        new ErrorPopUp().show("ERROR - OPTION NOT VALID", stage);
    }

    /**
     * Handles the event passed as parameter calling the {@code update} method on the {@link PreGameSceneController}
     *
     * @param event the {@link CV_CardChoiceRequestGameEvent} just received by the server saying that this client has to choice his card
     */
    @Override
    public void handleEvent(CV_CardChoiceRequestGameEvent event) {
        System.out.println("I have to choose my card!");
        preGameSceneController.update(event);
    }

    /**
     * Handles the event passed as parameter calling the {@code update} method on the {@link PreGameSceneController}
     *
     * @param event the {@link CV_ChallengerChooseFirstPlayerRequestEvent} just received by the server saying that the challenger has chosen the first player that has to start
     */
    @Override
    public void handleEvent(CV_ChallengerChooseFirstPlayerRequestEvent event) {
        System.out.println("Choose the first player!");
        preGameSceneController.update(event);
    }

    /**
     * Handles the event passed as parameter calling the {@code handleEvent} method on the {@link BoardSceneController}
     *
     * @param event the {@link CV_PlayerPlaceWorkerRequestEvent} just received by the server saying that this client has to place a worker
     */
    @Override
    public void handleEvent(CV_PlayerPlaceWorkerRequestEvent event) {
        boardSceneController.handle(event);
    }

    /**
     * Handles the event passed as parameter calling the {@code update} method on the {@link PreGameSceneController}
     *
     * @param event the {@link CV_ChallengerChosenEvent} just received by the server saying that this client is the challenger
     */
    @Override
    public void handleEvent(CV_ChallengerChosenEvent event) {
        System.out.println("I'm the challenger");
        preGameSceneController.update(event);
    }

    /**
     * Handles the event passed as parameter calling the {@code update} method on the {@link PreGameSceneController} or the {@code handleEvent} on the {@link BoardSceneController}
     *
     * @param event the {@link CV_ChallengerChosenEvent} just received by the server saying that this client is the challenger
     */
    @Override
    public void handleEvent(CV_WaitPreMatchGameEvent event) {
        System.out.println("Wait received");
        preGameSceneController.update(event);
        if (!stage.getScene().equals(boardScene)) {
            changeScene(preGameScene);
        } else if (stage.getScene().equals(boardScene)) {
            boardSceneController.handle(event);
        }
    }

    /**
     * Called when it's time to switch to board scene, locks view for everyone.
     * Initialize the {@link BoardSceneController} instance and change the actual scene to the {@code boardSceneController}
     * @param event the {@link CV_WorkerPlacementGameEvent} just received by the server saying that the worker placement is started
     */
    /* called when it's time to switch to board scene, locks view for everyone */
    @Override
    public void handleEvent(CV_WorkerPlacementGameEvent event) {
        try {
            setNewBoardGameScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("DEBUG: worker placement update event has arrived");
        boardSceneController.handle(event);
        boardSceneController.init(event);
        changeScene(boardScene);
        stage.setResizable(false);
    }

    /**
     * Handles the event passed as parameter calling the {@code handleEvent} method on the {@link BoardSceneController}
     *
     * @param event the {@link CV_CommandRequestEvent} just received by the server saying that the last command sent was successfully executed
     */
    @Override
    public void handleEvent(CV_CommandExecutedGameEvent event) {
        boardSceneController.handle(event);
    }

    /**
     * Handles the event passed as parameter calling the {@code handleEvent} method on the {@link BoardSceneController}
     *
     * @param event the {@link CV_CommandRequestEvent} just received by the server containing the possible action the client
     * can perform
     */
    @Override
    public void handleEvent(CV_CommandRequestEvent event) {
        boardSceneController.handle(event);
    }

    /**
     * Handles the event that the client receives when the game is over, shows the {@code outcomeScene}
     *
     * @param event the event received when the game is over
     */
    @Override
    public void handleEvent(CV_GameOverEvent event) {
        if (event.getWinner() != null) {
            //someone won
            System.out.println("DEBUG: game is over, loading outcome scene.");
            boardSceneController.setWaitingView();

            try {
                setOutcomeScene();
                outcomeSceneController.initAndFillWinner(event.getWinner(), this);
                changeScene(outcomeScene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //someone lost the game
            List<String> losers = event.getLosers();
            if (losers.size() == 1 && losers.get(0).equals(username)) {
                //you lost the game
                System.out.println("I've lost");
                try {
                    setOutcomeScene();
                    outcomeSceneController.initAndFillSpectator(this);
                    changeScene(outcomeScene);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(username.toUpperCase() + "has lost.");
            }
        }

    }

    /**
     * notifies that the game is started
     *
     * @param event sent by room when the game starts after pregame, contains first username (0) in turn sequence
     */
    @Override
    public void handleEvent(CV_GameStartedGameEvent event) {
        boardSceneController.handle(event);
    }

    /**
     * Handles the event passed as parameter calling the {@code handleEvent} method on the {@link BoardSceneController}
     *
     * @param event the {@link CV_NewTurnEvent} just received by the server saying that a new turn is starting
     */
    @Override
    public void handleEvent(CV_NewTurnEvent event) {
        boardSceneController.handle(event);
    }

    /**
     * Handles the event passed as parameter calling the {@code handleEvent} method on the {@link BoardSceneController}
     *
     * @param event the {@link CV_IslandUpdateEvent} just received by the server with the update of the island after a command was executed
     */
    @Override
    public void handleEvent(CV_IslandUpdateEvent event) {
        boardSceneController.handle(event);
    }

    /**
     * Handles the event passed as parameter calling the {@code handleEvent} method on the {@link BoardSceneController}
     *
     * @param event the {@link CV_WaitMatchGameEvent} just received by the server saying that is another client turn and this client has not to perform an action
     */
    @Override
    public void handleEvent(CV_WaitMatchGameEvent event) {
        boardSceneController.handle(event);
    }

    /**
     * Handles the event passed as parameter calling the {@code handleEvent} method on the {@link BoardSceneController}
     *
     * @param event the {@link CV_TurnInfoEvent} just received by the server containing the info of the new turn
     */
    @Override
    public void handleEvent(CV_TurnInfoEvent event) {
        boardSceneController.handle(event);
    }

    /**
     * Handles the event passed as parameter calling the {@code handleEvent} method on the {@link BoardSceneController}
     *
     * @param event the {@link CV_SpectatorGameEvent} just received by the server saying that this client is now in spectator mode
     */
    @Override
    public void handleEvent(CV_SpectatorGameEvent event) {
        boardSceneController.handle(event);
    }

    public String getOnlineServerIP() {
        return ONLINE_SERVER_IP;
    }

    public void setShowBoardScene() {
        changeScene(boardScene);
    }

    /**
     * Closes the main stage and the application, if the client is yet connected closes also the connection with the server
     */
    public void closeApp() {
        stage.close();
        Platform.exit();
        if (client.isConnectionOpen()) {
            client.closeConnection();
        }
        System.exit(0);
    }

    @Override
    public void handleEvent(CV_NewGameRequestEvent event) {
        //NOT USED IN GUI
    }
}
