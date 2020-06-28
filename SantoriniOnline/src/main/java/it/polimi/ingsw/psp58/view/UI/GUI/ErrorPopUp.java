package it.polimi.ingsw.psp58.view.UI.GUI;

import it.polimi.ingsw.psp58.view.UI.GUI.controller.ErrorSceneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Class that loads the ErrorScene to show the error messages to the player
 */
public class ErrorPopUp {


    FXMLLoader loader;

    /**
     * Loads the ErrorScene.fxml
     * @return the ErrorScene.fxml just loaded
     */
    private Scene loadScene() {
        loader = new FXMLLoader(
                getClass().getResource("/scenes/ErrorScene.fxml"));
        try {
            return new Scene(loader.load());
        } catch (IOException e) {
            e.printStackTrace();

        }
        return null;
    }

    /**
     * Displays the error message through a PopUp with a modal stage
     * @param message the string containing the error message to display
     * @param parentStage the stage on which the new modal stage will be displayed
     */
    public void show(String message, Stage parentStage) {

        Stage stage = new Stage();

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("ERROR");
        stage.setResizable(false);

        Scene scene = loadScene();

        stage.setScene(scene);
        stage.initOwner(parentStage);
        ((ErrorSceneController) loader.getController()).setMessageText(message);

        stage.show();
        ((ErrorSceneController) loader.getController()).displayAndArm();


    }
}
