package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import it.polimi.ingsw.psp58.auxiliary.IslandData;
import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandExecutedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandRequestEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_NewTurnEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.VC_PlayerCommandGameEvent;
import it.polimi.ingsw.psp58.model.TurnAction;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.BoardSceneController;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.WorkerStatus;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.exceptions.WorkerLockedException;

import static it.polimi.ingsw.psp58.view.UI.GUI.boardstate.GameStateEnum.*;


public class CommandGameState extends GameStateAbstract {
    private CV_NewTurnEvent eventArrived;
    private CV_CommandRequestEvent lastCommandRequest;
    private final BoardSceneController boardSceneController;
    private final GUI gui;
    private final String myUsername;

    private GameStateEnum state;

    private BlockTypeEnum blockToBuild = null;

    public CommandGameState(CV_NewTurnEvent eventArrived, GUI gui, BoardSceneController boardController) {
        this.gui = gui;
        boardSceneController = boardController;
        this.eventArrived = eventArrived;
        myUsername = gui.getUsername();
        state = CLEAN_TURN;
        boardController.hideWorkerPlacementBox();
    }

    @Override
    public GameStateEnum getState() {
        return state;
    }

    @Override
    public ControllerGameEvent handleClick(String username, int x, int y, Worker.IDs workerID, GameStateEnum state) {
        switch (state) {
            case SELECT_WORKER:
//                boardSceneController.showPossibleBlockAction(workerID);
                break;
            case MOVE:
                return new VC_PlayerCommandGameEvent("", TurnAction.MOVE, username, new int[]{x, y}, workerID, null);
            case BUILD:
                return new VC_PlayerCommandGameEvent("", TurnAction.BUILD, username, new int[]{x, y}, workerID, null);
            case WAIT_COMMAND_BUTTON_:
                displayPopupError("please select a command first");
        }
        return null;
    }

    @Override
    public void handleClickOnButton(int x, int y) {
        //gets the id of the clicked worker, if not ok contain a null
        Worker.IDs workerIdInCellCluster = boardSceneController.getWorkerID(x, y);
        WorkerStatus actualWorkerStatus = boardSceneController.getWorkerStatus();
        VC_PlayerCommandGameEvent commandEvent;
        int[] position;
        switch (state) {
            case CLEAN_TURN:
                if (workerIdInCellCluster != null && !actualWorkerStatus.isAlreadySelectedWorker()) {
                    String playerOfWorker = boardSceneController.getLastIslandUpdate().getCellCluster(x, y).getUsernamePlayer();
                    boolean isMyWorker = playerOfWorker.equals(myUsername);
                    if (isMyWorker) {
                        try {
                            actualWorkerStatus.setSelectedWorker(workerIdInCellCluster);
                            boardSceneController.setWorkerGlow(true, x, y);
                            state = WORKER_SELECTED;
                        } catch (WorkerLockedException e) {
                            System.out.println("Worker already locked");
                            state = WORKER_LOCKED;
                        }
                    } else {
                        System.out.println("ERROR - " + state + " - Please select one of your workers");
                        displayPopupError("Please select one of your workers");
                    }
                } else {
                    System.out.println("ERROR - " + state + " - Please select one of your workers");
                    displayPopupError("Please select one of your workers");
                }
                break;
            case WORKER_SELECTED:
                if (workerIdInCellCluster != null && isMyWorker(x, y)) {
                    if (actualWorkerStatus.getSelectedWorker().equals(workerIdInCellCluster) && !actualWorkerStatus.isWorkerLocked()) {
                        actualWorkerStatus.resetSelectedWorker();
                        boardSceneController.setWorkerGlow(false, x, y);
                        state = CLEAN_TURN;
                    } else {
                        boardSceneController.setWorkerGlow(false, actualWorkerStatus.getSelectedWorker());
                        try {
                            actualWorkerStatus.setSelectedWorker(workerIdInCellCluster);
                            boardSceneController.setWorkerGlow(true, x, y);
                            state = WORKER_SELECTED;
                        } catch (WorkerLockedException e) {
                            System.out.println("Worker already locked");
                            state = WORKER_LOCKED;
                        }
                    }
                } else {
                    System.out.println("ERROR - " + state + " - Please select an available action or your workers");
                    displayPopupError("Please select an available action or your workers");
                }
                break;
            case WORKER_LOCKED:
                System.out.println("ERROR - " + state + " - Please select an available action or your workers");
                displayPopupError("Please select an available action or your workers");
                break;
            case MOVE_SELECTED:
                position = new int[2];
                position[0] = x;
                position[1] = y;

                commandEvent = new VC_PlayerCommandGameEvent("", TurnAction.MOVE, myUsername, position, actualWorkerStatus.getSelectedWorker(), blockToBuild);
                if (isCommandEventValid(commandEvent)) {
                    gui.sendEvent(commandEvent);
                    state = selectMsgSentState(actualWorkerStatus);
                    boardSceneController.disableAllActionButtons();
                } else {
                    displayPopupError("Error in the creation of the event");
                }
                break;
            case BUILD_SELECTED:
                //it's the same case of BUILDING_BLOCK_SELECTED
            case BUILDING_BLOCK_SELECTED:
                position = new int[2];
                position[0] = x;
                position[1] = y;

                commandEvent = new VC_PlayerCommandGameEvent("", TurnAction.BUILD, myUsername, position, actualWorkerStatus.getSelectedWorker(), blockToBuild);
                if (isCommandEventValid(commandEvent)) {
                    gui.sendEvent(commandEvent);
                    state = selectMsgSentState(actualWorkerStatus);
                    boardSceneController.disableAllActionButtons();
                } else {
                    displayPopupError("Error in the creation of the event");
                }
                break;
            case MSG_SENT_UNLOCKED:
                //Same case of MSG_SENT_LOCKED
            case MSG_SENT_LOCKED:
                System.out.println("ERROR - " + state + " - Please wait. The server is handling your request");
                displayPopupError("Please wait. The server is handling your request");
                break;
        }
    }

    @Override
    public void handleClickOnButton(TurnAction buttonPressed) {
        WorkerStatus actualWorkerStatus = boardSceneController.getWorkerStatus();
        VC_PlayerCommandGameEvent commandEvent;
        switch (state) {
            case CLEAN_TURN:
                if (buttonPressed == TurnAction.PASS) {
                    setGreenActionButton(true, TurnAction.PASS);
                    commandEvent = new VC_PlayerCommandGameEvent("", TurnAction.PASS, myUsername, null, null, null);
                    if (isCommandEventValid(commandEvent)) {
                        gui.sendEvent(commandEvent);
                        state = selectMsgSentState(actualWorkerStatus);
                        boardSceneController.disableAllActionButtons();
                    } else {
                        displayPopupError("Error in the creation of the event");
                    }
                } else {
                    System.out.println("ERROR - " + state + " - Please select one of your workers");
                    displayPopupError("Please select one of your workers");
                }
                break;
            case WORKER_SELECTED:
                switch (buttonPressed) {
                    case MOVE:
                        setGreenActionButton(true, TurnAction.MOVE);
                        state = MOVE_SELECTED;
                        showPossibleBlockAction(actualWorkerStatus.getSelectedWorker(),TurnAction.MOVE);
                        break;
                    case BUILD:
                        setGreenActionButton(true, TurnAction.BUILD);
                        showPossibleBlockAction(actualWorkerStatus.getSelectedWorker(),TurnAction.BUILD);
                        state = BUILD_SELECTED;
                        break;
                    case PASS:
                        System.out.println("ERROR - " + state + " - Please deselect the worker and press again");
                        displayPopupError("Please deselect the worker and press again");
                        break;
                }
                break;
            case WORKER_LOCKED:
                switch (buttonPressed) {
                    case MOVE:
                        setGreenActionButton(true, TurnAction.MOVE);
                        showPossibleBlockAction(actualWorkerStatus.getSelectedWorker(),TurnAction.MOVE);
                        state = MOVE_SELECTED;
                        break;
                    case BUILD:
                        setGreenActionButton(true, TurnAction.BUILD);
                        showPossibleBlockAction(actualWorkerStatus.getSelectedWorker(),TurnAction.BUILD);
                        state = BUILD_SELECTED;
                        break;
                    case PASS:
                        commandEvent = new VC_PlayerCommandGameEvent("", TurnAction.PASS, myUsername, null, null, null);
                        if (isCommandEventValid(commandEvent)) {
                            gui.sendEvent(commandEvent);
                            state = selectMsgSentState(actualWorkerStatus);
                            boardSceneController.disableAllActionButtons();
                        } else {
                            displayPopupError("Error in the creation of the event");
                        }
                        break;
                }
                break;
            case MOVE_SELECTED:
                switch (buttonPressed) {
                    case MOVE:
                        setGreenActionButton(false, TurnAction.MOVE);
                        boardSceneController.deactivateAllGlowOnPanels();
                        state = selectWorkerState(actualWorkerStatus);
                        break;
                    case BUILD:
                        setGreenActionButton(false, TurnAction.MOVE);
                        setGreenActionButton(true, TurnAction.BUILD);
                        boardSceneController.deactivateAllGlowOnPanels();
                        showPossibleBlockAction(actualWorkerStatus.getSelectedWorker(),TurnAction.BUILD);
                        state = BUILD_SELECTED;
                        break;
                    case PASS:
                        if (actualWorkerStatus.isWorkerLocked()) {
                            commandEvent = new VC_PlayerCommandGameEvent("", TurnAction.PASS, myUsername, null, null, null);
                            if (isCommandEventValid(commandEvent)) {
                                gui.sendEvent(commandEvent);
                                state = selectMsgSentState(actualWorkerStatus);
                                boardSceneController.disableAllActionButtons();
                            } else {
                                displayPopupError("Error in the creation of the event");
                            }
                        } else {
                            System.out.println("ERROR - " + state + " - Please deselect the worker and press again");
                            displayPopupError("Please deselect the worker and press again");
                        }
                        break;
                }
                break;
            case BUILD_SELECTED:
                switch (buttonPressed) {
                    case MOVE:
                        setGreenActionButton(false, TurnAction.BUILD);
                        setGreenActionButton(true, TurnAction.MOVE);
                        boardSceneController.deactivateAllGlowOnPanels();
                        showPossibleBlockAction(actualWorkerStatus.getSelectedWorker(),TurnAction.MOVE);
                        state = MOVE_SELECTED;
                        break;
                    case BUILD:
                        setGreenActionButton(false, TurnAction.BUILD);
                        boardSceneController.deactivateAllGlowOnPanels();
                        state = selectWorkerState(actualWorkerStatus);
                        break;
                    case PASS:
                        if (actualWorkerStatus.isWorkerLocked()) {
                            commandEvent = new VC_PlayerCommandGameEvent("", TurnAction.PASS, myUsername, null, null, null);
                            if (isCommandEventValid(commandEvent)) {
                                gui.sendEvent(commandEvent);
                                state = selectMsgSentState(actualWorkerStatus);
                                boardSceneController.disableAllActionButtons();
                            } else {
                                displayPopupError("Error in the creation of the event");
                            }
                        } else {
                            System.out.println("ERROR - " + state + " - Please deselect the worker and press again");
                            displayPopupError("Please deselect the worker and press again");
                        }
                        break;
                }
                break;
            case BUILDING_BLOCK_SELECTED:
                System.out.println("ERROR - " + state + " - Please select the building destination on the Board");
                displayPopupError("Please select the building destination on the Board");
                break;
            case MSG_SENT_UNLOCKED:
                //same as
            case MSG_SENT_LOCKED:
                System.out.println("ERROR - " + state + " - Please wait. The server is handling your request");
                displayPopupError("Please wait. The server is handling your request");
                break;
        }
    }

    @Override
    public void handleClickOnButton(BlockTypeEnum blockClicked) {
        switch (state) {
            case BUILD_SELECTED:
                blockToBuild = blockClicked;
                setGreenBlockButton(true, blockToBuild);
                state = BUILDING_BLOCK_SELECTED;
                break;
            case BUILDING_BLOCK_SELECTED:
                if (blockClicked.equals(blockToBuild)) {
                    setGreenBlockButton(true, blockToBuild);
                    blockToBuild = null;
                    state = BUILD_SELECTED;
                } else {
                    setGreenBlockButton(false, blockToBuild);
                    blockToBuild = blockClicked;
                    setGreenBlockButton(true, blockToBuild);
                }
                break;
            default:
                System.out.println("ERROR - " + state + " - Please press BUILD button before selecting a Building Block");
                displayPopupError("Please press BUILD button before selecting a Building Block");
        }
    }

    @Override
    public void updateFromServer(CV_CommandExecutedGameEvent event) {
        if (state == MSG_SENT_UNLOCKED) {
            state = MSG_SENT_LOCKED;
            boardSceneController.getWorkerStatus().setWorkerLocked(true);
        }
    }

    @Override
    public void updateFromServer(CV_CommandRequestEvent event) {
        lastCommandRequest = event;
        if (state == MSG_SENT_UNLOCKED) {
            state = CLEAN_TURN;
        } else if (state == MSG_SENT_LOCKED) {
            state = WORKER_LOCKED;
        }
    }

    /**
     * If the worker is already locked the method returns the MSG_SENT_LOCKED state, otherwise it returns the MSG_SENT_UNLOCKED state
     *
     * @param workerStatus represent the status of actual selected worker
     * @return The next Game state based on the worker status
     */
    private GameStateEnum selectMsgSentState(WorkerStatus workerStatus) {
        if (workerStatus.isWorkerLocked()) {
            return MSG_SENT_LOCKED;
        } else {
            return MSG_SENT_UNLOCKED;
        }
    }

    /**
     * If the worker is already locked the method returns the Worker LOCKED state, otherwise it returns the Worker UNLOCKED state
     *
     * @param workerStatus represent the status of actual selected worker
     * @return The next Game state based on the worker status
     */
    private GameStateEnum selectWorkerState(WorkerStatus workerStatus) {
        if (workerStatus.isWorkerLocked()) {
            return WORKER_LOCKED;
        } else {
            return WORKER_SELECTED;
        }
    }

    private void displayPopupError(String errorMessage) {
        boardSceneController.displayPopupMessage(errorMessage);
    }

    private void displayRightMessage(String message) {
        boardSceneController.addMessageToQueueList(message);
    }

    @Override
    public ViewGameEvent getEvent() {
        return eventArrived;
    }

    private void showPossibleBlockAction(Worker.IDs workerID, TurnAction actionClicked) {
        if (actionClicked == TurnAction.MOVE) {
            switch (workerID) {
                case A:
                    boardSceneController.activateGlowOnPanels(lastCommandRequest.getAvailableMovementBlocksA(),actionClicked);
                    break;
                case B:
                    boardSceneController.activateGlowOnPanels(lastCommandRequest.getAvailableMovementBlocksB(),actionClicked);
                    break;
            }
        } else if (actionClicked == TurnAction.BUILD) {
            switch (workerID) {
                case A:
                    boardSceneController.activateGlowOnPanels(lastCommandRequest.getAvailableBuildBlocksA(),actionClicked);
                    break;
                case B:
                    boardSceneController.activateGlowOnPanels(lastCommandRequest.getAvailableBuildBlocksB(),actionClicked);
                    break;
            }
        }


    }

    private String getUsernamePlayer(int x, int y) {
        IslandData actualIsland = boardSceneController.getLastIslandUpdate();
        return actualIsland.getCellCluster(x, y).getUsernamePlayer();
    }

    private boolean isMyWorker(int x, int y) {
        return myUsername.equals(getUsernamePlayer(x, y));
    }

    //enable green button
    private void setGreenActionButton(boolean enableGreen, TurnAction buttonToSet) {
        boardSceneController.setGreenActionButton(enableGreen, buttonToSet);
        System.out.println(buttonToSet + " is now green");
    }

    //enable green button
    private void setGreenBlockButton(boolean enableGreen, BlockTypeEnum blockToSet) {
        boardSceneController.setGreenBuildingBlocks(enableGreen, blockToSet);
        System.out.println(blockToSet + " is now green");
    }

    private boolean isCommandEventValid(VC_PlayerCommandGameEvent commandEvent) {
        return commandEvent.isCommandEventValid() && myUsername.equals(commandEvent.getFromPlayer());
    }
}
