package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.event.gameEvents.prematch.CV_ChallengerChosenEvent;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class PreGameSceneController {
    private GUI gui;

    //CENTER
    public Text chooseXCardText;
    public GridPane staticGripPane;

    //LEFT
    public HBox player1;

    //RIGHT
    public Button  confirmButton;


    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public void update(CV_ChallengerChosenEvent event) {

        System.out.println("You're the chellenger, chose " + event.getRoomSize() + " cards");
        setTitleCenter("CHOOSE " + event.getRoomSize() + " CARDS");

//        Text player1ID = (Text) player1.getChildren().get(0);
//        player1ID.setText("FIGA HO MODIFICATO IL PLAYER1");

        ObservableList<Node> children = staticGripPane.getChildren();
        for(Node node : children){
            if(node instanceof HBox){
                HBox hBox = (HBox) node;
                ImageView image = (ImageView) hBox.getChildren().get(0);
                image.setImage(new Image("/images/gods/full_0000s_0001_god_and_hero_cards_0059_Castor_and_Pollux.png"));
            }
        }
    }

    private void setTitleCenter(String title) {
        chooseXCardText.setText(title.toUpperCase());
    }

    private void updateCards(){

    }

    private void enableConfirmButton(){
        confirmButton.setDisable(false);
    }

    private void disableConfirmButton(){
        confirmButton.setDisable(true);
    }
}
