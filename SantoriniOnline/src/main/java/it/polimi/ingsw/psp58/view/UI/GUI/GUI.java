package it.polimi.ingsw.psp58.view.UI.GUI;

import it.polimi.ingsw.psp58.auxiliary.IslandData;
import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.*;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.*;
import it.polimi.ingsw.psp58.event.gameEvents.match.*;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.*;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.WorkerColors;
import it.polimi.ingsw.psp58.networking.client.SantoriniClient;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GUI extends Application implements ViewListener {

    private Stage stage;

    private final int socketPort = 7557;

    private String chosenIp;

    private boolean enablePing = true;

    private WorkerColors playerColor;

    private String username;

    private SantoriniClient client;

    private IslandData currentIsland;

    private Scene startingScene;
    private StartingSceneController startingSceneController;


    private Scene lobbyScene;
    private LobbySceneController lobbySceneController;

    private Scene boardScene;
    private BoardSceneController boardSceneController;

    private Scene preGameScene;
    private PreGameSceneController preGameSceneController;



    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        List<String> args = getParameters().getRaw();

        if (args != null && !args.isEmpty()) {
            for (String currentArgument : args) {
                switch (currentArgument) {
                    case "-ping off":
                        enablePing = false;
                        break;
                }
            }
        }

        stage = primaryStage;
        stage.setResizable(false);

        //set up the starting scene and controller
        FXMLLoader loaderStartingScene = new FXMLLoader(
                getClass().getResource("/scenes/StartingScene.fxml"));
        startingScene = new Scene(loaderStartingScene.load());

        startingSceneController = loaderStartingScene.getController();
        startingSceneController.setGui(this);
        startingSceneController.start();

        //starts with the startingScene
        stage.setTitle("Santorini Online");

        //stage.setScene(startingScene);


        //DEBUG START WITH ANOTHER SCENE
        FXMLLoader boardLoader = new FXMLLoader(
                getClass().getResource("/scenes/BoardScene.fxml"));
        boardScene = new Scene(boardLoader.load());
        boardSceneController = boardLoader.getController();
        stage.setResizable(false);
        changeScene(boardScene);

        boardSceneController.debugTest();
        stage.show();

        //set up the lobby
        FXMLLoader lobbySceneLoader = new FXMLLoader(
                getClass().getResource("/scenes/LobbyScene.fxml"));
        lobbyScene = new Scene(lobbySceneLoader.load());
        lobbySceneController = lobbySceneLoader.getController();
        lobbySceneController.setGui(this);

        //set up the testLoad
        FXMLLoader preGameSceneLoader = new FXMLLoader(
                getClass().getResource("/scenes/PreGameScene.fxml"));
        preGameScene = new Scene(preGameSceneLoader.load());
        preGameSceneController = preGameSceneLoader.getController();
        preGameSceneController.setGui(this);
    }

    public void changeScene(Scene scene) {
//        stage.close();
        stage.setTitle("Santorini Online");
        stage.setScene(scene);
        stage.show();
    }

    public void sendEvent(ControllerGameEvent event) {
        client.sendEvent(event);
    }

    public void setChosenIp(String chosenIp) {
        this.chosenIp = chosenIp;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setClient(SantoriniClient client) {
        this.client = client;
    }

    public SantoriniClient getClient() {
        return client;
    }

    public void setStartingScene(Scene startingScene) {
        this.startingScene = startingScene;
    }

    public void setStartingSceneController(StartingSceneController startingSceneController) {
        this.startingSceneController = startingSceneController;
    }

    public void setLobbyScene(Scene roomScene) {
        this.lobbyScene = roomScene;
    }

    public void setLobbySceneController(LobbySceneController lobbySceneController) {
        this.lobbySceneController = lobbySceneController;
    }

    public void setBoardScene(Scene boardScene) {
        this.boardScene = boardScene;
    }

    public void setBoardSceneController(BoardSceneController boardSceneController) {
        this.boardSceneController = boardSceneController;
    }

    public boolean isPingEnabled() {
        return enablePing;
    }

    public int getSocketPort() {
        return socketPort;
    }

    public Scene getStartingScene() {
        return startingScene;
    }

    public StartingSceneController getStartingSceneController() {
        return startingSceneController;
    }


    public Scene getLobbyScene() {
        return lobbyScene;
    }

    public LobbySceneController getLobbySceneController() {
        return lobbySceneController;
    }

    public Scene getBoardScene() {
        return boardScene;
    }

    public BoardSceneController getBoardSceneController() {
        return boardSceneController;
    }

    @Override
    public void handleEvent(CV_ConnectionRejectedErrorGameEvent event) {

        getStartingSceneController().enableAllLoginFields();
        //notify the error on screen
        Message.show(event.getErrorMessage());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void handleEvent(CV_ReconnectionRejectedErrorGameEvent event) {

    }

    @Override
    public void handleEvent(CV_NewGameRequestEvent event) {

    }

    @Override
    public void handleEvent(CV_GameErrorGameEvent event) {

    }

    @Override
    public void handleEvent(PlayerDisconnectedViewEvent event) {

    }

    @Override
    public void handleEvent(CV_RoomSizeRequestGameEvent event) {
        int number = Message.askRoomSize("You're the first player, choose the size of the room:");
        startingSceneController.complete();
        System.out.println(number);
        VC_RoomSizeResponseGameEvent responseEvent = new VC_RoomSizeResponseGameEvent("", number);
        sendEvent(responseEvent);
    }

    @Override
    public void handleEvent(CV_RoomUpdateGameEvent event) {
        System.out.println("room received");
        lobbySceneController.update(event);
        if (stage.getScene().equals(startingScene)) {
            changeScene(lobbyScene);
        }
        preGameSceneController.update(event);
    }

    @Override
    public void handleEvent(CV_CardChoiceRequestGameEvent event) {
        stage.setResizable(true);
        System.out.println("I have to choose my card!");
        preGameSceneController.update(event);
    }

    @Override
    public void handleEvent(CV_ChallengerChooseFirstPlayerRequestEvent event) {
        System.out.println("Choose the first player!");
        preGameSceneController.update(event);
    }

    @Override
    public void handleEvent(CV_PlayerPlaceWorkerRequestEvent event) {

    }

    @Override
    public void handleEvent(CV_ChallengerChosenEvent event) {
        stage.setResizable(true);
        stage.setMaximized(true);
        System.out.println("I'm the challenger");
        preGameSceneController.update(event);
        changeScene(preGameScene);
    }

    @Override
    public void handleEvent(CV_WaitPreMatchGameEvent event) {
        stage.setResizable(true);
        if(event.getWaitCode().equals("CHALLENGERS_CARDS")){
            stage.setMaximized(true);
        }
        System.out.println("Wait received");
        preGameSceneController.update(event);
        changeScene(preGameScene);
    }

    @Override
    public void handleEvent(CV_CommandRequestEvent event) {

    }

    @Override
    public void handleEvent(CV_GameOverEvent event) {

    }

    @Override
    public void handleEvent(CV_GameStartedGameEvent event) {

    }

    @Override
    public void handleEvent(CV_NewTurnEvent event) {

    }

    @Override
    public void handleEvent(CV_IslandUpdateEvent event) {

    }

    @Override
    public void handleEvent(CV_WaitMatchGameEvent event) {

    }

}
