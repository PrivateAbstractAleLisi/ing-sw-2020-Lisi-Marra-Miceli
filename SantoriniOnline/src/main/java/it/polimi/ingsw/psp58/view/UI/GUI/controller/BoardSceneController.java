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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.*;

/**
 * This class handle the entire match using the Pattern State.
 * It receives lots of events and updates the board on the screen.
 */

public class BoardSceneController {
    /**
     * Color of my workers.
     */
    private WorkerColors myColor;
    /**
     * {@link String} with my username.
     */
    private String myUsername = "";
    /**
     * Main {@link GUI} object.
     */
    private GUI gui;

    /**
     * Last island update, overwritten many times during the match.
     */
    private IslandData lastIslandUpdate;
    /**
     * The last {@link GridPane} content saved as {@link StackPane} matrix.
     */
    private StackPane[][] lastGridPane;

    //STATE PATTERN
    /**
     * Current selected worker.
     */
    private WorkerStatus workerStatus;
    /**
     * Current State of the game for this player.
     */
    private GameStateAbstract currentStateInstance;
    /**
     * The game board with all the elements.
     */
    public GridPane board;

    /**
     * {@link VBox} that contains the info about the first player's card.
     */
    public VBox cardInfo1;
    /**
     * {@link VBox} that contains the info about the second player's card.
     */
    public VBox cardInfo2;
    /**
     * {@link VBox} that contains the info about the third player's card.
     */
    public VBox cardInfo3;

    /**
     * List of {@link VBox} that contains all the info about the cards.
     */
    private List<VBox> cardInfoList;

    /**
     * {@link ImageView} of first player's card.
     */
    public ImageView cardImage1;
    /**
     * {@link ImageView} of second player's card.
     */
    public ImageView cardImage2;
    /**
     * {@link ImageView} of third player's card.
     */
    public ImageView cardImage3;

    /**
     * List of {@link ImageView} that contains all the cards images used by the players.
     */
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
    /**
     * {@link Text} that contains the current message showed at the screen in the right side of the scene.
     */
    public Text rightMessage;
    /**
     * {@link Queue} of the messages that have to been showed in the right side of the scene.
     */
    LinkedList<String> messagesQueue;

    /**
     * First method to call when the Game starts. The method begin the worker placement procedure.
     *
     * @param event The {@link CV_WorkerPlacementGameEvent} event that trigger the placement of the workers
     */
    public void init(CV_WorkerPlacementGameEvent event) {
        initializeIsland();

        updateTurnSequence(event.getTurnSequence());
        this.myUsername = gui.getUsername();
        myColor = event.getPlayerWorkerColors().get(myUsername.toLowerCase());

        String url = getWorkerUrl(myColor);

        workerSlotA.setImage(new Image(url));
        workerSlotB.setImage(new Image(url));

        resetWorkerStatus();

        //update blocks counter to starting state
        updateBlocksCounter(22, 18, 14, 18);

        messagesQueue = new LinkedList<>();
    }

    /* ----------------------------------------------------------------------------------------------
                                         BOARD CLICK
       ----------------------------------------------------------------------------------------------*/

    /**
     * Method called each time the user click on the board.
     * It gets the coordinates of the click and calls the {@code handleClickOnButton} method on the {@code currentStateInstance}.
     *
     * @param mouseEvent Event with information about the mouse click
     */
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

    /**
     * Method called each time the user click on the MOVE button.
     * It calls the {@code handleClickOnButton} method on the {@code currentStateInstance}.
     */
    public void moveButtonClick() {
        currentStateInstance.handleClickOnButton(TurnAction.MOVE);
    }

    /**
     * Method called each time the user click on the BUILD button.
     * It calls the {@code handleClickOnButton} method on the {@code currentStateInstance}.
     */
    public void buildButtonClick() {
        currentStateInstance.handleClickOnButton(TurnAction.BUILD);
    }

    /**
     * Method called each time the user click on the PASS button.
     * It calls the {@code handleClickOnButton} method on the {@code currentStateInstance}.
     */
    public void passButtonClick() {
        currentStateInstance.handleClickOnButton(TurnAction.PASS);
    }

    /* ----------------------------------------------------------------------------------------------
                                         BLOCKS CLICK
       ----------------------------------------------------------------------------------------------*/

    /**
     * Method called each time the user click on the LEVEL1 block.
     * It calls the {@code handleClickOnButton} method on the {@code currentStateInstance}.
     */
    public void level1Click() {
        currentStateInstance.handleClickOnButton(BlockTypeEnum.LEVEL1);
    }

    /**
     * Method called each time the user click on the LEVEL2 block.
     * It calls the {@code handleClickOnButton} method on the {@code currentStateInstance}.
     */
    public void level2Click() {
        currentStateInstance.handleClickOnButton(BlockTypeEnum.LEVEL2);
    }

    /**
     * Method called each time the user click on the LEVEL3 block.
     * It calls the {@code handleClickOnButton} method on the {@code currentStateInstance}.
     */
    public void level3Click() {
        currentStateInstance.handleClickOnButton(BlockTypeEnum.LEVEL3);
    }

    /**
     * Method called each time the user click on the DOME block.
     * It calls the {@code handleClickOnButton} method on the {@code currentStateInstance}.
     */
    public void domeClick() {
        currentStateInstance.handleClickOnButton(BlockTypeEnum.DOME);
    }

    /* ----------------------------------------------------------------------------------------------
                                         STATUS and HANDLE METHODS
       ----------------------------------------------------------------------------------------------*/

    //STATUS

    /**
     * Reset the worker status in this class creating a new {@link WorkerStatus}
     */
    public void resetWorkerStatus() {
        workerStatus = new WorkerStatus();
    }

    /**
     * Return the {@link WorkerStatus} of the turn
     *
     * @return The {@link WorkerStatus} of the turn
     */
    public WorkerStatus getWorkerStatus() {
        return workerStatus;
    }

    /**
     * It prepare the waiting view disabling all the buttons and the glow
     */
    public void setWaitingView() {
        //Todo worker glow non si disattiva
        disableAllActionButtons();
        resetWorkerStatus();
        disableWorkerGlow();
    }

    /**
     * Hide the left box with the worker icons
     */
    public void hideWorkerPlacementBox() {
        //workerPrePlaceHBox.setVisible(false);
    }

    /**
     * Set the {@link WaitGameState} State waiting for a {@link CV_PlayerPlaceWorkerRequestEvent}.
     *
     * @param event A {@link CV_WorkerPlacementGameEvent} that notify the beginning of WorkerPlacement Phase.
     */
    public void handle(CV_WorkerPlacementGameEvent event) {
        GameStateAbstract nextState = new WaitGameState(gui);
        setWaitingView();
        this.currentStateInstance = nextState;
    }

    /**
     * Set the {@link PlaceWorkerGameState} State and ask to the player to place a worker.
     *
     * @param event Contains the {@code workerID} of the worker to place.
     */
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

    /**
     * Set the {@link WaitGameState} State waiting for another event.
     *
     * @param event A {@link CV_WaitPreMatchGameEvent} that notify a Wait Phase.
     */
    public void handle(CV_WaitPreMatchGameEvent event) {
        GameStateAbstract nextState = new WaitGameState(event, gui);
        setWaitingView();
        this.currentStateInstance = nextState;
    }

    /**
     * Set the {@link WaitGameState} State waiting for a {@link CV_NewTurnEvent}.
     *
     * @param event A {@link CV_GameStartedGameEvent} that notify the beginning of Game Phase.
     */
    public void handle(CV_GameStartedGameEvent event) {
        setGlowByNode(false, workerSlotA);
        setGlowByNode(false, workerSlotB);
        GameStateAbstract nextState = new WaitGameState(gui);
        setWaitingView();
        this.currentStateInstance = nextState;
        addMessageToQueueList("Game is starting!");
    }

    /**
     * Set the {@link CommandGameState} State for the acting player and update the {@code TurnSequence} for all the players.
     *
     * @param event A {@link CV_NewTurnEvent} that notify the beginning of the new turn.
     */
    public void handle(CV_NewTurnEvent event) {
        updateTurnSequence(event.getTurnRotation());

        if (event.getCurrentPlayerUsername().equals(myUsername)) {
            currentStateInstance = new CommandGameState(event, gui, this);
            resetWorkerStatus();
            addMessageToQueueList("IT'S YOUR TURN!");
        }
    }

    /**
     * Enable the buttons and notify {@code currentStateInstance} to wait an user input.
     *
     * @param event Contains the {@code availableActions} for this player.
     */
    public void handle(CV_CommandRequestEvent event) {
        enableActionButtons(event.getAvailableActions());
        disableAllGreenActionButton();
        disableAllGreenBuildingBlock();
        deactivateAllGlowOnPanels();

        //if worker is not locked, it will be deleted
        workerStatus.resetSelectedWorker();

        currentStateInstance.updateFromServer(event);
    }

    /**
     * Notify the {@code currentStateInstance} that the player command has been executed
     *
     * @param event {@link CV_CommandExecutedGameEvent} means that the command has been executed
     */
    public void handle(CV_CommandExecutedGameEvent event) {
        System.out.println("DEBUG: CV_CommandExecutedGameEvent event has arrived");
        currentStateInstance.updateFromServer(event);
    }

    /**
     * Prepare the Game scene and ask to the player to place a worker.
     *
     * @param workerRequested Contains the worker to place.
     */
    public void handleWorkerPlacement(Worker.IDs workerRequested) {
        disableAllActionButtons();
        setGlowByNode(false, workerSlotA);
        setGlowByNode(false, workerSlotB);
        switch (workerRequested) {
            case A:
                setGlowByNode(true, workerSlotA);
                addMessageToQueueList("Please place the FIRST worker");
                break;
            case B:
                setGlowByNode(true, workerSlotB);
                addMessageToQueueList("Please place the SECOND worker");
                break;
        }
    }

    /* ----------------------------------------------------------------------------------------------
                                         ACTION BUTTONS METHODS
       ----------------------------------------------------------------------------------------------*/

    /**
     * Enable the available Action Buttons.
     *
     * @param availableActions Contains the available actions for this player at the moment.
     */
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

    /**
     * Disable the Action Buttons at once.
     */
    public void disableAllActionButtons() {
        moveButton.setDisable(true);
        buildButton.setDisable(true);
        passButton.setDisable(true);
    }

    /**
     * Enable the Green Glow around the button that has been selected.
     *
     * @param enable      {@link Boolean} value, if true enable the glow, if false disable the glow.
     * @param buttonToSet {@link TurnAction} that indicates the button to set.
     */
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

    /**
     * Disable the Green Glow around all the Action Buttons at once.
     */
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

    /**
     * Enable the Green Glow around the Block that has been selected.
     *
     * @param enable     {@link Boolean} value, if true enable the glow, if false disable the glow.
     * @param blockToSet {@link BlockTypeEnum} that indicates the block to set.
     */
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

    /**
     * Disable the Green Glow around all the Building Blocks at once.
     */
    public void disableAllGreenBuildingBlock() {
        L1Box.setEffect(null);
        L2Box.setEffect(null);
        L3Box.setEffect(null);
        DomeBox.setEffect(null);
    }

    /* ----------------------------------------------------------------------------------------------
                                         TURN SEQUENCE METHODS
       ----------------------------------------------------------------------------------------------*/

    /**
     * Update the Turn Sequence list in the high-left corner.
     * @param turnSequenceFromEvent {@link HashMap} containing the list of current players and their cards.
     */
    public void updateTurnSequence(Map<String, CardEnum> turnSequenceFromEvent) {

        //fill turn sequence
        Map<String, CardEnum> sequence = turnSequenceFromEvent;
        List<String> turnSequence = new ArrayList<>();
        for (String player : sequence.keySet()) {
            turnSequence.add(player);
        }
        updateTurnSequence(turnSequence);
    }

    /**
     * Update the Turn Sequence list in the high-left corner.
     * @param sequence {@code List<String>} containing the list of current players and their cards.
     */
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

    public void deactivateAllGlowOnPanels() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                StackPane stackPane = (StackPane) getNodeByRowColumnIndex(i, j);
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

    public void setWorkerGlow(boolean enable, int x, int y) {
        Node node = getNodeByRowColumnIndex(x, y);
        System.out.println("DEBUG: setWorkerGlow" + enable + x + " " + y);
        setGlowByNode(enable, node);
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

    private void setGlowByNode(boolean enable, Node node) {
        if (enable) {
            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(14.5);
            dropShadow.setWidth(30);
            dropShadow.setHeight(30);
            dropShadow.setOffsetX(0);
            dropShadow.setOffsetY(0);
            dropShadow.setSpread(0.6);
            dropShadow.setColor(Color.rgb(255, 234, 5));
            node.setEffect(dropShadow);
        } else {
            node.setEffect(null);
            node = null;
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
