package placeholders;

import model.exception.InvalidMovementException;
import view.CLIView;

public class Main {
    public static void main(String[] args) throws CloneNotSupportedException, InvalidMovementException {

      CLIView view = new CLIView();
      view.start();
    }
}
