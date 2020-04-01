package auxiliary;

public class Range {
    private int low;
    private int high;



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

    private final int lower = 0;
    private final int upper = 4;
    public boolean isIndexOfCellInRange(int x, int y) {
        return (x >= lower && x <= upper) && (y >= lower && y <= upper);
    }

}
