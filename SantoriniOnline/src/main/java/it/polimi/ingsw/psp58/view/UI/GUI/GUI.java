package it.polimi.ingsw.psp58.view.UI.GUI;

import it.polimi.ingsw.psp58.auxiliary.IslandData;
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
import it.polimi.ingsw.psp58.model.WorkerColors;
import it.polimi.ingsw.psp58.networking.client.SantoriniClient;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;


public class GUI extends Application implements ViewListener {
    private Stage stage;

    private Scene roomSizeScene;
    private Stage roomSizeStage;
    private RoomSizeSceneController roomSizeController;
    private final int socketPort = 7557;
    private final String gameVersion = "1.5.7";
    private final String onlineServerIP = "23.23.52.127";

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

    private Scene outcomeScene;
    private OutcomeSceneController outcomeSceneController;


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

        //set up the lobby scene and controller
        FXMLLoader lobbySceneLoader = new FXMLLoader(
                getClass().getResource("/scenes/LobbyScene.fxml"));
        lobbyScene = new Scene(lobbySceneLoader.load());
        lobbySceneController = lobbySceneLoader.getController();
        lobbySceneController.setGui(this);

        //set up the pregame scene and controller
//        FXMLLoader preGameSceneLoader = new FXMLLoader(
//                getClass().getResource("/scenes/PreGameScene.fxml"));
//        preGameScene = new Scene(preGameSceneLoader.load());
//        preGameSceneController = preGameSceneLoader.getController();
//        preGameSceneController.setGui(this);

        //set up the board scene and controller
//        FXMLLoader boardLoader = new FXMLLoader(
//                getClass().getResource("/scenes/BoardScene.fxml"));
//        boardScene = new Scene(boardLoader.load());
//        boardSceneController = boardLoader.getController();
//        boardSceneController.setGui(this);

        //set up the starting scene and controller
//        FXMLLoader outcomeSceneLoader = new FXMLLoader(
//                getClass().getResource("/scenes/OutcomeScene.fxml"));
//        outcomeScene = new Scene(outcomeSceneLoader.load());
//
//        outcomeSceneController = outcomeSceneLoader.getController();

        //RoomSizeRequest

        //starts with the startingScene
        stage.setTitle("Santorini Online");
        stage.setScene(startingScene);

        stage.show();
    }

    @Override
    public void handleEvent(CV_RoomSizeRequestGameEvent event) {
        //int number = Message.askRoomSize("You're the first player, choose the size of the room:", stage);
        try {
            prepareRoomSizeRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startingSceneController.complete();

    }

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

    public void roomSizeResponse(int result) {
        roomSizeStage.close();
        System.out.println(result);
        VC_RoomSizeResponseGameEvent responseEvent = new VC_RoomSizeResponseGameEvent("", result);
        sendEvent(responseEvent);

    }

    public void changeScene(Scene scene) {
//        stage.close();
        stage.setTitle("Santorini Online");
        stage.setScene(scene);
        if (scene.equals(preGameScene)) {
            stage.setResizable(true);
        } else {
            stage.setResizable(false);
        }
        stage.show();
    }

    private void setNewPreGameScene() throws IOException {
        //set up the pregame scene and controller
        FXMLLoader preGameSceneLoader = new FXMLLoader(
                getClass().getResource("/scenes/PreGameScene.fxml"));
        preGameScene = new Scene(preGameSceneLoader.load());
        preGameSceneController = preGameSceneLoader.getController();
        preGameSceneController.setGui(this);
    }

    private void setNewBoardGameScene() throws IOException {
        //set up the board scene and controller
        FXMLLoader boardLoader = new FXMLLoader(
                getClass().getResource("/scenes/BoardScene.fxml"));
        boardScene = new Scene(boardLoader.load());
        boardSceneController = boardLoader.getController();
        boardSceneController.setGui(this);
    }

    private void setOutcomeScene() throws IOException {
        //set up the starting scene and controller
        FXMLLoader outcomeSceneLoader = new FXMLLoader(
                getClass().getResource("/scenes/OutcomeScene.fxml"));
        outcomeScene = new Scene(outcomeSceneLoader.load());

        outcomeSceneController = outcomeSceneLoader.getController();
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

    public Stage getStage() {
        return stage;
    }

    public String getGameVersion() {
        return gameVersion;
    }

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

    @Override
    public void handleEvent(CV_ReconnectionRejectedErrorGameEvent event) {

        new ErrorPopUp().show("Please wait few seconds and retry.", stage);
    }

    @Override
    public void handleEvent(CV_NewGameRequestEvent event) {

    }


    public void showError(String message) {
        new ErrorPopUp().show(message, stage);
    }

    @Override
    public void handleEvent(CV_GameErrorGameEvent event) {
        Message.show(event.getEventDescription(), stage);
    }

    @Override
    public void handleEvent(PlayerDisconnectedViewEvent event) {
        Message.show(event.getDisconnectedUsername() + event.getReason(), stage);
    }


    @Override
    public void handleEvent(CV_RoomUpdateGameEvent event) {
        System.out.println("Room Update received");
        lobbySceneController.update(event);
        if (!stage.getScene().equals(lobbyScene)) {
            changeScene(lobbyScene);
        }
    }

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

    @Override
    public void handleEvent(CV_PreGameErrorGameEvent event) {
        new ErrorPopUp().show("Ablerobello", stage);
    }

    @Override
    public void handleEvent(CV_CardChoiceRequestGameEvent event) {
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
        boardSceneController.handle(event);
    }

    @Override
    public void handleEvent(CV_ChallengerChosenEvent event) {
        System.out.println("I'm the challenger");
        preGameSceneController.update(event);
    }

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

    @Override
    public void handleEvent(CV_CommandExecutedGameEvent event) {
        boardSceneController.handle(event);
    }

    @Override
    public void handleEvent(CV_CommandRequestEvent event) {
        boardSceneController.handle(event);
    }

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
                    outcomeSceneController.initAndFillSpectator(event.getLosers(), this);
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

    @Override
    public void handleEvent(CV_NewTurnEvent event) {
        boardSceneController.handle(event);
    }

    @Override
    public void handleEvent(CV_IslandUpdateEvent event) {
        boardSceneController.handle(event);
    }

    @Override
    public void handleEvent(CV_WaitMatchGameEvent event) {
        if (!event.getActingPlayer().equals(username)) {
            boardSceneController.addMessageToQueueList(event.getEventDescription().toUpperCase() + " " + event.getActingPlayer().toUpperCase());
        }
    }

    @Override
    public void handleEvent(CV_TurnInfoEvent event) {
        boardSceneController.handle(event);
    }

    @Override
    public void handleEvent(CV_SpectatorGameEvent cv_spectatorGameEvent) {
        boardSceneController.handle(cv_spectatorGameEvent);
    }

    public String getOnlineServerIP() {
        return onlineServerIP;
    }

    public void setShowBoardScene (){
        changeScene(boardScene);
    }

    public void closeApp(){
        client.closeConnection();
        stage.close();
    }
}
