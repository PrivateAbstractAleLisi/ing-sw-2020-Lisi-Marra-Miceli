package model;

public enum CardEnum {
    APOLLO(1, "Your Worker may move into an opponent Worker’s space by forcing their Worker to the space yours just vacated."),
    ARTEMIS(2, "Your Worker may move one additional time, but not back to its initial space."),
    ATHENA(3, "If one of your Workers moved up on your last turn, opponent Workers cannot move up this turn."),
    ATLAS(4, "Your Worker may build a dome at any level."),
    DEMETER(5, "Your Worker may build one additional time, but not on the same space."),
    HEPHAESTUS(6, "Your Worker may build one additional block (not dome) on top of your first block."),
    MINOTAUR(8, "Your Worker may move into an opponent Worker’s space, if their Worker can be forced one space straight backwards to an unoccupied space at any level."),
    PAN(9, "You also win if your Worker moves down two or more levels."),
    PROMETHEUS(10, "If your Worker does not move up, it may build both before and after moving.");

    private int number;
    private String description;

    CardEnum(int number, String description) {
        this.number = number;
        this.description = description;
    }

    public int getNumber() {
        return number;
    }

    public String getDescription() {
        return description;
    }
}
