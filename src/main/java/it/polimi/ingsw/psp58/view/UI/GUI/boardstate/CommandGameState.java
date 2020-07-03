package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import it.polimi.ingsw.psp58.auxiliary.IslandData;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandExecutedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandRequestEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.VC_PlayerCommandGameEvent;
import it.polimi.ingsw.psp58.model.TurnAction;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.BoardSceneController;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.WorkerStatus;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.exceptions.WorkerLockedException;

import static it.polimi.ingsw.psp58.view.UI.GUI.boardstate.TurnStateEnum.*;

/**
 * Handle all the user interactions like OnBoard Clicks or Buttons.
 * Implements a second Layer of State Pattern with {@link TurnStateEnum} enum.
 */
public class CommandGameState implements GameStateAbstract {
    /**
     * Last {@link CV_CommandRequestEvent} event is memorized.
     */
    private CV_CommandRequestEvent lastCommandRequest;
    /**
     * The {@link BoardSceneController} that created this Object.
     */
    private final BoardSceneController boardSceneController;
    /**
     * Main {@link GUI} class.
     */
    private final GUI gui;
    /**
     * My username saved here for a quick access.
     */
    private final String myUsername;

    /**
     * Actual state of the turn.
     */
    private TurnStateEnum state;

    /**
     * {@link BlockTypeEnum} selected by the user.
     */
    private BlockTypeEnum blockToBuild = null;

    /**
     * Unique constructor of the Class.
     *
     * @param gui             Main {@link GUI} class.
     * @param boardController The {@link BoardSceneController} that created this Object.
     */
    public CommandGameState(GUI gui, BoardSceneController boardController) {
        this.gui = gui;
        boardSceneController = boardController;
        myUsername = gui.getUsername();
        state = CLEAN_TURN;
    }

    /**
     * Handle the click on the Board and decide what to do based on the Actual {@code TurnStateEnum state}.
     *
     * @param x X Coordinate of the click.
     * @param y Y Coordinate of the click.
     */
    @Override
    public void handleClick(int x, int y) {
        //gets the id of the clicked worker, if not ok contain a null
        Worker.IDs workerIdInCellCluster = boardSceneController.getWorkerID(x, y);
        WorkerStatus actualWorkerStatus = boardSceneController.getWorkerStatus();
        VC_PlayerCommandGameEvent commandEvent;
        int[] position;
        switch (state) {
            case CLEAN_TURN:
                cleanTurnBoardClick(x, y, workerIdInCellCluster, actualWorkerStatus);
                break;
            case WORKER_SELECTED:
                workerSelectedBoardClick(x, y, workerIdInCellCluster, actualWorkerStatus);
                break;
            case WORKER_LOCKED:
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
                displayPopupError("Please wait. The server is handling your request");
                break;
        }
    }

    /**
     * Handle the Click on the board when the {@code state} is {@code CLEAN_TURN}.
     *
     * @param x                     X Coordinate of the click.
     * @param y                     Y Coordinate of the click.
     * @param workerIdInCellCluster {@code WorkerID} in the selected Board Cell.
     * @param actualWorkerStatus    {@link WorkerStatus} at the moment.
     */
    private void cleanTurnBoardClick(int x, int y, Worker.IDs workerIdInCellCluster, WorkerStatus actualWorkerStatus) {
        if (workerIdInCellCluster != null && !actualWorkerStatus.isAlreadySelectedWorker()) {
            String playerOfWorker = boardSceneController.getLastIslandUpdate().getCellCluster(x, y).getUsernamePlayer();
            boolean isMyWorker = playerOfWorker.equals(myUsername);
            if (isMyWorker) {
                try {
                    //Select the worker
                    actualWorkerStatus.setSelectedWorker(workerIdInCellCluster);
                    boardSceneController.setWorkerGlow(true, x, y);
                    state = WORKER_SELECTED;
                } catch (WorkerLockedException e) {
                    state = WORKER_LOCKED;
                }
            } else {
                displayPopupError("Please select one of your workers");
            }
        } else {
            displayPopupError("Please select one of your workers");
        }

    }

    /**
     * Handle the Click on the board when the {@code state} is {@code WORKER_SELECTED}.
     *
     * @param x                     X Coordinate of the click.
     * @param y                     Y Coordinate of the click.
     * @param workerIdInCellCluster {@code WorkerID} in the selected Board Cell.
     * @param actualWorkerStatus    {@link WorkerStatus} at the moment.
     */
    private void workerSelectedBoardClick(int x, int y, Worker.IDs workerIdInCellCluster, WorkerStatus actualWorkerStatus) {
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
                    state = WORKER_LOCKED;
                }
            }
        } else {
            displayPopupError("Please select an available action or your workers");
        }

    }

    /**
     * Handle the click on the Action Buttons and decide what to do based on the Actual {@code TurnStateEnum state}.
     *
     * @param buttonPressed {@link TurnAction} of the Button pressed.
     */
    @Override
    public void handleClick(TurnAction buttonPressed) {
        WorkerStatus actualWorkerStatus = boardSceneController.getWorkerStatus();
        switch (state) {
            case CLEAN_TURN:
                cleanTurnButtonClick(buttonPressed, actualWorkerStatus);
                break;
            case WORKER_SELECTED:
                workerSelectedButtonClick(buttonPressed, actualWorkerStatus);
                break;
            case WORKER_LOCKED:
                workerLockedButtonClick(buttonPressed, actualWorkerStatus);
                break;
            case MOVE_SELECTED:
                moveSelectedButtonClick(buttonPressed, actualWorkerStatus);
                break;
            case BUILD_SELECTED:
                buildSelectedButtonClick(buttonPressed, actualWorkerStatus);
                break;
            case BUILDING_BLOCK_SELECTED:
                displayPopupError("Please select the building destination on the Board");
                break;
            case MSG_SENT_UNLOCKED:
                //same as
            case MSG_SENT_LOCKED:
                displayPopupError("Please wait. The server is handling your request");
                break;
        }
    }

    /**
     * Handle the click on the Action Buttons when the {@code state} is {@code CLEAN_TURN}.
     *
     * @param buttonPressed      {@link TurnAction} of the Button pressed.
     * @param actualWorkerStatus {@link WorkerStatus} at the moment.
     */
    private void cleanTurnButtonClick(TurnAction buttonPressed, WorkerStatus actualWorkerStatus) {
        VC_PlayerCommandGameEvent commandEvent;
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
            displayPopupError("Please select one of your workers");
        }
    }

    /**
     * Handle the click on the Action Buttons when the {@code state} is {@code WORKER_SELECTED}.
     *
     * @param buttonPressed      {@link TurnAction} of the Button pressed.
     * @param actualWorkerStatus {@link WorkerStatus} at the moment.
     */
    private void workerSelectedButtonClick(TurnAction buttonPressed, WorkerStatus actualWorkerStatus) {
        switch (buttonPressed) {
            case MOVE:
                setGreenActionButton(true, TurnAction.MOVE);
                state = MOVE_SELECTED;
                showPossibleBlockAction(actualWorkerStatus.getSelectedWorker(), TurnAction.MOVE);
                break;
            case BUILD:
                setGreenActionButton(true, TurnAction.BUILD);
                showPossibleBlockAction(actualWorkerStatus.getSelectedWorker(), TurnAction.BUILD);
                state = BUILD_SELECTED;
                break;
            case PASS:
                displayPopupError("Please deselect the worker and press again");
                break;
        }
    }

    /**
     * Handle the click on the Action Buttons when the {@code state} is {@code WORKER_LOCKED}.
     *
     * @param buttonPressed      {@link TurnAction} of the Button pressed.
     * @param actualWorkerStatus {@link WorkerStatus} at the moment.
     */
    private void workerLockedButtonClick(TurnAction buttonPressed, WorkerStatus actualWorkerStatus) {
        VC_PlayerCommandGameEvent commandEvent;
        switch (buttonPressed) {
            case MOVE:
                setGreenActionButton(true, TurnAction.MOVE);
                showPossibleBlockAction(actualWorkerStatus.getSelectedWorker(), TurnAction.MOVE);
                state = MOVE_SELECTED;
                break;
            case BUILD:
                setGreenActionButton(true, TurnAction.BUILD);
                showPossibleBlockAction(actualWorkerStatus.getSelectedWorker(), TurnAction.BUILD);
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
    }

    /**
     * Handle the click on the Action Buttons when the {@code state} is {@code MOVE_SELECTED}.
     *
     * @param buttonPressed      {@link TurnAction} of the Button pressed.
     * @param actualWorkerStatus {@link WorkerStatus} at the moment.
     */
    private void moveSelectedButtonClick(TurnAction buttonPressed, WorkerStatus actualWorkerStatus) {
        VC_PlayerCommandGameEvent commandEvent;
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
                showPossibleBlockAction(actualWorkerStatus.getSelectedWorker(), TurnAction.BUILD);
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
                    displayPopupError("Please deselect the worker and press again");
                }
                break;
        }
    }

    /**
     * Handle the click on the Action Buttons when the {@code state} is {@code BUILD_SELECTED}.
     *
     * @param buttonPressed      {@link TurnAction} of the Button pressed.
     * @param actualWorkerStatus {@link WorkerStatus} at the moment.
     */
    private void buildSelectedButtonClick(TurnAction buttonPressed, WorkerStatus actualWorkerStatus) {
        VC_PlayerCommandGameEvent commandEvent;
        switch (buttonPressed) {
            case MOVE:
                setGreenActionButton(false, TurnAction.BUILD);
                setGreenActionButton(true, TurnAction.MOVE);
                boardSceneController.deactivateAllGlowOnPanels();
                showPossibleBlockAction(actualWorkerStatus.getSelectedWorker(), TurnAction.MOVE);
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
                    displayPopupError("Please deselect the worker and press again");
                }
                break;
        }
    }

    /**
     * Handle the click on the Building Blocks selected and decide what to do based on the Actual {@code TurnStateEnum state}.
     *
     * @param blockClicked {@link BlockTypeEnum} of the Button pressed.
     */
    @Override
    public void handleClick(BlockTypeEnum blockClicked) {
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
                displayPopupError("Please press BUILD button before selecting a Building Block");
        }
    }

    /**
     * If not already Locked, the method lock the Worker and select a LOCKED STATE
     *
     * @param event The {@link CV_CommandExecutedGameEvent} received from the server.
     */
    @Override
    public void updateFromServer(CV_CommandExecutedGameEvent event) {
        if (state == MSG_SENT_UNLOCKED) {
            state = MSG_SENT_LOCKED;
            boardSceneController.getWorkerStatus().setWorkerLocked(true);
        }
    }

    /**
     * Handle the Request of a New Command, selecting the right STATE
     *
     * @param event The {@link CV_CommandRequestEvent} received from the server.
     */
    @Override
    public void updateFromServer(CV_CommandRequestEvent event) {
        lastCommandRequest = event;
        blockToBuild = null;
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
    private TurnStateEnum selectMsgSentState(WorkerStatus workerStatus) {
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
    private TurnStateEnum selectWorkerState(WorkerStatus workerStatus) {
        if (workerStatus.isWorkerLocked()) {
            return WORKER_LOCKED;
        } else {
            return WORKER_SELECTED;
        }
    }

    /**
     * Display an Error Popup using the BoardSceneController method.
     *
     * @param errorMessage {@link String} to print on the message.
     */
    private void displayPopupError(String errorMessage) {
        boardSceneController.displayPopupMessage(errorMessage);
    }

    /**
     * Display an a message on the right pane using the BoardSceneController method.
     *
     * @param message {@link String} to print on the message.
     */
    private void displayRightMessage(String message) {
        boardSceneController.addMessageToQueueList(message);
    }

    /**
     * Enable the Glow on the Avaailable Action
     *
     * @param workerID      The {@code WorkerID} of the selected worker.
     * @param actionClicked {@link TurnAction} clicked to enable the glow effect.
     */
    private void showPossibleBlockAction(Worker.IDs workerID, TurnAction actionClicked) {
        if (actionClicked == TurnAction.MOVE) {
            if (workerID == Worker.IDs.A) {
                boardSceneController.activateGlowOnPanels(lastCommandRequest.getAvailableMovementBlocksA(), actionClicked);
            } else if (workerID == Worker.IDs.B) {
                boardSceneController.activateGlowOnPanels(lastCommandRequest.getAvailableMovementBlocksB(), actionClicked);
            }
        } else if (actionClicked == TurnAction.BUILD) {
            if (workerID == Worker.IDs.A) {
                boardSceneController.activateGlowOnPanels(lastCommandRequest.getAvailableBuildBlocksA(), actionClicked);
            } else if (workerID == Worker.IDs.B) {
                boardSceneController.activateGlowOnPanels(lastCommandRequest.getAvailableBuildBlocksB(), actionClicked);
            }
        }


    }

    /**
     * Get the name of the Player that has a worker in the given coordinates.
     *
     * @param x X Coordinate.
     * @param y Y Coordinate.
     * @return {@link String} name of the Player that has a worker in the given coordinates.
     */
    private String getUsernamePlayer(int x, int y) {
        IslandData actualIsland = boardSceneController.getLastIslandUpdate();
        return actualIsland.getCellCluster(x, y).getUsernamePlayer();
    }

    /**
     * Check if a Worker is mine or not.
     *
     * @param x X Coordinate.
     * @param y Y Coordinate.
     * @return {@code true} if the worker is mine, {@code false} otherwise.
     */
    private boolean isMyWorker(int x, int y) {
        return myUsername.equals(getUsernamePlayer(x, y));
    }

    /**
     * Set the greenGlow around the Action Button.
     *
     * @param enableGreen {@code boolean} value, {@code true} to enable, {@code false} to disable the green.
     * @param buttonToSet {@link TurnAction} clicked to enable the green effect.
     */
    private void setGreenActionButton(boolean enableGreen, TurnAction buttonToSet) {
        boardSceneController.setGreenActionButton(enableGreen, buttonToSet);
    }

    /**
     * Set the greenGlow around the Building Blocks Button.
     *
     * @param enableGreen {@code boolean} value, {@code true} to enable, {@code false} to disable the green.
     * @param blockToSet  {@link BlockTypeEnum} clicked to enable the green effect.
     */
    private void setGreenBlockButton(boolean enableGreen, BlockTypeEnum blockToSet) {
        boardSceneController.setGreenBuildingBlocks(enableGreen, blockToSet);
    }

    /**
     * Check if the command is valid with all the info or not.
     *
     * @param commandEvent The {@link VC_PlayerCommandGameEvent} to test.
     * @return {@code true} if the command is valid, {@code false} otherwise.
     */
    private boolean isCommandEventValid(VC_PlayerCommandGameEvent commandEvent) {
        return commandEvent.isCommandEventValid() && myUsername.equals(commandEvent.getFromPlayer());
    }
}
