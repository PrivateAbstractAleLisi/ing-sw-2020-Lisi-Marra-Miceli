package placeholders;

import model.exception.InvalidMovementException;
import view.WelcomeView;

public class Main {



    public static void main(String[] args) throws CloneNotSupportedException, InvalidMovementException {

      WelcomeView wv = new WelcomeView();
      wv.start();
    }
}
