package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.model.gamemap.Worker;

public class WorkerStatus {
    private Worker.IDs selectedWorker;
    private boolean workerLocked;

    public WorkerStatus() {
        selectedWorker = null;
        workerLocked = false;
    }

    public Worker.IDs getSelectedWorker() {
        return selectedWorker;
    }

    public void setSelectedWorker(Worker.IDs selectedWorker) {
        this.selectedWorker = selectedWorker;
    }

    public void deleteSelectedWorker() {
        this.selectedWorker = null;
    }

    public boolean isAlreadySelectedWorker() {
        return selectedWorker != null;
    }

    public boolean isWorkerLocked() {
        return workerLocked;
    }

    public void setWorkerLocked(boolean workerLocked) {
        this.workerLocked = workerLocked;
    }
}
