package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.event.gameEvents.prematch.CV_ChallengerChosenEvent;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class PreGameSceneController {
    private GUI gui;

    //CENTER
    public Text chooseXCardText;
    public GridPane staticGridPane;

    //LEFT
    public HBox player1;

    //RIGHT
    public Button confirmButton;


    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public void update(CV_ChallengerChosenEvent event) {

        System.out.println("You're the chellenger, chose " + event.getRoomSize() + " cards");
        setTitleCenter("CHOOSE " + event.getRoomSize() + " CARDS");

//        ObservableList<Node> children = staticGripPane.getChildren();
//        for(Node node : children){
//            if(node instanceof HBox){
//                HBox hBox = (HBox) node;
//                ImageView image = (ImageView) hBox.getChildren().get(0);
//                image.setImage(new Image(CardEnum.ARTEMIS.getImgUrl()));
//            }
//        }

        ObservableList<Node> children = staticGridPane.getChildren();
        children.set(0,createCardHBOX(CardEnum.PAN));

    }

    private void setTitleCenter(String title) {
        chooseXCardText.setText(title.toUpperCase());
    }

    private void updateCards() {

    }

    private HBox createCardHBOX(CardEnum card) {
        HBox hBox = new HBox();
        VBox vBox = new VBox();

        hBox.setId(card.getName());
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(10);
        hBox.getStyleClass().add("card-hbox");

        ImageView imageView = new ImageView(new Image(card.getImgUrl()));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(170);
        imageView.setCursor(Cursor.HAND);
        hBox.getChildren().add(imageView);

        Text cardName = new Text(card.getName());
        Text cardDescription = new Text(card.getDescription());
        vBox.setPrefHeight(200);
        vBox.setPrefWidth(100);
        vBox.setSpacing(10);
        vBox.getChildren().add(cardName);
        vBox.getChildren().add(cardDescription);


        hBox.getChildren().add(vBox);
        return hBox;
    }

    private void enableConfirmButton() {
        confirmButton.setDisable(false);
    }

    private void disableConfirmButton() {
        confirmButton.setDisable(true);
    }
}
