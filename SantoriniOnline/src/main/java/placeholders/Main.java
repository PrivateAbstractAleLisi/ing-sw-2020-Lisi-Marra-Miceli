package placeholders;

import model.exception.InvalidMovementException;
import model.gamemap.*;

public class Main {
    public static void main(String[] args) throws CloneNotSupportedException, InvalidMovementException {

      Island island = new Island();
        IslandPrinter ip = new IslandPrinter(island);
        ip.displayIsland();
        Worker wa = new Worker(Worker.IDs.A, "ale");
        Worker wb = new Worker(Worker.IDs.B, "ale");
        island.placeWorker(wa, 3, 4);
        island.placeWorker(wa, 4, 4);
        //island.placeWorker(wb, 3, 4);
        island.placeWorker(wb, 3, 4);
        ip.displayIsland();


    }
}
