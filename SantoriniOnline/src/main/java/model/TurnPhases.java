package model;

public enum TurnPhases {

        PRE_BUILD,
        MOVE,
        BUILD,
        MOVE_SECOND_TIME,
        CHECK_IF_MOVED_ONTO_DIFF_CELL,
        END;

    private static TurnPhases[] vals = values();

    /*
    get the next value of the enum (aka the next phase)
    */
    public TurnPhases next()
    {
        return vals[(this.ordinal()+1) % vals.length];
    }
}
