package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StartingSceneController {
    private GUI gui;

    public void start() throws IOException {
        gui.changeScene(gui.getConnectionScene());
    }
    public void close(){
        System.exit(1);
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }
}
