package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.event.gameEvents.PingEvent;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.CV_RoomUpdateGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.CV_CardChoiceRequestGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.CV_ChallengerChosenEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.CV_WaitPreMatchGameEvent;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.animation.RotateTransition;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

public class PreGameSceneController {
    private GUI gui;

    //CONSTANT
    public final double MAX_CARDS_NUMBERS = 15;

    //CENTER
    public Text chooseXCardText;
    public HBox extWaitHBox;//InvisibleByDefault
    public HBox waitHBox;

    //Challenger
    public ScrollPane challengerScrollPane; //InvisibleByDefault
    public FlowPane challengerCardsFlowPane;
    //challengerCardsButton
    public HBox challengerCard01;
    public HBox challengerCard02;
    public HBox challengerCard03;
    public HBox challengerCard04;
    public HBox challengerCard05;
    public HBox challengerCard06;
    public HBox challengerCard07;
    public HBox challengerCard08;
    public HBox challengerCard09;
    public HBox challengerCard10;
    public HBox challengerCard11;
    public HBox challengerCard12;
    public HBox challengerCard13;
    public HBox challengerCard14;
    public HBox challengerCard15;

    //CardChoice
    public TilePane cardChoiceTilePane;//InvisibleByDefault
    public HBox cardChoiceHBox;
    public VBox cardChoice_VBox1;//InvisibleByDefault
    public VBox cardChoice_VBox2;//InvisibleByDefault
    public VBox cardChoice_VBox3;//InvisibleByDefault
    //CardsButton
    public StackPane choiceCard1;
    public StackPane choiceCard2;
    public StackPane choiceCard3;

    //LEFT
    public HBox player1;
    public HBox player2;
    public HBox player3;

    //RIGHT
    public Button confirmButton;
    public Pane chosenCard1;
    public Pane chosenCard2;
    public Pane chosenCard3;

    //BOTTOM
    public Button superUserButton;

    //Map
    private Map<HBox, CardEnum> challengerCardMapByHBox;
    private Map<CardEnum, HBox> challengerHBoxMapByCard;


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

    public void update(CV_WaitPreMatchGameEvent event) {
        Text waitText = (Text) waitHBox.getChildren().get(0);
        setTitleCenter("Wait your turn");

        switch (event.getWaitCode()) {
            case "CHALLENGER_CARDS":
                waitText.setText(event.getChallenger().toUpperCase() + " is the challenger and he's choosing the cards.\nPlease wait");
        }

        Thread rotation = new Thread(() -> {
            ImageView hourglass = (ImageView) waitHBox.getChildren().get(1);
            try {
                while (waitHBox.isVisible()) {
                    hourglass.setRotate(hourglass.getRotate() + 2);
                    Thread.sleep(11);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                Thread.currentThread().interrupt();
                hourglass.setRotate(0);
            }
        });
        rotation.start();
        setVisibleOnlyThisNode(waitHBox);
    }

    public void update(CV_CardChoiceRequestGameEvent event) {
        LinkedList<VBox> vBoxes = new LinkedList<>();
        vBoxes.add(cardChoice_VBox1);
        vBoxes.add(cardChoice_VBox2);
        vBoxes.add(cardChoice_VBox3);

        setTitleCenter("CHOOSE YOUR CARD!!");

        ArrayList<CardEnum> allCards = new ArrayList<>(3);
        allCards.addAll(event.getAvailableCards());
        if (event.getUsedCards() != null) {
            allCards.addAll(event.getUsedCards());
        }
        allCards.sort(Comparator.comparing(CardEnum::getName));

        if (allCards.size() < 3) {
            cardChoiceHBox.getChildren().remove(2);
            vBoxes.removeLast();
        }

        for (CardEnum card : allCards) {
            fillChoiceCard(vBoxes.remove(), card, event.getAvailableCards().contains(card));
        }

        setVisibleOnlyThisNode(cardChoiceTilePane);
    }

    private void fillChoiceCard(VBox vBox, CardEnum card, boolean available) {
        if (!available) {
            vBox.setDisable(true);
            vBox.getStyleClass().clear();
            StackPane parent = (StackPane) vBox.getParent();
            parent.getChildren().get(1).setVisible(true);
        }

        ImageView image = (ImageView) vBox.getChildren().get(0);
        image.setImage(new Image(card.getImgUrl()));

        VBox internalVBox = (VBox) vBox.getChildren().get(1);
        Text cardName = (Text) internalVBox.getChildren().get(0);
        Text cardDescription = (Text) internalVBox.getChildren().get(1);

        cardName.setText(card.getName());
        cardDescription.setText(card.getDescription());

        vBox.setVisible(true);
    }

    public void setVisibleOnlyThisNode(Node node) {
        cardChoiceTilePane.setVisible(false);
        challengerScrollPane.setVisible(false);
        extWaitHBox.setVisible(false);

        if (node.equals(cardChoiceTilePane)) {
            cardChoiceTilePane.setVisible(true);
        } else if (node.equals(challengerScrollPane)) {
            challengerScrollPane.setVisible(true);
        } else if (node.equals(waitHBox)) {
            extWaitHBox.setVisible(true);
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
        challengerCardMapByHBox = new HashMap<>();
        challengerHBoxMapByCard = new HashMap<>();
        ObservableList<Node> children = challengerCardsFlowPane.getChildren();

        int actualNode = 0;
        for (CardEnum card : CardEnum.values()) {
            if (actualNode < MAX_CARDS_NUMBERS) {
                if (children.get(actualNode) instanceof HBox) {
                    HBox hBox = (HBox) children.get(actualNode);
                    fillSingleHBox(hBox, card);
                    challengerCardMapByHBox.put(hBox, card);
                    challengerHBoxMapByCard.put(card, hBox);
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

    public void onClickEventChallengerCard(MouseEvent mouseEvent) {
        HBox ciao = (HBox) mouseEvent.getSource();
        CardEnum card = challengerCardMapByHBox.get(ciao);
    }

    private void selectChallengerCard(HBox hBoxSelected) {

    }
}
