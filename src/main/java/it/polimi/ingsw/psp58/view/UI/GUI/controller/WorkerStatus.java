package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.model.gamemap.Worker;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.exceptions.WorkerLockedException;

/**
 * Class used to handle the status of the worker (selection, movement and build) during the game phase.
 */
public class WorkerStatus {
    private Worker.IDs selectedWorker;
    private boolean workerLocked;

    /**
     * Constructor, the status is initialized to the starting one ({@code selectedWorker} equals to null, {@code workerLocked} equals to false).
     */
    public WorkerStatus() {
        selectedWorker = null;
        workerLocked = false;
    }

    /**
     *
     * @return the {@link Worker.IDs} of the worker that is select, null if there is no one
     */
    public Worker.IDs getSelectedWorker() {
        return selectedWorker;
    }

    /**
     * Set the status of the worker passed as parameter to selected (it can now make a move)
     * @param selectedWorker the worker just clicked by the player that will now be in the selected status
     * @throws WorkerLockedException if the worker cannot move or build
     */
    public void setSelectedWorker(Worker.IDs selectedWorker) throws WorkerLockedException {
        if (isWorkerLocked()) {
            throw new WorkerLockedException();
        } else {
            this.selectedWorker = selectedWorker;
        }
    }

    /**
     * Deselect the worker returning his state to the starting one
     * @return true if the worker is not locked, false otherwise
     */
    public boolean resetSelectedWorker() {
        if (!isWorkerLocked()) {
            deleteSelectedWorker();
            return true;
        }
        return false;
    }

    /**
     * Deselects the worker making the selectedWorker equals to null
     */
    private void deleteSelectedWorker() {
        this.selectedWorker = null;
    }

    /**
     *
     * @return true if there is a worker already selected, false otherwise
     */
    public boolean isAlreadySelectedWorker() {
        return selectedWorker != null;
    }

    /**
     *
     * @return true if the worker is locked, false otherwise
     */
    public boolean isWorkerLocked() {
        return workerLocked;
    }

    /**
     * Sets the locked status of the worker
     * @param workerLocked the locked status of the worker
     */
    public void setWorkerLocked(boolean workerLocked) {
        this.workerLocked = workerLocked;
    }
}
