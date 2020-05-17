package it.polimi.ingsw.psp58.view.UI.GUI;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.concurrent.atomic.AtomicInteger;

public class Message {

    public static void show(String message, Stage parentStage) {

        Stage stage = new Stage();

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(250);
        stage.setMinHeight(100);

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



    public static int askRoomSize(String message, Stage parentStage){
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(250);
        stage.setMinHeight(100);
        stage.initStyle(StageStyle.UNDECORATED);

        AtomicInteger number = new AtomicInteger();

        Label label = new Label(message);

        HBox vBox = new HBox(15);

        Button button1 = new Button("2");
        Button button2 = new Button("3");


        button1.setOnAction(event -> {
            number.set(2);
            stage.close();
        });

        button2.setOnAction(event -> {
            number.set(3);
            stage.close();
        });

        vBox.getChildren().addAll(label,button1, button2);

        vBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.initOwner(parentStage);
        stage.showAndWait();

        return number.get();
    }
}
