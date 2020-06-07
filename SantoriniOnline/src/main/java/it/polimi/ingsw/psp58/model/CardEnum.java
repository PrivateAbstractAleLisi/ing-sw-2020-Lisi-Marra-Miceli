package it.polimi.ingsw.psp58.model;

public enum CardEnum {
    
    APOLLO(1, "Apollo", "Your Worker may move into an opponent Worker’s space by forcing their Worker to the space yours just vacated.", "/images/gods/01_Apollo.png"),
    ARTEMIS(2, "Artemis", "Your Worker may move one additional time, but not back to its initial space.", "/images/gods/02_Artemis.png"),
    ATHENA(3, "Athena", "If one of your Workers moved up on your last turn, opponent Workers cannot move up this turn.", "/images/gods/03_Athena.png"),
    ATLAS(4, "Atlas", "Your Worker may build a dome at any level.", "/images/gods/04_Atlas.png"),
    DEMETER(5, "Demeter", "Your Worker may build one additional time, but not on the same space.", "/images/gods/05_Demer.png"),
    HEPHAESTUS(6, "Hephaestus", "Your Worker may build one additional block (not dome) on top of your first block.", "/images/gods/06_Hephaestus.png"),
    MINOTAUR(8, "Minotaur", "Your Worker may move into an opponent Worker’s space, if their Worker can be forced one space straight backwards to an unoccupied space at any level.", "/images/gods/08_Minotaur.png"),
    PAN(9, "Pan", "You also win if your Worker moves down two or more levels.", "/images/gods/09_Pan.png"),
    PROMETHEUS(10, "Prometheus", "If your Worker does not move up, it may build both before and after moving.", "/images/gods/10_Prometheus.png"),
    CHRONUS(16, "Chronus", "You also win when there are at least five Complete Towers on the board.","/images/gods/16_Chronus.png"),
    HERA(20, "Hera", "An opponent cannot win by moving into a perimeter space.","/images/gods/20_Hera.png"),
    HESTIA(21, "Hestia", "Your Worker may build one additional time, but this cannot be on a perimeter space.","/images/gods/21_Hestia.png"),
    TRITON(29, "Triton", "Each time your Worker moves into a perimeter space, it may immediately move again.","/images/gods/29_Triton.png"),
    ZEUS(30, "Zeus", "Your Worker may build a block under itself.","/images/gods/30_Zeus.png"),
    SUPERUSER(99, "Superuser", "You can do what the F**k you want", "/images/gods/99_Superuser.png");

    private int number;
    private String name;
    private String description;
    private String imgUrl;

    CardEnum(int number, String name, String description, String imgUrl) {
        this.number = number;
        this.description = description;
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static CardEnum getValueFromInt(int id) {

        for (CardEnum ce : CardEnum.values()) {
            if (ce.getNumber() == id) {
                return ce;
            }

        }
        return null;
    }

    public String getImgUrl() {
        return imgUrl;
    }

}
