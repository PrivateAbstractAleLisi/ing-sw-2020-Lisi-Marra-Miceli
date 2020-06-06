package it.polimi.ingsw.psp58.model.gods;

import it.polimi.ingsw.psp58.model.BehaviourManager;
import it.polimi.ingsw.psp58.model.Card;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.CellCluster;
import it.polimi.ingsw.psp58.model.gamemap.Island;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

import static it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum.DOME;

public class Zeus extends Card {

    public Zeus(Player p) {
        super(p);
        name = CardEnum.ZEUS;
    }

    /**
     * Check if the construction can be done from the actual position of the {@link Worker}.
     *
     * @param block    level of construction {@link Player} wants to build
     * @param actualX  Actual X Position of the worker
     * @param actualY  Actual Y Position of the worker
     * @param desiredX X Position where the player wants to place the worker
     * @param desiredY Y Position where the player wants to place the worker
     * @param island   The current board of game
     * @return true when the construction can be done from the actual position, false otherwise
     */
    @Override
    protected boolean isValidConstruction(BlockTypeEnum block, int actualX, int actualY, int desiredX, int desiredY, Island island) throws IndexOutOfBoundsException {
        CellCluster desiredCellCluster = island.getCellCluster(desiredX, desiredY);
        boolean samePosition = actualX == desiredX && actualY == desiredY;
        BehaviourManager behaviour = playedBy.getBehaviour();

        //verifica il behaviour permette di costruire
        if (behaviour.getBlockPlacementLeft() <= 0) {
            return false;
        }

        if (desiredCellCluster.hasWorkerOnTop() && !samePosition) {
            return false;
        }
        if (desiredCellCluster.isComplete()) {
            return false;
        }
        if(samePosition && block==DOME){
            return false;
        }
        //calcola la distanza euclidea e verifica che sia min di 2 (ritorna false altrimenti)
        if (distance(actualX, actualY, desiredX, desiredY) >= 2) {
            return false;
        }

        //genero un array contenente la struttura del cellcluster (e il nuovo blocco) e l'analizzo nella funzione successiva
        int[] desiredConstruction = desiredCellCluster.toIntArrayWithHypo(block);
        if (!isValidBlockPlacement(block, desiredConstruction, behaviour)) {
            return false;
        }
        return true;
    }
}
