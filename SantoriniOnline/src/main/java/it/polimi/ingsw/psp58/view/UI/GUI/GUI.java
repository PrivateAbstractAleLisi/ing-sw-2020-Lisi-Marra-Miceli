package it.polimi.ingsw.psp58.view.UI.GUI;

import com.google.gson.Gson;
import it.polimi.ingsw.psp58.auxiliary.CellClusterData;
import it.polimi.ingsw.psp58.auxiliary.IslandData;
import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.CV_GameErrorGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.PlayerDisconnectedViewEvent;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.*;
import it.polimi.ingsw.psp58.event.gameEvents.match.*;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.*;
import it.polimi.ingsw.psp58.event.gamephase.CV_WorkerPlacementGameEvent;
import it.polimi.ingsw.psp58.exceptions.InvalidBuildException;
import it.polimi.ingsw.psp58.exceptions.InvalidMovementException;
import it.polimi.ingsw.psp58.model.WorkerColors;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.CellCluster;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import it.polimi.ingsw.psp58.networking.client.SantoriniClient;
import it.polimi.ingsw.psp58.view.UI.GUI.boardstate.CommandGameState;
import it.polimi.ingsw.psp58.view.UI.GUI.boardstate.GameState;
import it.polimi.ingsw.psp58.view.UI.GUI.boardstate.PlaceWorkerGameState;
import it.polimi.ingsw.psp58.view.UI.GUI.boardstate.WaitGameState;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.BoardSceneController;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.LobbySceneController;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.PreGameSceneController;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.StartingSceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Random;


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

        //set up the lobby scene and controller
        FXMLLoader lobbySceneLoader = new FXMLLoader(
                getClass().getResource("/scenes/LobbyScene.fxml"));
        lobbyScene = new Scene(lobbySceneLoader.load());
        lobbySceneController = lobbySceneLoader.getController();
        lobbySceneController.setGui(this);

        //set up the pregame scene and controller
        FXMLLoader preGameSceneLoader = new FXMLLoader(
                getClass().getResource("/scenes/PreGameScene.fxml"));
        preGameScene = new Scene(preGameSceneLoader.load());
        preGameSceneController = preGameSceneLoader.getController();
        preGameSceneController.setGui(this);

        //set up the board scene and controller
        FXMLLoader boardLoader = new FXMLLoader(
                getClass().getResource("/scenes/BoardScene.fxml"));
        boardScene = new Scene(boardLoader.load());
        boardSceneController = boardLoader.getController();
        stage.setResizable(false);
        boardSceneController.setGui(this);

        //starts with the startingScene
        stage.setTitle("Santorini Online");
        stage.setScene(startingScene);
        stage.show();
    }

    public IslandData generateRandomIsland() throws InvalidBuildException, InvalidMovementException {
        Random random = new Random();
        CellClusterData[][] islandData = new CellClusterData[5][5];
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                CellCluster cellCluster = new CellCluster();
                switch (random.nextInt(5)) {
                    case 0:
                        break;
                    case 1:
                        cellCluster.build(BlockTypeEnum.LEVEL1);
                        break;
                    case 2:
                        cellCluster.build(BlockTypeEnum.LEVEL1);
                        cellCluster.build(BlockTypeEnum.LEVEL2);
                        break;
                    case 3:
                        cellCluster.build(BlockTypeEnum.LEVEL1);
                        cellCluster.build(BlockTypeEnum.LEVEL2);
                        cellCluster.build(BlockTypeEnum.LEVEL3);
                        break;
                    case 4:
                        cellCluster.build(BlockTypeEnum.LEVEL1);
                        cellCluster.build(BlockTypeEnum.LEVEL2);
                        cellCluster.build(BlockTypeEnum.LEVEL3);
                        cellCluster.build(BlockTypeEnum.DOME);
                        break;
                }
                if(random.nextInt(8)==1 && !cellCluster.isComplete()) cellCluster.build(BlockTypeEnum.DOME);

                Worker worker = new Worker(Worker.IDs.A, "matteo");

                switch (random.nextInt(5)) {

                    case 0:
                    case 4:
                        worker = null;
                        break;
                    case 1:
                        worker.setColor(WorkerColors.WHITE);
                        break;
                    case 2:
                        worker.setColor(WorkerColors.BEIGE);
                        break;
                    case 3:
                        worker.setColor(WorkerColors.BLUE);
                        break;
                }
                int [] array = cellCluster.toIntArray();
                if (worker != null && array.length>0 && array[array.length - 1]!=4) cellCluster.addWorker(worker);
                islandData[x][y] = new CellClusterData(cellCluster);
            }
        }
        IslandData island = new IslandData();
        island.fillIsland(islandData);

        return island;
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

    public Stage getStage() {
        return stage;
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
        Message.show(event.getErrorMessage(),stage);

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
        Message.show(event.getEventDescription(), stage);
    }

    @Override
    public void handleEvent(PlayerDisconnectedViewEvent event) {
        Message.show(event.getDisconnectedUsername()+ event.getReason(), stage);
    }

    @Override
    public void handleEvent(CV_RoomSizeRequestGameEvent event) {
        int number = Message.askRoomSize("You're the first player, choose the size of the room:", stage);
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
        boardSceneController.setStateInstance(new PlaceWorkerGameState(event));
        boardSceneController.setWorkerOnAction(event.getWorkerToPlace());
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
        if (!stage.getScene().equals(boardScene)) {
            changeScene(preGameScene);
        }
    }

    @Override
    public void handleEvent(CV_WorkerPlacementGameEvent event) {
        stage.setResizable(false);
        System.out.println("DEBUG: worker placement update event has arrived");
        changeScene(boardScene);
        boardSceneController.setStateInstance(new WaitGameState());
        boardSceneController.init(event, username);
    }

    @Override
    public void handleEvent(CV_CommandRequestEvent event) {
        boardSceneController.setStateInstance(new CommandGameState(event));
        if(!boardSceneController.hasAlreadyMadeAMove()){
            boardSceneController.setCurrentState(GameState.SELECT_WORKER);
        }
    }

    @Override
    public void handleEvent(CV_GameOverEvent event) {

    }

    @Override
    public void handleEvent(CV_GameStartedGameEvent event) {

    }

    @Override
    public void handleEvent(CV_NewTurnEvent event) {
        boardSceneController.updateTurnSequence(event.getTurnRotation());
        if(event.getCurrentPlayerUsername().equals(username)){
            Message.show("IT'S YOUR TURN!", stage);
            boardSceneController.setAlreadyMadeAMoveThisTurn(false);
        }
    }

    @Override
    public void handleEvent(CV_IslandUpdateEvent event) {
        Gson gson = new Gson();
        final IslandData island = gson.fromJson(event.getNewIsland(), IslandData.class);
       boardSceneController.updateIsland(island);
    }

    @Override
    public void handleEvent(CV_WaitMatchGameEvent event) {
        if(!event.getActingPlayer().equals(username)){
            Message.show(event.getEventDescription().toUpperCase() +" " + event.getActingPlayer().toUpperCase(), stage);
        }
    }

}
