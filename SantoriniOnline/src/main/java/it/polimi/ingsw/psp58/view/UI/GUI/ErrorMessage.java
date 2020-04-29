package it.polimi.ingsw.psp58.view.UI.GUI;

import it.polimi.ingsw.psp58.event.gameEvents.CV_GameErrorGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ErrorMessage {

    public static void display(String message) {

        Stage stage = new Stage();

        stage.initModality(Modality.APPLICATION_MODAL);

        Label label = new Label(message);

        VBox vBox = new VBox(10);

        Button button = new Button("OK");
        button.setMaxSize(40, 30);

        button.setOnAction(e -> stage.close());

        vBox.getChildren().addAll(label, button);

        vBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.showAndWait();
    }
}
