package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.psp58.auxiliary.CellClusterData;
import it.polimi.ingsw.psp58.auxiliary.IslandData;
import it.polimi.ingsw.psp58.event.gameEvents.match.*;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.CV_PlayerPlaceWorkerRequestEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.CV_WaitPreMatchGameEvent;
import it.polimi.ingsw.psp58.event.gamephase.CV_WorkerPlacementGameEvent;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.TurnAction;
import it.polimi.ingsw.psp58.model.WorkerColors;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import it.polimi.ingsw.psp58.view.UI.GUI.BoardPopUp;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import it.polimi.ingsw.psp58.view.UI.GUI.boardstate.CommandGameState;
import it.polimi.ingsw.psp58.view.UI.GUI.boardstate.GameStateAbstract;
import it.polimi.ingsw.psp58.view.UI.GUI.boardstate.PlaceWorkerGameState;
import it.polimi.ingsw.psp58.view.UI.GUI.boardstate.WaitGameState;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.exceptions.WorkerLockedException;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.*;


public class BoardSceneController {
    private WorkerColors myColor;
    private String myUsername = "";
    private GUI gui;

    private IslandData lastIslandUpdate;
    private StackPane[][] lastGridPane;

    //STATE PATTERN
    private WorkerStatus workerStatus;
    private GameStateAbstract currentStateInstance;
    public GridPane board;

    public Node workerSelectedNode;

    public VBox cardInfo1;
    public VBox cardInfo2;
    public VBox cardInfo3;

    private List<VBox> cardInfoList;

    public ImageView cardImage1;
    public ImageView cardImage2;
    public ImageView cardImage3;

    private List<ImageView> cardImagesList;

    public Label player1;
    public Label player2;
    public Label player3;

    public HBox workerPrePlaceHBox;

    private List<Label> playersList;

    //ACTION BUTTONS
    public Button moveButton, buildButton, passButton;

    //BLOCKS
    public HBox L1Box, L2Box, L3Box, DomeBox;

    //WORKERS
    public ImageView workerSlotA;
    public ImageView workerSlotB;

    //TURN SEQUENCE
    public Label turnSequence;

    //RightMessagge
    public Text rightMessage;
    LinkedList<String> messagesQueue;

    public void init(CV_WorkerPlacementGameEvent event, String myUsername) {
        initializeIsland();

        updateTurnSequence(event.getTurnSequence());
        this.myUsername = myUsername;
        myColor = event.getPlayerWorkerColors().get(myUsername.toLowerCase());

        String url = getWorkerUrl(myColor);

        workerSlotA.setImage(new Image(url));
        workerSlotB.setImage(new Image(url));

        resetTurnStatus();

        //update blocks counter to starting state
        updateBlocksCounter(22, 18, 14, 18);

        messagesQueue = new LinkedList<>();
    }


    /* ----------------------------------------------------------------------------------------------
                                         BOARD CLICK
       ----------------------------------------------------------------------------------------------*/

    public void onClickEventCellCluster(MouseEvent mouseEvent) {
        StackPane source = (StackPane) mouseEvent.getSource();
        Integer colIndex = GridPane.getColumnIndex(source);
        Integer rowIndex = GridPane.getRowIndex(source);
        System.out.printf("Mouse clicked cell [%d, %d]%n", colIndex, rowIndex);
        currentStateInstance.handleClickOnButton(colIndex, rowIndex);
    }

    /* ----------------------------------------------------------------------------------------------
                                         BUTTONS CLICK
       ----------------------------------------------------------------------------------------------*/

    public void moveButtonClick() {
        currentStateInstance.handleClickOnButton(TurnAction.MOVE);
    }

    public void buildButtonClick() {
        currentStateInstance.handleClickOnButton(TurnAction.BUILD);
    }

    public void passButtonClick() {
        currentStateInstance.handleClickOnButton(TurnAction.PASS);
    }

    /* ----------------------------------------------------------------------------------------------
                                         BLOCKS CLICK
       ----------------------------------------------------------------------------------------------*/

    public void level1Click() {
        currentStateInstance.handleClickOnButton(BlockTypeEnum.LEVEL1);
    }

    public void level2Click() {
        currentStateInstance.handleClickOnButton(BlockTypeEnum.LEVEL2);
    }

    public void level3Click() {
        currentStateInstance.handleClickOnButton(BlockTypeEnum.LEVEL3);
    }

    public void domeClick() {
        currentStateInstance.handleClickOnButton(BlockTypeEnum.DOME);
    }

    /* ----------------------------------------------------------------------------------------------
                                         STATUS and HANDLE METHODS
       ----------------------------------------------------------------------------------------------*/

    //STATUS
    public void resetTurnStatus() {
        workerStatus = new WorkerStatus();
    }

    public WorkerStatus getWorkerStatus() {
        return workerStatus;
    }

    public void setWaitingView() {
        disableAllActionButtons();
        resetTurnStatus();
        disableWorkerGlow();
    }

    public void hideWorkerPlacementBox() {
        //workerPrePlaceHBox.setVisible(false);
    }

    public void handle(CV_PlayerPlaceWorkerRequestEvent event) {
        GameStateAbstract nextState = new PlaceWorkerGameState(event, gui, this);
        try {
            workerStatus.setSelectedWorker(event.getWorkerToPlace());
        } catch (WorkerLockedException e) {
            e.printStackTrace();
        }

        setLastIslandUpdate(event.getIsland());
        updateIsland(event.getIsland());
        handleWorkerPlacement(event.getWorkerToPlace());

        this.currentStateInstance = nextState;
    }

    public void handle(CV_WorkerPlacementGameEvent event) {
        GameStateAbstract nextState = new WaitGameState(gui);
        setWaitingView();
        this.currentStateInstance = nextState;
    }

    public void handle(CV_WaitPreMatchGameEvent event) {
        GameStateAbstract nextState = new WaitGameState(event, gui);
        setWaitingView();
        this.currentStateInstance = nextState;
    }

    public void handle(CV_CommandRequestEvent event) {
        enableActionButtons(event.getAvailableActions());
        disableAllGreenActionButton();
        disableAllGreenBuildingBlock();
        deactivateAllGlowOnPanels();

        //if worker is not locked, it will be deleted
        workerStatus.resetSelectedWorker();

        currentStateInstance.updateFromServer(event);
    }

    public void handle(CV_NewTurnEvent event) {
        updateTurnSequence(event.getTurnRotation());

        if (event.getCurrentPlayerUsername().equals(myUsername)) {
            currentStateInstance = new CommandGameState(event, gui, this);
            resetTurnStatus();
            addMessageToQueueList("IT'S YOUR TURN!");
        }
    }

    public void handle(CV_GameStartedGameEvent event) {
        GameStateAbstract nextState = new WaitGameState(gui);
        setWaitingView();
        this.currentStateInstance = nextState;
        addMessageToQueueList("Game is starting!");
    }

    public void handleWorkerPlacement(Worker.IDs workerRequested) {
        disableAllActionButtons();
        switch (workerRequested) {

            case A:
                workerSlotA.setEffect(new Glow(0.8));
                BoardPopUp.show("Please place worker " + workerRequested.toString(), gui.getStage());
                break;
            case B:
                workerSlotB.setEffect(new Glow(0.8));
                BoardPopUp.show("Please place worker " + workerRequested.toString(), gui.getStage());
                break;
        }
    }

    public void handle(CV_CommandExecutedGameEvent event) {
        System.out.println("DEBUG: CV_CommandExecutedGameEvent event has arrived");
        currentStateInstance.updateFromServer(event);
    }

    /* ----------------------------------------------------------------------------------------------
                                         ACTION BUTTONS METHODS
       ----------------------------------------------------------------------------------------------*/

    public void disableAllActionButtons() {
        moveButton.setDisable(true);
        buildButton.setDisable(true);
        passButton.setDisable(true);
    }

    public void enableActionButtons(List<TurnAction> availableActions) {
        for (TurnAction action : availableActions) {
            switch (action) {
                case MOVE:
                    moveButton.setDisable(false);
                    break;
                case BUILD:
                    buildButton.setDisable(false);
                    break;
                case PASS:
                    passButton.setDisable(false);
                    break;
            }
        }
    }

    public void setGreenActionButton(boolean enable, TurnAction buttonToSet) {
        if (enable) {
            moveButton.setEffect(null);
            buildButton.setEffect(null);
            passButton.setEffect(null);
        }
        Button button;
        switch (buttonToSet) {
            case MOVE:
                button = moveButton;
                break;
            case BUILD:
                button = buildButton;
                break;
            case PASS:
                button = passButton;
                break;
            default:
                button = passButton;
        }

        if (enable) {
            //point.setEffect(new Glow(0.7));
            System.out.println("DEBUG: setButtonGlow on " + buttonToSet);

            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(14.5);
//            dropShadow.setWidth(30);
//            dropShadow.setHeight(30);
            dropShadow.setOffsetX(0);
            dropShadow.setOffsetY(0);
            dropShadow.setSpread(0.6);
            dropShadow.setColor(Color.rgb(50, 180, 50));
            button.setEffect(dropShadow);
        } else {
            button.setEffect(null);
        }

    }

    private void disableAllGreenActionButton() {
        moveButton.setEffect(null);
        buildButton.setEffect(null);
        passButton.setEffect(null);
    }

    /* ----------------------------------------------------------------------------------------------
                                         BUILDING BLOCKS METHODS
       ----------------------------------------------------------------------------------------------*/

    private void updateBlocksCounter(int lev1, int lev2, int lev3, int dome) {
        ((Label) L1Box.getChildren().get(1)).setText(Integer.toString(lev1));
        ((Label) L2Box.getChildren().get(1)).setText(Integer.toString(lev2));
        ((Label) L3Box.getChildren().get(1)).setText(Integer.toString(lev3));
        ((Label) DomeBox.getChildren().get(1)).setText(Integer.toString(dome));
    }


    public void setGreenBuildingBlocks(boolean enable, BlockTypeEnum blockToSet) {
        if (enable) {
            L1Box.setEffect(null);
            L2Box.setEffect(null);
            L3Box.setEffect(null);
            DomeBox.setEffect(null);
        }
        HBox hBox;
        switch (blockToSet) {
            case LEVEL1:
                hBox = L1Box;
                break;
            case LEVEL2:
                hBox = L2Box;
                break;
            case LEVEL3:
                hBox = L3Box;
                break;
            case DOME:
                hBox = DomeBox;
                break;
            default:
                hBox = DomeBox;
        }

        if (enable) {
            //point.setEffect(new Glow(0.7));
            System.out.println("DEBUG: setButtonGlow on " + blockToSet);

            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(14.5);
//            dropShadow.setWidth(30);
//            dropShadow.setHeight(30);
            dropShadow.setOffsetX(0);
            dropShadow.setOffsetY(0);
            dropShadow.setSpread(0.6);
            dropShadow.setColor(Color.rgb(50, 180, 50));
            hBox.setEffect(dropShadow);
        } else {
            hBox.setEffect(null);
        }


    }

    public void disableAllGreenBuildingBlock() {
        L1Box.setEffect(null);
        L2Box.setEffect(null);
        L3Box.setEffect(null);
        DomeBox.setEffect(null);
    }

    /* ----------------------------------------------------------------------------------------------
                                         TURN SEQUENCE METHODS
       ----------------------------------------------------------------------------------------------*/

    public void updateTurnSequence(Map<String, CardEnum> turnSequenceFromEvent) {

        //fill turn sequence
        Map<String, CardEnum> sequence = turnSequenceFromEvent;
        List<String> turnSequence = new ArrayList<>();
        for (String player : sequence.keySet()) {
            turnSequence.add(player);
        }
        updateTurnSequence(turnSequence);
    }

    public void updateTurnSequence(List<String> sequence) {
        String turnSequenceText = "";
        boolean first = true;
        for (String s : sequence) {
            if (first) {
                turnSequenceText += " ";
                first = false;
            } else {
                turnSequenceText += " << ";
            }
            turnSequenceText += s.toUpperCase();
        }
        turnSequence.setText(turnSequenceText);
    }

     /* ----------------------------------------------------------------------------------------------
                                         ISLAND METHODS
       ----------------------------------------------------------------------------------------------*/

    public void handle(CV_IslandUpdateEvent event) {
        updateIsland(event.getNewIsland());
    }

    public IslandData getLastIslandUpdate() {
        return lastIslandUpdate;
    }

    public void updateIsland(String islandDataJSON) {
        IslandData island = islandDataFromJson(islandDataJSON);
        updateIsland(island);
    }

    public void updateIsland(IslandData island) {
        setLastIslandUpdate(island);

        String url = "";
        Platform.runLater(() -> {
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    CellClusterData cellClusterData = island.getCellCluster(x, y);
                    StackPane stackPane = new StackPane();
                    Pane pane = new Pane();
                    Pane upperPane = new Pane();
                    upperPane.setVisible(false);

                    //if there are construction blocks adds the image
                    if (cellClusterData.getBlocks() != null && (cellClusterData.getBlocks().length > 0 || cellClusterData.isDomeOnTop())) {
                        pane.getChildren().add(getBlockImage(cellClusterData));
                    }

                    //if there is a worker adds the image
                    if (cellClusterData.getWorkerOnTop() != null) {
                        pane.getChildren().add(getWorkerImage(cellClusterData));


                    }
                    stackPane.getChildren().addAll(pane, upperPane);
                    stackPane.setOnMouseClicked(this::onClickEventCellCluster);

                    GridPane.setConstraints(stackPane, x, y);

                    board.getChildren().remove(lastGridPane[x][y]);
                    board.getChildren().add(stackPane);

                    lastGridPane[x][y] = stackPane;

                    if (cellClusterData.getWorkerOnTop() != null) {
                        if (workerStatus.getSelectedWorker() != null && cellClusterData.getWorkerColor().equals(myColor) && workerStatus.getSelectedWorker().equals(cellClusterData.getWorkerOnTop())) {
                            setWorkerGlow(true, x, y);

                            System.out.println("DEBUG: RESTORING GLOW");
                        }
                    }
                }
            }
        });

    }

    public void setLastIslandUpdate(IslandData lastIslandUpdate) {
        this.lastIslandUpdate = lastIslandUpdate;
    }

    public void setLastIslandUpdate(String lastIslandUpdate) {
        this.lastIslandUpdate = islandDataFromJson(lastIslandUpdate);
    }

    public void initializeIsland() {
        lastGridPane = new StackPane[5][5];
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                StackPane stackPane = new StackPane();
                Pane pane = new Pane();
                Pane upperPane = new Pane();
                upperPane.setVisible(false);

                stackPane.getChildren().addAll(pane, upperPane);
                stackPane.setOnMouseClicked(this::onClickEventCellCluster);
                GridPane.setConstraints(stackPane, y, x); //node, column, row

                board.getChildren().add(stackPane);

                lastGridPane[x][y] = stackPane;
            }
        }
    }

    public ImageView getBlockImage(CellClusterData cellClusterData) {
        String url = getUrlFromCellCluster(cellClusterData);

        ImageView image = new ImageView();
        image.setFitWidth(100);
        image.setFitHeight(100);
        image.setPreserveRatio(true);
        image.setImage(new Image(url));
        return image;
    }

    public void activateGlowOnPanels(List<int[]> panelPositions, TurnAction actionClicked) {
        for (int[] position : panelPositions) {
            StackPane stackPane = (StackPane) getNodeByRowColumnIndex(position[0], position[1]);
            if (actionClicked == TurnAction.MOVE) {
                Pane pane = (Pane) stackPane.getChildren().get(1);
                pane.setVisible(true);
                pane.setStyle("-fx-background-color: rgba(0,255,255,0.73)");
            } else if (actionClicked == TurnAction.BUILD) {
                stackPane.getChildren().get(1).setVisible(true);
                stackPane.getChildren().get(1).setStyle("-fx-background-color: rgba(165,42,42,0.52)");
            }
//            board.getChildren().remove(position[0], position[1]);
//            board.add(stackPane, position[0], position[1]);
        }
    }
    public void deactivateAllGlowOnPanels(){
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                StackPane stackPane = (StackPane) getNodeByRowColumnIndex(i,j);
                stackPane.getChildren().get(1).setVisible(false);
            }
        }
    }

    public String getUrlFromCellCluster(CellClusterData cellClusterData) {
        String url = "/images/cellcluster/";

        int[] blocks = cellClusterData.getBlocks();
        boolean domeOnTop = cellClusterData.isDomeOnTop();
        Worker.IDs workerID = cellClusterData.getWorkerOnTop();
        WorkerColors color = cellClusterData.getWorkerColor();

        //construct the url of the image
        if ((blocks.length == 1 && blocks[0] == 4)) {
            if (domeOnTop) url = url + "L0_DOME.png";
            return url;
        } else {
            if (blocks[blocks.length - 1] == 4) {
                blocks = Arrays.copyOfRange(blocks, 0, blocks.length - 1);
            }
            switch (blocks[blocks.length - 1]) {
                case 1: {
                    url = url + "L1";
                    break;
                }
                case 2: {
                    url = url + "L2";
                    break;
                }
                case 3: {
                    url = url + "L3";
                    break;
                }
            }
            if (domeOnTop) {
                url = url + "_DOME";
            }
            url = url + ".png";
        }
        return url;
    }

    //utility to get a cell from the board
    public Node getNodeByRowColumnIndex(final int row, final int column) {

        return lastGridPane[row][column];
//        Node result = null;
//        ObservableList<Node> children = board.getChildren();
//
//        for (Node node : children) {
//            if (node instanceof StackPane && GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
//                result = node;
//                break;
//            }
//        }
//
//        return result;
    }

    private IslandData islandDataFromJson(String islandJson) {
        //display island
        Gson gson = new Gson();
        return gson.fromJson(islandJson, IslandData.class);
    }

    /* ----------------------------------------------------------------------------------------------
                                         UTILITY METHODS
       ----------------------------------------------------------------------------------------------*/

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    /**
     * Displays a message to the user with a popup panel
     *
     * @param message the message you would like to display
     */
    public void displayPopupMessage(String message) {
        //TODO messaggio sotto e non popup
        BoardPopUp.show(message.toUpperCase(), gui.getStage());
    }

    public void addMessageToQueueList(String messageToShow) {
        messagesQueue.add(messageToShow);
        showMessageOntRightPane();
    }

    private void showMessageOntRightPane() {
        rightMessage.setText(messagesQueue.remove());
    }

    /* ----------------------------------------------------------------------------------------------
                                         WORKER UTILITY AND METHODS
       ----------------------------------------------------------------------------------------------*/

    private String getWorkerUrl(WorkerColors color) {
        String url = "/images/cellcluster/";
        switch (color) {
            case WHITE:
                url = url + "W_PINK.png";
                break;
            case BLUE:
                url = url + "W_BLUE.png";
                break;
            case BEIGE:
                url = url + "W_ORANGE.png";
                break;
        }
        return url;
    }

    public void setWorkerGlow(boolean active, int x, int y) {
        Node point = getNodeByRowColumnIndex(x, y);
        if (active) {

            //point.setEffect(new Glow(0.7));
            System.out.println("DEBUG: setWorkerGlow on " + x + " " + y);
            workerSelectedNode = point;

            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(14.5);
            dropShadow.setWidth(30);
            dropShadow.setHeight(30);
            dropShadow.setOffsetX(0);
            dropShadow.setOffsetY(0);
            dropShadow.setSpread(0.6);
            dropShadow.setColor(Color.rgb(255, 234, 5));
            point.setEffect(dropShadow);
        } else {
            point.setEffect(null);
            point = null;
        }
    }

    public void setWorkerGlow(boolean active, Worker.IDs workerID) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                CellClusterData cellClusterData = lastIslandUpdate.getCellCluster(i, j);
                if (cellClusterData.getWorkerOnTop() != null && cellClusterData.getWorkerOnTop().equals(workerID) && cellClusterData.getUsernamePlayer().equals(myUsername)) {
                    setWorkerGlow(active, i, j);
                }
            }
        }
    }

    public void disableWorkerGlow() {
        if (workerStatus != null && workerStatus.getSelectedWorker() != null) {
            setWorkerGlow(false, workerStatus.getSelectedWorker());
        }
    }

    public Worker.IDs getWorkerID(int x, int y) {
        return lastIslandUpdate.getCellCluster(x, y).getWorkerOnTop();
    }

    public ImageView getWorkerImage(CellClusterData cellClusterData) {
        String url = getWorkerUrl(cellClusterData.getWorkerColor());
        ImageView workerImage = new ImageView();
        workerImage.setFitWidth(100);
        workerImage.setFitHeight(100);
        workerImage.setPreserveRatio(true);
        workerImage.setImage(new Image(url));
        return workerImage;
    }


    //    private void initializePlayersList(){
//        playersList = new ArrayList<>();
//        playersList.add(player1);
//        playersList.add(player2);
//        playersList.add(player3);
//    }
//
//    private void initializeCardImages(){
//        cardImagesList = new ArrayList<>();
//        cardImagesList.add(cardImage1);
//        cardImagesList.add(cardImage2);
//        cardImagesList.add(cardImage3);
//
//    }
//
//    private void initializeCardInfo(){
//        cardInfoList = new ArrayList<>();
//        cardInfoList.add(cardInfo1);
//        cardInfoList.add(cardInfo2);
//        cardInfoList.add(cardInfo3);
//    }
//
//    public void setUpPlayersCardsCorrespondence(Map<String, CardEnum> playersCardsCorrespondence){
//        int index = 0;
//        for (Map.Entry<String, CardEnum> entry : playersCardsCorrespondence.entrySet()) {
//            playersList.get(index).setText(entry.getKey());
//            cardImagesList.get(index).setImage(new Image(entry.getValue().getImgUrl()));
//
//            //set up the card info
//            ObservableList<Node> children = cardInfoList.get(index).getChildren();
//            //set up the card name
//            Label cardName = (Label) children.get(0);
//            cardName.setText(entry.getValue().getName());
//            //set up the card description
//            Label cardDescription = (Label) children.get(1);
//            cardDescription.setText(entry.getValue().getDescription());
//
//            index ++ ;
//        }
//
//    }

}
