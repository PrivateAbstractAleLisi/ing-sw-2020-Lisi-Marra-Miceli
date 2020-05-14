package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.event.gameEvents.lobby.CV_RoomUpdateGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.CV_CardChoiceRequestGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.CV_ChallengerChosenEvent;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class PreGameSceneController {

    private GUI gui;

    //CONSTANT
    public final double MAX_CARDS_NUMBERS = 15;

    //CENTER
    public Text chooseXCardText;

    //Challenger
    public ScrollPane challengerScrollPane; //Invisible
    public FlowPane challengerCardsFlowPane;

    //CardChoice
    public TilePane cardChoiceTilePane;//Invisible
    public HBox cardChoiceHBox;
    public VBox cardChoice_VBox1;
    public VBox cardChoice_VBox2;
    public VBox cardChoice_VBox3;

    //LEFT
    public HBox player1;
    public HBox player2;
    public HBox player3;

    //RIGHT
    public Button confirmButton;

    public void setGui(GUI gui) {
        this.gui = gui;
    }


    /**
     * Add the player to the left list of Players
     *
     * @param event used to fill the list
     */
    public void update(CV_RoomUpdateGameEvent event) {
        int actualPlayersInRoom = event.getUsersInRoom().length;
        String[] usersInRoom = event.getUsersInRoom();
        Text text;
        switch (actualPlayersInRoom) {
            case 1:
                text = (Text) player1.getChildren().get(0);
                text.setText("1) " + usersInRoom[0].toUpperCase());

                player1.setVisible(true);
                player2.setVisible(false);
                player3.setVisible(false);
                break;
            case 2:
                text = (Text) player1.getChildren().get(0);
                text.setText("1) " + usersInRoom[0].toUpperCase());

                text = (Text) player2.getChildren().get(0);
                text.setText("2) " + usersInRoom[1].toUpperCase());

                player1.setVisible(true);
                player2.setVisible(true);
                player3.setVisible(false);
                break;
            case 3:
                text = (Text) player1.getChildren().get(0);
                text.setText("1) " + usersInRoom[0].toUpperCase());

                text = (Text) player2.getChildren().get(0);
                text.setText("2) " + usersInRoom[1].toUpperCase());

                text = (Text) player3.getChildren().get(0);
                text.setText("3) " + usersInRoom[2].toUpperCase());

                player1.setVisible(true);
                player2.setVisible(true);
                player3.setVisible(true);
                break;
            default:
                System.out.println("ERROR");
        }
    }

    public void update(CV_CardChoiceRequestGameEvent event) {
        setTitleCenter("CHOOSE 1 CARD");
        setVisibleOnlyThisNode(cardChoiceTilePane);
    }

    public void setVisibleOnlyThisNode(Node node) {
        cardChoiceTilePane.setVisible(false);
        challengerScrollPane.setVisible(false);

        if (node.equals(cardChoiceTilePane)) {
            cardChoiceTilePane.setVisible(true);
        } else if (node.equals(challengerScrollPane)) {
            challengerScrollPane.setVisible(true);
        }
    }

    private void setTextToBold(Text text, double fontSize) {
        text.setFont(new Font("System Bold", fontSize));
    }

    private boolean isMyUsername(String usernameToTest) {
        return usernameToTest.toLowerCase().equals(gui.getMyUsername().toLowerCase());
    }

    private void setTitleCenter(String title) {
        chooseXCardText.setText(title.toUpperCase());
    }

    /**
     * CHALLENGER
     * Receive the event CV_ChallengerChosenEvent and handle the choice of the cards
     *
     * @param event Contains some data like the size of the room.
     */
    public void update(CV_ChallengerChosenEvent event) {

        System.out.println("You're the chellenger, chose " + event.getRoomSize() + " cards");
        setTitleCenter("YOU ARE THE CHALLENGER!! \tCHOOSE " + event.getRoomSize() + " CARDS");

        fillAllCards();
        showAllCards();
    }

    /**
     * CHALLENGER
     * Set Visible all the Cards
     */
    public void showAllCards() {
        setVisibleOnlyThisNode(challengerScrollPane);
    }

    /**
     * CHALLENGER
     * Fill all the card to show them to the challenger
     */
    private void fillAllCards() {
        ObservableList<Node> children = challengerCardsFlowPane.getChildren();

        int actualNode = 0;
        for (CardEnum card : CardEnum.values()) {
            if (actualNode < MAX_CARDS_NUMBERS) {
                if (children.get(actualNode) instanceof HBox) {
                    fillSingleHBox((HBox) children.get(actualNode), card);
                } else {
                    hideNextCardsHBoxes(actualNode);
                }
                actualNode++;
            } else {
                System.out.println("ERROR TOO MUCH CARDS");
                break;
            }
        }
        if (actualNode < MAX_CARDS_NUMBERS) {
            hideNextCardsHBoxes(actualNode);
            deleteNextCardsHBoxes(actualNode);
        }
    }

    /**
     * CHALLENGER
     * Hide remaining card from the actualHBox param
     *
     * @param actualHBox where the method began to hide cards
     */
    private void hideNextCardsHBoxes(int actualHBox) {
        ObservableList<Node> children = challengerCardsFlowPane.getChildren();
        while (actualHBox < MAX_CARDS_NUMBERS) {
            children.get(actualHBox).setVisible(false);
            actualHBox++;
        }
    }

    /**
     * CHALLENGER
     * Delete remaining card from the actualHBox param
     *
     * @param actualHBox where the method began to DELETE cards
     */
    private void deleteNextCardsHBoxes(int actualHBox) {
        ObservableList<Node> children = challengerCardsFlowPane.getChildren();
        while (actualHBox < children.size()) {
            children.remove(actualHBox);
        }
    }

    /**
     * CHALLENGER
     * Fill one (1) HBox using data collected from CardEnum
     *
     * @param hBox the HBox to fill
     * @param card the Card where to find data
     */
    private void fillSingleHBox(HBox hBox, CardEnum card) {
        ImageView imageView = (ImageView) hBox.getChildren().get(0);
        VBox vBox = (VBox) hBox.getChildren().get(1);
        Text cardName = (Text) vBox.getChildren().get(0);
        Text cardDescription = (Text) vBox.getChildren().get(1);

        imageView.setImage(new Image(card.getImgUrl()));
        cardName.setText(card.getName());
        cardDescription.setText(card.getDescription());
    }

    private void enableConfirmButton() {
        confirmButton.setDisable(false);
    }

    private void disableConfirmButton() {
        confirmButton.setDisable(true);
    }
}
