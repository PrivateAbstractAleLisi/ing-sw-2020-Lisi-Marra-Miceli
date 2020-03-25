package gamemap;

import exception.*;

import java.util.ArrayList;
import java.util.List;

public class CellCluster {
    private List<BlockTypeEnum> costruction; //generics su list?
    private boolean isComplete, isFree;

    public CellCluster() {
        costruction = new ArrayList<BlockTypeEnum>();
        isComplete = false;
        isFree = true;
        isComplete = false;
    }


    public void build (BlockTypeEnum block) throws InvalidBuildException {
        isFree = false;
        //Costruisci:
        if (!isComplete) {

            costruction.add(block);

            if (costruction.contains(BlockTypeEnum.DOME)) {
                isComplete = true;
            }
        }
        else {
            throw new InvalidBuildException("Beware, you cannot build here, this cell is full.");
        }
    }

    //GETTERS

    /**
     *
     * @return l'altezza attuale della costruzione, 0 se la cella è libera
     */
    public int getCostructionHeight() {
        if (isFree && costruction.isEmpty()) {
            return 0;
        }
        else {
            return costruction.size();
        }

    }

    /**
     *
     * @return true se la casella ha una costruzione completa con la cupola come ultimo elemento
     */
    public boolean isComplete() {
        return isComplete;
    }

    /**
     *
     * @return true se la casella è libera (nessun giocatore, nessuna costruzione)
     */
    public boolean isFree() {
        return isFree;
    }
}
