package auxiliary;

public class Range {
    private int low;
    private int high;

    private final Range ISLAND_OFFSET_RANGE = new Range(0, 4);

    public Range(int low, int high) {
        this.low = low;
        this.high = high;
    }

    public boolean contains(int number) {
        return (number >= low && number <= high);

    }

    /**
     * @author alelisi
     * checks if index of island cell is inside the island or not 0..4 0..4
     * @param x x index of a cell
     * @param y y index of a cell
     * @return true if is in range, false otherwise
     */
    public boolean isIndexOfCellInRange(int x, int y) {
        if (ISLAND_OFFSET_RANGE.contains(x) && ISLAND_OFFSET_RANGE.contains(y)) {
            return true;
        }
        return false;
    }

}
