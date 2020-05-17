package it.polimi.ingsw.psp58.view.UI.GUI;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BoardPopUp {

    public static void show(String message, Stage parentStage) {

        Stage stage = new Stage();

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(250);
        stage.setMinHeight(200);

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
