package it.polimi.ingsw.psp58.view.UI.GUI;

import it.polimi.ingsw.psp58.auxiliary.IslandData;
import it.polimi.ingsw.psp58.event.PlayerDisconnectedGameEvent;
import it.polimi.ingsw.psp58.event.core.EventListener;
import it.polimi.ingsw.psp58.event.core.EventSource;
import it.polimi.ingsw.psp58.event.gameEvents.CV_GameErrorGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.*;
import it.polimi.ingsw.psp58.event.gameEvents.match.*;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.*;
import it.polimi.ingsw.psp58.model.WorkerColors;
import it.polimi.ingsw.psp58.networking.client.SantoriniClient;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.BoardSceneController;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.ConnectionSceneController;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.RoomSceneController;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.StartingSceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static it.polimi.ingsw.psp58.event.core.ListenerType.VIEW;


public class GUI extends Application implements EventListener {

    private Stage stage;

    private final int socketPort = 7557;

    private String chosenIp;

    private WorkerColors playerColor;

    private String username;

    private SantoriniClient client;

    private IslandData currentIsland;

    private Scene startingScene;
    private StartingSceneController startingSceneController;

    private Scene connectionScene;
    private ConnectionSceneController connectionSceneController;

    private Scene roomScene;
    private RoomSceneController roomSceneController;

    private Scene boardScene;
    private BoardSceneController boardSceneController;



    public static void main(String[] args) {
        Application.launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws IOException {


        stage = primaryStage;

        //set up the starting scene
        FXMLLoader loaderStartingScene = new FXMLLoader(
                getClass().getResource("/scenes/StartingScene.fxml"));
        startingScene = new Scene(loaderStartingScene.load());
        startingSceneController = loaderStartingScene.getController();
        startingSceneController.setGui(this);

        //set up the connection scene
        FXMLLoader loaderConnectionScene = new FXMLLoader(
                getClass().getResource("/scenes/ConnectionScene.fxml"));
        connectionScene= new Scene(loaderConnectionScene.load());
        connectionSceneController =loaderConnectionScene.getController();
        connectionSceneController.setGui(this);

//        //set up the room scene
//        FXMLLoader loaderRoomScene = new FXMLLoader(
//                getClass().getResource("/scenes/RoomScene.fxml"));
//        roomScene = new Scene(loaderRoomScene.load());
//        roomSceneController =loaderRoomScene.getController();
//        connectionSceneController.setGui(this);
//
//        //set up the board scene
//        FXMLLoader loaderBoardScene = new FXMLLoader(
//                getClass().getResource("/scenes/BoardScene.fxml"));
//        boardScene = new Scene(loaderBoardScene.load());
//        boardSceneController =loaderBoardScene.getController();
//        connectionSceneController.setGui(this);


        //starts with the startingScene
        stage.setTitle("Santorini Online");
        stage.setScene(startingScene);
        stage.show();
    }

    public void changeScene(Scene scene){
        stage.close();
        stage.setTitle("Santorini Online");
        stage.setScene(scene);
        stage.show();
    }

    public void sendEvent(GameEvent event){
        client.sendEvent(event);
    }

    public void setChosenIp(String chosenIp) {
        this.chosenIp = chosenIp;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void setConnectionScene(Scene connectionScene) {
        this.connectionScene = connectionScene;
    }

    public void setConnectionSceneController(ConnectionSceneController connectionSceneController) {
        this.connectionSceneController = connectionSceneController;
    }

    public void setRoomScene(Scene roomScene) {
        this.roomScene = roomScene;
    }

    public void setRoomSceneController(RoomSceneController roomSceneController) {
        this.roomSceneController = roomSceneController;
    }

    public void setBoardScene(Scene boardScene) {
        this.boardScene = boardScene;
    }

    public void setBoardSceneController(BoardSceneController boardSceneController) {
        this.boardSceneController = boardSceneController;
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

    public Scene getConnectionScene() {
        return connectionScene;
    }

    public ConnectionSceneController getConnectionSceneController() {
        return connectionSceneController;
    }

    public Scene getRoomScene() {
        return roomScene;
    }

    public RoomSceneController getRoomSceneController() {
        return roomSceneController;
    }

    public Scene getBoardScene() {
        return boardScene;
    }

    public BoardSceneController getBoardSceneController() {
        return boardSceneController;
    }

    @Override
    public void handleEvent(CV_ConnectionRejectedErrorGameEvent event) {
        ErrorMessage.display(event.getEventDescription());
    }

    @Override
    public void handleEvent(GameEvent event) {

    }

    @Override
    public void handleEvent(CV_GameErrorGameEvent event) {

    }

    @Override
    public void handleEvent(CC_ConnectionRequestGameEvent event) {

    }



    @Override
    public void handleEvent(CV_RoomSizeRequestGameEvent event) {

    }

    @Override
    public void handleEvent(CV_RoomUpdateGameEvent event) {

    }

    @Override
    public void handleEvent(VC_ConnectionRequestGameEvent event) {

    }

    @Override
    public void handleEvent(VC_RoomSizeResponseGameEvent event) {

    }

    @Override
    public void handleEvent(CV_CardChoiceRequestGameEvent event) {

    }

    @Override
    public void handleEvent(CV_ChallengerChooseFirstPlayerRequestEvent event) {

    }

    @Override
    public void handleEvent(CV_PlayerPlaceWorkerRequestEvent event) {

    }

    @Override
    public void handleEvent(CV_ChallengerChosenEvent event) {

    }

    @Override
    public void handleEvent(CV_WaitPreMatchGameEvent event) {

    }

    @Override
    public void handleEvent(VC_ChallengerCardsChosenEvent event) {

    }

    @Override
    public void handleEvent(VC_ChallengerChosenFirstPlayerEvent event) {

    }

    @Override
    public void handleEvent(VC_PlayerCardChosenEvent event) {

    }

    @Override
    public void handleEvent(VC_PlayerPlacedWorkerEvent event) {

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

    @Override
    public void handleEvent(VC_PlayerCommandGameEvent event) {

    }

    @Override
    public void handleEvent(PlayerDisconnectedGameEvent event) {

    }
}
