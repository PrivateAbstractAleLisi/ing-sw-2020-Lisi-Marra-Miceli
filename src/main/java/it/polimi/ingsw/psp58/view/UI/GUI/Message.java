package it.polimi.ingsw.psp58.view.UI.GUI;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Used to diplay message
 */
public class Message {

    /**
     * Displays a PopUp message with a modal stage
     * @param x the width of the modal stage
     * @param y the height of the modal stage
     * @param message the string containing the message to display
     * @param parentStage the stage that this modal stage will overlap
     */
    public static void show(int x, int y, String message, Stage parentStage) {

        Stage stage = new Stage();

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(x);
        stage.setMinHeight(y);

        Label label = new Label(message);


        VBox vBox = new VBox(15);

        Button button = new Button("OK");

        button.setOnAction(event -> stage.close());

        vBox.getChildren().addAll(label, button);

        vBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.initOwner(parentStage);
        stage.showAndWait();
    }
}
