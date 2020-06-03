package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import it.polimi.ingsw.psp58.view.UI.GUI.SantoriniAudioPlayer;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomSizeSceneController {
    public ImageView img2, img3;
    public ImageView background;

    private GUI gui;

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    @FXML
    public void initialize() {
        img2.setVisible(false);
        img3.setVisible(false);

        displayButtons();

    }

    public void displayButtons() {
        FadeTransition trans = new FadeTransition();
        trans.setDuration(Duration.millis(666));
        trans.setNode(img2);
        trans.setFromValue(0);
        trans.setToValue(1);

        FadeTransition trans2 = new FadeTransition();
        trans2.setDuration(Duration.millis(666));
        trans2.setNode(img3);
        trans2.setFromValue(0);
        trans2.setToValue(1);
        img2.setVisible(true);
        img3.setVisible(true);
        trans2.play();
        trans.play();
    }



    //2 player
    public void img2OnMouseClicked() {
        choice(2);
    }

    public void img2OnMouseEntered() {
        img2.setEffect(new Glow());
    }
    public void img2OnMouseExited() {
        img2.setEffect(null);
    }

    //3 player
    public void img3OnMouseClicked() {
        choice(3);
    }

    public void img3OnMouseEntered() {
        img3.setEffect(new Glow());
    }
    public void img3OnMouseExited() {
        img3.setEffect(null);
    }



    private void turnDown(int choice) {
        FadeTransition trans = new FadeTransition();
        trans.setDuration(Duration.millis(666));
        trans.setNode(img2);
        trans.setFromValue(1);
        trans.setToValue(0);

        FadeTransition trans2 = new FadeTransition();
        trans2.setDuration(Duration.millis(666));
        trans2.setNode(img3);
        trans2.setFromValue(1);
        trans2.setToValue(0);


        trans.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                gui.roomSizeResponse(choice);
            }
        });
        thunder();
        trans2.play();
        trans.play();



    }

    private void thunder() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    SantoriniAudioPlayer player = new SantoriniAudioPlayer();
                    player.loadSound("src/main/resources/sounds/welcome.wav");
                    player.play();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        System.out.println("thunder done");
    }
    private void choice(int number) {

        background.setEffect(new Bloom());
        turnDown(number);



    }


}
