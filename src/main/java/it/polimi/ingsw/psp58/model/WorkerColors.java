package it.polimi.ingsw.psp58.model;

/**
 * Colors assigned to the players
 */
public enum WorkerColors {
        ORANGE(0),
        BLUE(1),
        PINK(2);

        private final int id;

        WorkerColors(int id) {
                this.id=id;
        }
}
