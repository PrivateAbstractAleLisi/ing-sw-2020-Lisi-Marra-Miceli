package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.model.gamemap.Worker;

public class TurnStatus {
    Worker.IDs selectedWorker;
    boolean alreadySelectedWorker = false;

    public TurnStatus() {
        selectedWorker = null;
        alreadySelectedWorker = false;
    }

    public Worker.IDs getSelectedWorker() {
        return selectedWorker;
    }

    public void setSelectedWorker(Worker.IDs selectedWorker) {
        this.selectedWorker = selectedWorker;
    }

    public boolean isAlreadySelectedWorker() {
        return alreadySelectedWorker;
    }

    public void setAlreadySelectedWorker(boolean alreadySelectedWorker) {
        this.alreadySelectedWorker = alreadySelectedWorker;
    }
}
