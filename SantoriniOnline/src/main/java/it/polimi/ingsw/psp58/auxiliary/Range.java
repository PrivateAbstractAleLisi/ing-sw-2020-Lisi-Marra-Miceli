package it.polimi.ingsw.psp58.auxiliary;

/**
 * auxiliary class that represents a natural numbers range between low and high (including upper and lower bounds)
 */
public class Range {

    private int low;
    private int high;

    public Range(int low, int high) {

        this.low = low;
        this.high = high;

    }

    /**
     * checks if the number is or is between the range
     * @param number the number that could be in the range
     * @return true if the number is or is between the range
     */
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

    /**
     * checks if a given cell indexes are both inside the range or not
     * @param x x index of a cell
     * @param y y index of a cell
     * @return true if the cell x and y indexes are both inside the range
     */
    public boolean isIndexOfCellInRange(int x, int y) {
        return (x >= lower && x <= upper) && (y >= lower && y <= upper);
    }

}
