package it.polimi.ingsw.psp58.view.UI.GUI;

import it.polimi.ingsw.psp58.view.UI.GUI.controller.ErrorSceneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


public class ErrorPopUp {


    FXMLLoader loader;

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

    public void show(String message, Stage parentStage) {

        Stage stage = new Stage();

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("ERROR");
        stage.setResizable(false);

        // button.setOnAction(event -> stage.close());

        Scene scene = loadScene();

        stage.setScene(scene);
        stage.initOwner(parentStage);
        ((ErrorSceneController) loader.getController()).setMessageText(message);

        stage.show();
        ((ErrorSceneController) loader.getController()).displayAndArm();


    }
}
