package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.auxiliary.CellClusterData;
import it.polimi.ingsw.psp58.auxiliary.IslandData;
import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_GameStartedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_SpectatorGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_WorkerPlacementGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.*;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.CV_PlayerPlaceWorkerRequestEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.CV_WaitPreMatchGameEvent;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.TurnAction;
import it.polimi.ingsw.psp58.model.WorkerColors;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import it.polimi.ingsw.psp58.view.UI.GUI.Message;
import it.polimi.ingsw.psp58.view.UI.GUI.boardstate.*;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.exceptions.WorkerLockedException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
     * {@code boolean} is true if my turn just began and I need to show the popup.
     */
    private boolean myTurnFirstActionPopup = false;

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
    @FXML
    private GridPane board;

    /**
     * {@link VBox} that contains the info about the card of the local player.
     */
    @FXML
    private VBox myCardInfo;

    /**
     * {@link VBox} that contains the info about the first opponent's card.
     */
    @FXML
    private VBox cardInfo1;
    /**
     * {@link VBox} that contains the info about the second opponent's card.
     */
    @FXML
    private VBox cardInfo2;
    /**
     * {@link VBox} that contains the info about the current turn limitation.
     */
    @FXML
    private VBox turnInfo;

    //ACTION BUTTONS
    /**
     * {@link StackPane} that contains all the bottom buttons.
     */
    @FXML
    private StackPane commandGameButtonPane;
    /**
     * {@link Button} Move Button
     */
    @FXML
    private Button moveButton;
    /**
     * {@link Button} Build Button
     */
    @FXML
    private Button buildButton;
    /**
     * {@link Button} Pass Button
     */
    @FXML
    private Button passButton;

    //BLOCKS
    /**
     * {@link HBox} Level 1 HBox
     */
    @FXML
    private HBox l1Box;
    /**
     * {@link HBox} Level 2 HBox
     */
    @FXML
    private HBox l2Box;
    /**
     * {@link HBox} Level 3 HBox
     */
    @FXML
    private HBox l3Box;
    /**
     * {@link HBox} Dome HBox
     */
    @FXML
    private HBox domeBox;

    //WORKERS
    /**
     * {@link ImageView} Miniatures of Worker A
     */
    @FXML
    private ImageView workerSlotA;
    /**
     * {@link ImageView} Miniatures of Worker B
     */
    @FXML
    private ImageView workerSlotB;

    //TURN SEQUENCE
    /**
     * {@link Label} with Turn Sequence
     */
    @FXML
    private Label turnSequence;
    /**
     * {@link VBox} that contains the Turn Sequence.
     */
    @FXML
    private VBox turnSequenceVBox;

    //RightMessage
    /**
     * {@link Text} that contains the current message showed at the screen in the right side of the scene.
     */
    @FXML
    private Text rightMessage;
    /**
     * {@link Queue} of the messages that have to been showed in the right side of the scene.
     */
    private LinkedList<String> messagesQueue;

    /**
     * First method to call when the Game starts. The method begin the worker placement procedure.
     *
     * @param event The {@link CV_WorkerPlacementGameEvent} event that trigger the placement of the workers
     */
    public void init(CV_WorkerPlacementGameEvent event) {
        initializeIsland();

        this.myUsername = gui.getUsername();
        this.myColor = event.getPlayerWorkerColors().get(myUsername.toLowerCase());

        showTurnSequence(false);
        hideAllTurnInfo();

        String url = getWorkerUrl(myColor);

        workerSlotA.setImage(new Image(url));
        workerSlotB.setImage(new Image(url));

        resetWorkerStatus();

        setUpPlayersCardsCorrespondence(event.getPlayersCardsCorrespondence(), event.getPlayerWorkerColors());

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
        currentStateInstance.handleClick(colIndex, rowIndex);
    }

    /* ----------------------------------------------------------------------------------------------
                                         BUTTONS CLICK
       ----------------------------------------------------------------------------------------------*/

    /**
     * Method called each time the user click on the MOVE button.
     * It calls the {@code handleClickOnButton} method on the {@code currentStateInstance}.
     */
    public void moveButtonClick() {
        currentStateInstance.handleClick(TurnAction.MOVE);
    }

    /**
     * Method called each time the user click on the BUILD button.
     * It calls the {@code handleClickOnButton} method on the {@code currentStateInstance}.
     */
    public void buildButtonClick() {
        currentStateInstance.handleClick(TurnAction.BUILD);
    }

    /**
     * Method called each time the user click on the PASS button.
     * It calls the {@code handleClickOnButton} method on the {@code currentStateInstance}.
     */
    public void passButtonClick() {
        currentStateInstance.handleClick(TurnAction.PASS);
    }

    /**
     * Method called when an user, in Spectator State (mode) click the exit button.
     */
    public void exitSpectatorModeClick() {
        gui.closeApp();
    }

    /* ----------------------------------------------------------------------------------------------
                                         BLOCKS CLICK
       ----------------------------------------------------------------------------------------------*/

    /**
     * Method called each time the user click on the LEVEL1 block.
     * It calls the {@code handleClickOnButton} method on the {@code currentStateInstance}.
     */
    public void level1Click() {
        currentStateInstance.handleClick(BlockTypeEnum.LEVEL1);
    }

    /**
     * Method called each time the user click on the LEVEL2 block.
     * It calls the {@code handleClickOnButton} method on the {@code currentStateInstance}.
     */
    public void level2Click() {
        currentStateInstance.handleClick(BlockTypeEnum.LEVEL2);
    }

    /**
     * Method called each time the user click on the LEVEL3 block.
     * It calls the {@code handleClickOnButton} method on the {@code currentStateInstance}.
     */
    public void level3Click() {
        currentStateInstance.handleClick(BlockTypeEnum.LEVEL3);
    }

    /**
     * Method called each time the user click on the DOME block.
     * It calls the {@code handleClickOnButton} method on the {@code currentStateInstance}.
     */
    public void domeClick() {
        currentStateInstance.handleClick(BlockTypeEnum.DOME);
    }

    /* ----------------------------------------------------------------------------------------------
                                         STATUS and HANDLE METHODS
       ----------------------------------------------------------------------------------------------*/

    //STATUS

    /**
     * Reset the worker status in this class creating a new {@link WorkerStatus}
     */
    private void resetWorkerStatus() {
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
        disableAllActionButtons();
        resetWorkerStatus();
    }

    /**
     * This method disable all buttons and prepare the scene for spectator mode.
     */
    private void setSpectatorView() {
        commandGameButtonPane.getChildren().get(0).setVisible(false);
        commandGameButtonPane.getChildren().get(0).setDisable(true);
        commandGameButtonPane.getChildren().get(1).setVisible(true);
        commandGameButtonPane.getChildren().get(1).setDisable(false);
    }

    /**
     * Set the {@link WaitGameState} State waiting for a {@link CV_PlayerPlaceWorkerRequestEvent}.
     *
     * @param event A {@link CV_WorkerPlacementGameEvent} that notify the beginning of WorkerPlacement Phase.
     */
    public void handle(CV_WorkerPlacementGameEvent event) {
        GameStateAbstract nextState = new WaitGameState(this);
        setWaitingView();
        this.currentStateInstance = nextState;
        showTurnSequence(false);
    }

    /**
     * Set the {@link PlaceWorkerGameState} State and ask to the player to place a worker.
     *
     * @param event Contains the {@code workerID} of the worker to place.
     */
    public void handle(CV_PlayerPlaceWorkerRequestEvent event) {
        GameStateAbstract nextState = new PlaceWorkerGameState(gui, this);
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
        GameStateAbstract nextState = new WaitGameState(this);
        setWaitingView();
        this.currentStateInstance = nextState;
    }

    /**
     * Set the {@link WaitGameState} State waiting for another event.
     *
     * @param event A {@link CV_WaitMatchGameEvent} that notify a Wait Phase.
     */
    public void handle(CV_WaitMatchGameEvent event) {
        if (!event.getActingPlayer().equals(myUsername)) {
            GameStateAbstract nextState = new WaitGameState(this);
            setWaitingView();
            addMessageToQueueList(event.getEventDescription().toUpperCase() + " " + event.getActingPlayer().toUpperCase());
            this.currentStateInstance = nextState;
        }
    }


    /**
     * Set the {@link WaitGameState} State waiting for a {@link CV_NewTurnEvent}.
     *
     * @param event A {@link CV_GameStartedGameEvent} that notify the beginning of Game Phase.
     */
    public void handle(CV_GameStartedGameEvent event) {
        setGlowByNode(false, workerSlotA);
        setGlowByNode(false, workerSlotB);
        GameStateAbstract nextState = new WaitGameState(this);
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
        disableAllWorkerGlow();
        resetWorkerStatus();

        if (event.getCurrentPlayerUsername().equals(myUsername)) {
            currentStateInstance = new CommandGameState(gui, this);
            resetWorkerStatus();
            myTurnFirstActionPopup = true;
        } else {
            hideAllTurnInfo();
        }
    }

    /**
     * Set up the {@code turnInfo}
     *
     * @param event A {@link CV_TurnInfoEvent} that notify the what you able to do in your turn.
     */
    public void handle(CV_TurnInfoEvent event) {
        updateTurnInfo(event.getCanClimb(), event.getMovementsRemaining(), event.getBuildRemaining());
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
        if (myTurnFirstActionPopup) {
            displayPopupMessage("IT'S YOUR TURN!");
            myTurnFirstActionPopup = false;
        }
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
        if (workerRequested == Worker.IDs.A) {
            setGlowByNode(true, workerSlotA);
            addMessageToQueueList("Please place the FIRST worker");
        } else if (workerRequested == Worker.IDs.B) {
            setGlowByNode(true, workerSlotB);
            addMessageToQueueList("Please place the SECOND worker");
        }
    }

    /**
     * Prepare the Spectator Mode and set the State to Spectator.
     *
     * @param event Event with Spectator Info
     */
    public void handle(CV_SpectatorGameEvent event) {
        if (event.getSpectatorPlayer().equals(myUsername)) {
            GameStateAbstract nextState = new SpectatorGameState(this);
            setSpectatorView();
            this.currentStateInstance = nextState;
        } else {
            opponentPlayerSpectator(event.getSpectatorPlayer());
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
            default:
                button = passButton;
        }

        if (enable) {
            System.out.println("DEBUG: setButtonGlow on " + buttonToSet);

            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(14.5);
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

    /**
     * Enable the Green Glow around the Block that has been selected.
     *
     * @param enable     {@link Boolean} value, if true enable the glow, if false disable the glow.
     * @param blockToSet {@link BlockTypeEnum} that indicates the block to set.
     */
    public void setGreenBuildingBlocks(boolean enable, BlockTypeEnum blockToSet) {
        if (enable) {
            l1Box.setEffect(null);
            l2Box.setEffect(null);
            l3Box.setEffect(null);
            domeBox.setEffect(null);
        }
        HBox hBox;
        switch (blockToSet) {
            case LEVEL1:
                hBox = l1Box;
                break;
            case LEVEL2:
                hBox = l2Box;
                break;
            case LEVEL3:
                hBox = l3Box;
                break;
            case DOME:
            default:
                hBox = domeBox;
        }

        if (enable) {
            System.out.println("DEBUG: setButtonGlow on " + blockToSet);

            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(14.5);
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
        l1Box.setEffect(null);
        l2Box.setEffect(null);
        l3Box.setEffect(null);
        domeBox.setEffect(null);
    }

    /* ----------------------------------------------------------------------------------------------
                                         TURN SEQUENCE METHODS
       ----------------------------------------------------------------------------------------------*/

    /**
     * Show or hide the Turn Sequence Box.
     *
     * @param enable True to show the Turn Sequence, false to hide.
     */
    private void showTurnSequence(boolean enable) {
        turnSequenceVBox.setVisible(enable);
    }

    /**
     * Update the Turn Sequence list in the high-left corner.
     *
     * @param sequence {@code List<String>} containing the list of current players and their cards.
     */
    public void updateTurnSequence(List<String> sequence) {
        StringBuilder turnSequenceText = new StringBuilder();
        boolean first = true;
        for (String username : sequence) {
            if (first) {
                turnSequenceText.append(" ");
                first = false;
            } else {
                turnSequenceText.append(" << ");
            }
            turnSequenceText.append(username.toUpperCase());
            if (username.equals(myUsername)) {
                turnSequenceText.append(" (YOU)".toUpperCase());
            }
        }
        turnSequence.setText(turnSequenceText.toString());
        showTurnSequence(true);
    }

     /* ----------------------------------------------------------------------------------------------
                                         ISLAND METHODS
       ----------------------------------------------------------------------------------------------*/

    /**
     * Handle the Island Update: memorize the event and update the scene.
     *
     * @param event An {@link  CV_IslandUpdateEvent} contains the last update of the island.
     */
    public void handle(CV_IslandUpdateEvent event) {
        updateIsland(event.getNewIsland());
    }

    /**
     * Get last update of the Island.
     *
     * @return {@link IslandData} last update of the island.
     */
    public IslandData getLastIslandUpdate() {
        return lastIslandUpdate;
    }

    /**
     * Generates a StackPane containing the information of the {@link CellClusterData} passed as input
     * @param cellClusterData the {@link CellClusterData} to visualize
     * @return the stackPane with the information of the cellClusterData
     */
    private StackPane generateStackPane(CellClusterData cellClusterData){
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
        return stackPane;
    }

    /**
     * This method refresh and update the Island on the screen.
     *
     * @param island An {@link IslandData} with the last update of the island.
     */
    public void updateIsland(IslandData island) {
        setLastIslandUpdate(island);

        Platform.runLater(() -> {
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    CellClusterData cellClusterData = island.getCellCluster(x, y);
                    StackPane stackPane = generateStackPane(cellClusterData);

                    GridPane.setConstraints(stackPane, x, y);

                    board.getChildren().remove(lastGridPane[x][y]);
                    board.getChildren().add(stackPane);

                    lastGridPane[x][y] = stackPane;

                    if (cellClusterData.getWorkerOnTop() != null && workerStatus.isAlreadySelectedWorker() &&
                            cellClusterData.getWorkerColor().equals(myColor) &&
                            workerStatus.getSelectedWorker().equals(cellClusterData.getWorkerOnTop())) {

                        setWorkerGlow(true, x, y);
                    }
                }
            }
        });

    }

    /**
     * Set last update of the Island.
     *
     * @param lastIslandUpdate {@link IslandData} last update of the island.
     */
    public void setLastIslandUpdate(IslandData lastIslandUpdate) {
        this.lastIslandUpdate = lastIslandUpdate;
    }

    /**
     * This method create the Island on the Screen.
     */
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

    /**
     * Create the right {@link ImageView} based on the given {@link CellClusterData}.
     *
     * @param cellClusterData CellCluster to obtain the {@link ImageView}.
     * @return {@link ImageView} of the given {@link CellClusterData}.
     */
    public ImageView getBlockImage(CellClusterData cellClusterData) {
        String url = getUrlFromCellCluster(cellClusterData);

        ImageView image = new ImageView();
        image.setFitWidth(100);
        image.setFitHeight(100);
        image.setPreserveRatio(true);
        image.setImage(new Image(url));
        return image;
    }

    /**
     * Activate the Glow of possible Actions on the Game Board.
     *
     * @param panelPositions Index of panel to Glow.
     * @param actionClicked  Action clicked to choose the color.
     */
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
        }
    }

    /**
     * Deactivate all the glow for possible Actions on the Game Board.
     */
    public void deactivateAllGlowOnPanels() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                StackPane stackPane = (StackPane) getNodeByRowColumnIndex(i, j);
                stackPane.getChildren().get(1).setVisible(false);
            }
        }
    }

    /**
     * This method create the Url of the Resource based on the {@link CellClusterData}.
     *
     * @param cellClusterData {@link CellClusterData} of the block to print.
     * @return The Url of the requested resource.
     */
    public String getUrlFromCellCluster(CellClusterData cellClusterData) {
        String url = "/images/cellcluster/";

        int[] blocks = cellClusterData.getBlocks();
        boolean domeOnTop = cellClusterData.isDomeOnTop();

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
                default:
                    System.out.println("ERROR");
            }
            if (domeOnTop) {
                url = url + "_DOME";
            }
            url = url + ".png";
        }
        return url;
    }

    /**
     * Get the Board {@link Node} at the give indexes
     *
     * @param row    Row index
     * @param column Column index
     * @return The Node at given indexes
     */
    public Node getNodeByRowColumnIndex(final int row, final int column) {
        return lastGridPane[row][column];
    }

    /* ----------------------------------------------------------------------------------------------
                                         UTILITY METHODS
       ----------------------------------------------------------------------------------------------*/

    /**
     * Set the GUI.
     *
     * @param gui Main {@link GUI} class.
     */
    public void setGui(GUI gui) {
        this.gui = gui;
    }

    /**
     * Displays a message to the user with a popup panel
     *
     * @param message the message you would like to display
     */
    public void displayPopupMessage(String message) {
        Message.show(250, 200, message.toUpperCase(), gui.getStage());
    }

    /**
     * Add a low priority message to a Queue List to show on the right pane.
     *
     * @param messageToShow {@link String} to print.
     */
    public void addMessageToQueueList(String messageToShow) {
        messagesQueue.add(messageToShow);
        showMessageOntRightPane();
    }

    /**
     * Show the first message of the queue on the right pane
     */
    private void showMessageOntRightPane() {
        rightMessage.setText(messagesQueue.remove());
    }

    /* ----------------------------------------------------------------------------------------------
                                         WORKER UTILITY AND METHODS
       ----------------------------------------------------------------------------------------------*/

    /**
     * Get the Url for the image with the right worker
     *
     * @param color {@code WorkerColor} of the Worker
     * @return The Url of the Image of the Worker with the given Color.
     */
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

    /**
     * Set the Glow for the Worker with Coordinates.
     *
     * @param enable {@code True} to enable, {@code false} to disable.
     * @param x      X Coordinate
     * @param y      Y Coordinate
     */
    public void setWorkerGlow(boolean enable, int x, int y) {
        Node node = getNodeByRowColumnIndex(x, y);
        System.out.println("DEBUG: setWorkerGlow " + enable + " " + x + " " + y);
        setGlowByNode(enable, node);
    }

    /**
     * Set the Glow for the Worker with {@code WorkerID}.
     *
     * @param enable   {@code True} to enable, {@code false} to disable.
     * @param workerID ID of the worker to set.
     */
    public void setWorkerGlow(boolean enable, Worker.IDs workerID) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                CellClusterData cellClusterData = lastIslandUpdate.getCellCluster(i, j);
                if (cellClusterData.getWorkerOnTop() != null && cellClusterData.getWorkerOnTop().equals(workerID) && cellClusterData.getUsernamePlayer().equals(myUsername)) {
                    setWorkerGlow(enable, i, j);
                }
            }
        }
    }

    /**
     * Set the Glow for the Worker with {@link Node}.
     *
     * @param enable {@code True} to enable, {@code false} to disable.
     * @param node   {@link Node} where to find the Worker
     */
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
        }
    }

    /**
     * Deactivate the glow on all the workers.
     */
    private void disableAllWorkerGlow() {
        if (lastIslandUpdate != null) {
            setWorkerGlow(false, Worker.IDs.A);
            setWorkerGlow(false, Worker.IDs.B);
        }
    }

    /**
     * Get WorkerID
     *
     * @param x X Coordinate
     * @param y Y Coordinate
     * @return WorkerID
     */
    public Worker.IDs getWorkerID(int x, int y) {
        return lastIslandUpdate.getCellCluster(x, y).getWorkerOnTop();
    }

    /**
     * Get the {@link ImageView} of the Worker.
     *
     * @param cellClusterData CellCluster with the worker.
     * @return {@link ImageView} of the Worker
     */
    public ImageView getWorkerImage(CellClusterData cellClusterData) {
        String url = getWorkerUrl(cellClusterData.getWorkerColor());
        ImageView workerImage = new ImageView();
        workerImage.setFitWidth(100);
        workerImage.setFitHeight(100);
        workerImage.setPreserveRatio(true);
        workerImage.setImage(new Image(url));
        return workerImage;
    }

    /* ----------------------------------------------------------------------------------------------
                                        CARDS & INFO - UTILITY AND METHODS
       ----------------------------------------------------------------------------------------------*/

    /**
     * Set all the Info of Cards and Players
     *
     * @param playersCardsCorrespondence {@code Map<String, CardEnum>} with info about Player names and Cards.
     * @param playerWorkerColors         {@code Map<String, WorkerColors>} with info about Player names and Colors.
     */
    public void setUpPlayersCardsCorrespondence(Map<String, CardEnum> playersCardsCorrespondence, Map<String, WorkerColors> playerWorkerColors) {
        int index = 0;
        for (Map.Entry<String, CardEnum> entry : playersCardsCorrespondence.entrySet()) {
            if (entry.getKey().equals(myUsername)) { //set up local user info
                setUpMyCardInfo(entry.getValue());
            } else { //set up the opponents' info
                setUpOpponentsCardInfo(index, entry.getKey(), entry.getValue(), playerWorkerColors.get(entry.getKey()));
                index++;
            }
        }
        if (playersCardsCorrespondence.values().size() == 2) {
            cardInfo2.setVisible(false);
        }
    }

    /**
     * Set my info on left pane
     *
     * @param card My Card name
     */
    public void setUpMyCardInfo(CardEnum card) {
        //set up info of local user
        //username
        StackPane pane = (StackPane) myCardInfo.getChildren().get(0);
        Label cardName = (Label) pane.getChildren().get(1);
        cardName.setText(card.getName());

        //card image and description
        //image
        HBox hBox = (HBox) myCardInfo.getChildren().get(1);
        Image myCard = new Image(card.getImgUrl());
        ImageView imageView = (ImageView) hBox.getChildren().get(0);
        imageView.setImage(myCard);

        //description
        Text cardDescription = (Text) hBox.getChildren().get(1);

        setupCardDescription(cardDescription, card, true);
    }

    /**
     * Set all the info of opposite players.
     *
     * @param index       Index of opposite player.
     * @param player      Name of the player.
     * @param card        Card of the player
     * @param workerColor Color of the player.
     */
    public void setUpOpponentsCardInfo(int index, String player, CardEnum card, WorkerColors workerColor) {
        VBox actualVBox;
        if (index == 0) {
            actualVBox = cardInfo1;
        } else {
            actualVBox = cardInfo2;
        }
        //set up the name
        HBox upperHBox = (HBox) actualVBox.getChildren().get(0);
        Label name = (Label) upperHBox.getChildren().get(0);
        name.setText(player.toUpperCase());

        ImageView smallWorker = (ImageView) upperHBox.getChildren().get(1);
        String url = getWorkerUrl(workerColor);
        smallWorker.setImage(new Image(url));

        //set up the image of the card
        HBox lowerHBox = (HBox) actualVBox.getChildren().get(1);
        ImageView cardImage = (ImageView) lowerHBox.getChildren().get(0);
        Image image = new Image(card.getImgUrl());
        cardImage.setImage(image);

        //set up the name and the description of the card
        VBox vBox = (VBox) lowerHBox.getChildren().get(1);
        Label cardName = (Label) vBox.getChildren().get(0);
        cardName.setText(card.getName());
        Text cardDescription = (Text) vBox.getChildren().get(1);
        setupCardDescription(cardDescription, card, false);
    }

    /**
     * Set spectator info for opposite player.
     *
     * @param spectatorPlayer Name of spectator player
     */
    private void opponentPlayerSpectator(String spectatorPlayer) {
        VBox actualVBox;
        HBox upperHBox = (HBox) cardInfo1.getChildren().get(0);
        Label name1 = (Label) upperHBox.getChildren().get(0);

        if (name1.getText().equals(spectatorPlayer)) {
            actualVBox = cardInfo1;
        } else {
            actualVBox = cardInfo2;
        }

        Label name = (Label) upperHBox.getChildren().get(0);
        name.setText(spectatorPlayer.toUpperCase() + " - Spectator");
        actualVBox.setDisable(true);
    }

    /**
     * Set the proper Card description with the right size.
     *
     * @param cardDescription {@link Text} field that contains the card description.
     * @param card            Card to show.
     * @param isMyCard        {@code boolean} value, true if the card is mine, false otherwise.
     */
    private void setupCardDescription(Text cardDescription, CardEnum card, boolean isMyCard) {
        int minSize = 12;
        if (isMyCard) {
            minSize = 15;
        }
        String fontName = cardDescription.getFont().getName();
        int descriptionLength = card.getDescription().length();
        if (descriptionLength > 70) {
            if (descriptionLength > 100) {
                if (descriptionLength > 120) {
                    // descriptionLength > 120
                    cardDescription.setFont(Font.font(fontName, minSize));
                } else {
                    // 100 < descriptionLength <= 120
                    cardDescription.setFont(Font.font(fontName, minSize + 2));
                }
            } else {
                // 70 < descriptionLength <= 100
                cardDescription.setFont(Font.font(fontName, minSize + 4));
            }
        } else {
            // descriptionLength <=70
            cardDescription.setFont(Font.font(fontName, minSize + 6));
        }
        cardDescription.setText(card.getDescription());
    }

    /**
     * Set info about the turn, such as Number of Possible Movements ecc.
     *
     * @param climb             {@link String} can Climb.
     * @param numberOfMovements {@code int} number of movements.
     * @param numberOfBuilding  {@code int} number of buildings.
     */
    public void updateTurnInfo(String climb, int numberOfMovements, int numberOfBuilding) {
        //set if can climb
        Label canClimb = (Label) turnInfo.getChildren().get(1);
        canClimb.setText("can climb: " + climb.toUpperCase());
        canClimb.setVisible(true);

        //set number of moves
        Label numberOfMoves = (Label) turnInfo.getChildren().get(2);
        numberOfMoves.setText("available MOVE: " + numberOfMovements);
        numberOfMoves.setVisible(true);

        //set number of builds
        Label numberOfBuilds = (Label) turnInfo.getChildren().get(3);
        numberOfBuilds.setText("available BUILD: " + numberOfBuilding);
        numberOfBuilds.setVisible(true);
    }

    /**
     * Hide all the turn info
     */
    public void hideAllTurnInfo() {
        Label canClimb = (Label) turnInfo.getChildren().get(1);
        canClimb.setVisible(false);

        Label numberOfMoves = (Label) turnInfo.getChildren().get(2);
        numberOfMoves.setVisible(false);

        Label numberOfBuilds = (Label) turnInfo.getChildren().get(3);
        numberOfBuilds.setVisible(false);
    }
}
