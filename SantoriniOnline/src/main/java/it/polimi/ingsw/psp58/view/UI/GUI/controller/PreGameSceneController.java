package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.event.gameEvents.lobby.CV_RoomUpdateGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.*;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.*;

public class PreGameSceneController {
    private GUI gui;

    //CONSTANT
    public final double MAX_CARDS_NUMBERS = 15;

    private String STATE;
    /*
    States:
    - CARD_CHOICE
    - CHALLENGER_CARDS_CHOICE
    - FIRST_PLAYER_CHOICE
    - WAIT_CHALLENGER_CARD
    - WAIT_PLAYER_CARD
    - WAIT_FIRST_PLAYER
     */

    //CENTER
    public Text upperText;
    public HBox extWaitHBox;//InvisibleByDefault
    public HBox waitHBox;

    //Challenger
    public ScrollPane challengerScrollPane; //InvisibleByDefault
    public FlowPane challengerCardsFlowPane;

    //CardChoice
    public TilePane cardChoiceTilePane;//InvisibleByDefault
    public HBox cardChoiceHBox;
    //CardsButton
    public VBox cardChoice_VBox1;//InvisibleByDefault
    public VBox cardChoice_VBox2;//InvisibleByDefault
    public VBox cardChoice_VBox3;//InvisibleByDefault


    //FirstPlayerChoice
    public TilePane challengerFirstPlayerPane;//InvisibleByDefault
    public HBox challengerFirstPlayerHBox;
    public VBox challengerFirstPlayerVBox1;//InvisibleByDefault
    public VBox challengerFirstPlayerVBox2;//InvisibleByDefault
    public VBox challengerFirstPlayerVBox3;//InvisibleByDefault

    //LEFT
    public HBox playerName_HBox1;
    public HBox playerName_HBox2;
    public HBox playerName_HBox3;

    //RIGHT
    public Button confirmButton;
    public Pane chosenCardPane1;
    public Pane chosenCardPane2;
    public Pane chosenCardPane3;

    //BOTTOM
    public Button superUserButton;

    //Maps
    private Map<HBox, CardEnum> challengerCardMapByHBox;
    private Map<CardEnum, HBox> challengerHBoxMapByCard;
    private Map<VBox, CardEnum> playerCardMapByVBox;
    private Map<CardEnum, VBox> playerVBoxMapByCard;
    private Map<VBox, String> firstPlayerUsernameByVBox;
    private Map<String, VBox> firstPlayerVBoxByUsername;

    //CardSelection
    private int cardToChoose;
    private ArrayList<CardEnum> challengerSelectedCards = new ArrayList<>();
    private CardEnum playerSelectedCard;
    private ArrayList<CardEnum> playerAvailableCards;

    //firstPlayerSelection
    private String firstPlayerSelected;
    private ArrayList<String> usernamesAvailable;

    Thread hourglassRotation;

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public void setVisibleOnlyThisNode(Node node) {
        cardChoiceTilePane.setVisible(false);
        challengerScrollPane.setVisible(false);
        challengerFirstPlayerPane.setVisible(false);
        extWaitHBox.setVisible(false);

        if (!node.equals(waitHBox)) {
            if (hourglassRotation != null && hourglassRotation.isAlive()) {
                hourglassRotation.interrupt();
            }
        }

        if (node.equals(cardChoiceTilePane)) {
            cardChoiceTilePane.setVisible(true);
        } else if (node.equals(challengerScrollPane)) {
            challengerScrollPane.setVisible(true);
        } else if (node.equals(challengerFirstPlayerPane)) {
            challengerFirstPlayerPane.setVisible(true);
        } else if (node.equals(waitHBox)) {
            extWaitHBox.setVisible(true);
        }
    }

    private void setTextToBold(Text text, double fontSize) {
        text.setFont(new Font("System Bold", fontSize));
    }

    private void setTitleCenter(String title) {
        upperText.setText(title.toUpperCase());
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
                text = (Text) playerName_HBox1.getChildren().get(0);
                text.setText("1) " + usersInRoom[0].toUpperCase());

                playerName_HBox1.setVisible(true);
                playerName_HBox2.setVisible(false);
                playerName_HBox3.setVisible(false);
                break;
            case 2:
                text = (Text) playerName_HBox1.getChildren().get(0);
                text.setText("1) " + usersInRoom[0].toUpperCase());

                text = (Text) playerName_HBox2.getChildren().get(0);
                text.setText("2) " + usersInRoom[1].toUpperCase());

                playerName_HBox1.setVisible(true);
                playerName_HBox2.setVisible(true);
                playerName_HBox3.setVisible(false);
                break;
            case 3:
                text = (Text) playerName_HBox1.getChildren().get(0);
                text.setText("1) " + usersInRoom[0].toUpperCase());

                text = (Text) playerName_HBox2.getChildren().get(0);
                text.setText("2) " + usersInRoom[1].toUpperCase());

                text = (Text) playerName_HBox3.getChildren().get(0);
                text.setText("3) " + usersInRoom[2].toUpperCase());

                playerName_HBox1.setVisible(true);
                playerName_HBox2.setVisible(true);
                playerName_HBox3.setVisible(true);
                break;
            default:
                System.out.println("ERROR");
        }
    }

    public void update(CV_WaitPreMatchGameEvent event) {
        if (!extWaitHBox.isVisible() && hourglassRotation != null && hourglassRotation.isAlive()) {
            hourglassRotation.interrupt();
        }
        Text waitText = (Text) waitHBox.getChildren().get(0);
        setTitleCenter("Wait your turn");

        switch (event.getWaitCode()) {
            case "CHALLENGER_CARDS":
                STATE = "WAIT_CHALLENGER_CARD";
                waitText.setText(event.getChallenger().toUpperCase() + " is the challenger and he's choosing the cards.\nPlease wait");
                break;
            default:
                waitText.setText("Please wait");
        }

        if (!extWaitHBox.isVisible()) {
            hourglassRotation = new Thread(() -> {
                ImageView hourglass = (ImageView) waitHBox.getChildren().get(1);
                try {
                    while (waitHBox.isVisible()) {
                        hourglass.setRotate(hourglass.getRotate() + 2);
                        Thread.sleep(11);
                    }
                } catch (InterruptedException e) {
                    System.out.println("Turning Off the hourGlass");
                    ;
                } finally {
                    Thread.currentThread().interrupt();
                    hourglass.setRotate(0);
                }
            });
            hourglassRotation.start();
        }
        setVisibleOnlyThisNode(waitHBox);
    }

    public void update(CV_CardChoiceRequestGameEvent event) {
        STATE = "CARD_CHOICE";
        cardToChoose = 1;
        playerSelectedCard = null;
        playerCardMapByVBox = new HashMap<>();
        playerVBoxMapByCard = new HashMap<>();
        playerAvailableCards = new ArrayList<>();
        playerAvailableCards.addAll(event.getAvailableCards());

        LinkedList<VBox> vBoxes = new LinkedList<>();
        vBoxes.add(cardChoice_VBox1);
        vBoxes.add(cardChoice_VBox2);
        vBoxes.add(cardChoice_VBox3);

        setTitleCenter("CHOOSE YOUR CARD!!");

        ArrayList<CardEnum> allCards = new ArrayList<>();
        allCards.addAll(event.getAvailableCards());
        if (event.getUsedCards() != null) {
            allCards.addAll(event.getUsedCards());
        }
        allCards.sort(Comparator.comparing(CardEnum::getName));

        if (allCards.size() == 2) {
            cardChoiceHBox.getChildren().remove(2);
            vBoxes.removeLast();
        } else if (allCards.size() == 1) {
            cardChoiceHBox.getChildren().remove(2);
            vBoxes.removeLast();
            cardChoiceHBox.getChildren().remove(1);
            vBoxes.removeLast();
        }

        for (CardEnum card : allCards) {
            fillPlayerChoiceCard(vBoxes.remove(), card, event.getAvailableCards().contains(card));
        }

        setVisibleOnlyThisNode(cardChoiceTilePane);
    }

    private void fillPlayerChoiceCard(VBox vBox, CardEnum card, boolean available) {
        if (!available) {
            vBox.setDisable(true);
            vBox.getStyleClass().clear();
            StackPane parent = (StackPane) vBox.getParent();
            parent.getChildren().get(2).setVisible(true);
        }

        playerCardMapByVBox.put(vBox, card);
        playerVBoxMapByCard.put(card, vBox);

        ImageView image = (ImageView) vBox.getChildren().get(0);
        image.setImage(new Image(card.getImgUrl()));

        VBox internalVBox = (VBox) vBox.getChildren().get(1);
        Text cardName = (Text) internalVBox.getChildren().get(0);
        Text cardDescription = (Text) internalVBox.getChildren().get(1);

        cardName.setText(card.getName());
        cardDescription.setText(card.getDescription());

        vBox.setVisible(true);
    }

    /**
     * CHALLENGER
     * Receive the event CV_ChallengerChosenEvent and handle the choice of the cards
     *
     * @param event Contains some data like the size of the room.
     */
    public void update(CV_ChallengerChosenEvent event) {
        STATE = "CHALLENGER_CARDS_CHOICE";
        cardToChoose = event.getRoomSize();
        challengerSelectedCards = new ArrayList<>();
        System.out.println("You're the chellenger, chose " + event.getRoomSize() + " cards");
        setTitleCenter("YOU ARE THE CHALLENGER!! \tCHOOSE " + event.getRoomSize() + " CARDS");

        fillAllChallengerCards();
        showAllChallengerCards();
    }

    /**
     * CHALLENGER
     * Set Visible all the Cards
     */
    public void showAllChallengerCards() {
        setVisibleOnlyThisNode(challengerScrollPane);
    }

    /**
     * CHALLENGER
     * Fill all the card to show them to the challenger
     */
    private void fillAllChallengerCards() {
        challengerCardMapByHBox = new HashMap<>();
        challengerHBoxMapByCard = new HashMap<>();
        ObservableList<Node> children = challengerCardsFlowPane.getChildren();

        int actualNode = 0;
        for (CardEnum card : CardEnum.values()) {
            if (actualNode < MAX_CARDS_NUMBERS) {
                if (children.get(actualNode) instanceof StackPane) {
                    StackPane stackPane = (StackPane) children.get(actualNode);
                    HBox hBox = (HBox) stackPane.getChildren().get(1);
                    fillSingleChallengerHBox(hBox, card);
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
    private void fillSingleChallengerHBox(HBox hBox, CardEnum card) {
        ImageView imageView = (ImageView) hBox.getChildren().get(0);
        VBox vBox = (VBox) hBox.getChildren().get(1);
        Text cardName = (Text) vBox.getChildren().get(0);
        Text cardDescription = (Text) vBox.getChildren().get(1);

        imageView.setImage(new Image(card.getImgUrl()));
        cardName.setText(card.getName());
        cardDescription.setText(card.getDescription());
    }

    public void update(CV_ChallengerChooseFirstPlayerRequestEvent event) {
        STATE = "FIRST_PLAYER_CHOICE";
        firstPlayerUsernameByVBox = new HashMap<>();
        firstPlayerVBoxByUsername = new HashMap<>();
        usernamesAvailable = new ArrayList<>();
        usernamesAvailable.addAll(event.getPlayers());
        Collections.sort(usernamesAvailable);

        LinkedList<VBox> vBoxes = new LinkedList<>();
        vBoxes.add(challengerFirstPlayerVBox1);
        vBoxes.add(challengerFirstPlayerVBox2);
        vBoxes.add(challengerFirstPlayerVBox3);

        setTitleCenter("CHOOSE THE FIRST PLAYER!!");

        if (usernamesAvailable.size() == 2) {
            challengerFirstPlayerHBox.getChildren().remove(2);
            vBoxes.removeLast();
        }

        for (String username : usernamesAvailable) {
            fillFirstPlayerChoice(vBoxes.remove(), event.cardChosenByPlayer(username), username);
        }

        setVisibleOnlyThisNode(challengerFirstPlayerPane);
    }

    private void fillFirstPlayerChoice(VBox vBox, CardEnum card, String username) {
        firstPlayerUsernameByVBox.put(vBox, username);
        firstPlayerVBoxByUsername.put(username, vBox);

        ImageView image = (ImageView) vBox.getChildren().get(0);
        image.setImage(new Image(card.getImgUrl()));

        VBox internalVBox = (VBox) vBox.getChildren().get(1);
        Text playerName = (Text) internalVBox.getChildren().get(0);
        playerName.setText(username.toUpperCase());

        vBox.setVisible(true);
    }

    public void onClickEventConfirmButton() {
        switch (STATE) {
            case "CHALLENGER_CARDS_CHOICE":
                sendChallengerCardsChosenEvent();
                break;
            case "CARD_CHOICE":
                sendPlayerCardsChosenEvent();
                break;
            case "FIRST_PLAYER_CHOICE":
                sendFirstPlayerChosenEvent();
                break;
            default:
                System.out.println("ERROR - CASE NOT YET DEFINED");
        }
    }

    private void enableConfirmButton() {
        confirmButton.setDisable(false);
        StackPane parentPane = (StackPane) confirmButton.getParent();
        Pane greyPane = (Pane) parentPane.getChildren().get(3);
        greyPane.setVisible(false);
    }

    private void disableConfirmButton() {
        confirmButton.setDisable(true);
        StackPane parentPane = (StackPane) confirmButton.getParent();
        Pane greyPane = (Pane) parentPane.getChildren().get(3);
        greyPane.setVisible(true);
    }

    public void onClickEventChallengerCard(MouseEvent mouseEvent) {
        HBox hBox = (HBox) mouseEvent.getSource();
        CardEnum card = challengerCardMapByHBox.get(hBox);

        if (challengerSelectedCards.contains(card)) {
            //Already selected card is selected a second time
            challengerSelectedCards.remove(card);
            disableChallengerCardGlow(card);
            if (challengerSelectedCards.size() < cardToChoose) {
                enableNotSelectedChallengerCards();
                disableConfirmButton();
            }
        } else {
            challengerSelectedCards.add(card);
            enableChallengerCardGlow(card);
            if (challengerSelectedCards.size() == cardToChoose) {
                disableNotSelectedChallengerCards();
                enableConfirmButton();
            }
        }

        System.out.println("Card clicked: " + card);
    }

    private void enableChallengerCard(CardEnum card) {
        HBox hBox = challengerHBoxMapByCard.get(card);
        hBox.setDisable(false);

        StackPane parentPane = (StackPane) hBox.getParent();
        Pane greyPane = (Pane) parentPane.getChildren().get(2);
        greyPane.setVisible(false);
    }

    private void disableChallengerCard(CardEnum card) {
        HBox hBox = challengerHBoxMapByCard.get(card);
        hBox.setDisable(true);

        StackPane parentPane = (StackPane) hBox.getParent();
        Pane greyPane = (Pane) parentPane.getChildren().get(2);
        greyPane.setVisible(true);
    }

    private void enableChallengerCardGlow(CardEnum card) {
        HBox hBox = challengerHBoxMapByCard.get(card);

        StackPane parentPane = (StackPane) hBox.getParent();
        Pane glowPane = (Pane) parentPane.getChildren().get(0);
        glowPane.setVisible(true);
    }

    private void disableChallengerCardGlow(CardEnum card) {
        HBox hBox = challengerHBoxMapByCard.get(card);

        StackPane parentPane = (StackPane) hBox.getParent();
        Pane glowPane = (Pane) parentPane.getChildren().get(0);
        glowPane.setVisible(false);
    }

    private void disableNotSelectedChallengerCards() {
        for (CardEnum card : CardEnum.values()) {
            if (!challengerSelectedCards.contains(card)) {
                disableChallengerCard(card);
            }
        }
    }

    private void enableNotSelectedChallengerCards() {
        for (CardEnum card : CardEnum.values()) {
            if (!challengerSelectedCards.contains(card)) {
                enableChallengerCard(card);
            }
        }
    }

    private void sendChallengerCardsChosenEvent() {
        System.out.println("Send challenger card event");
        if (cardToChoose == challengerSelectedCards.size()) {
            VC_ChallengerCardsChosenEvent event = new VC_ChallengerCardsChosenEvent("", challengerSelectedCards);
            gui.getClient().sendEvent(event);
            disableConfirmButton();
            challengerSelectedCards = new ArrayList<>();
        } else {
            System.out.println("Error");
        }
    }

    public void onClickEventPlayerCard(MouseEvent mouseEvent) {
        VBox vBox = (VBox) mouseEvent.getSource();
        CardEnum card = playerCardMapByVBox.get(vBox);

        if (playerSelectedCard != null && playerSelectedCard.equals(card)) {
            //Already selected card is selected a second time
            playerSelectedCard = null;
            disablePlayerCardGlow(card);
            enableNotSelectedPlayerCards();
            disableConfirmButton();
        } else {
            playerSelectedCard = card;
            enablePlayerCardGlow(card);
            disableNotSelectedPlayerCards();
            enableConfirmButton();
        }

        System.out.println("Card clicked: " + card);
    }

    private void enablePlayerCard(CardEnum card) {
        VBox vBox = playerVBoxMapByCard.get(card);
        vBox.setDisable(false);

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane greyPane = (Pane) parentPane.getChildren().get(2);
        greyPane.setVisible(false);
    }

    private void disablePlayerCard(CardEnum card) {
        VBox vBox = playerVBoxMapByCard.get(card);
        vBox.setDisable(true);

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane greyPane = (Pane) parentPane.getChildren().get(2);
        greyPane.setVisible(true);
    }

    private void enablePlayerCardGlow(CardEnum card) {
        VBox vBox = playerVBoxMapByCard.get(card);

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane glowPane = (Pane) parentPane.getChildren().get(0);
        glowPane.setVisible(true);
    }

    private void disablePlayerCardGlow(CardEnum card) {
        VBox vBox = playerVBoxMapByCard.get(card);

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane glowPane = (Pane) parentPane.getChildren().get(0);
        glowPane.setVisible(false);
    }

    private void disableNotSelectedPlayerCards() {
        for (CardEnum card : playerAvailableCards) {
            if (!playerSelectedCard.equals(card)) {
                disablePlayerCard(card);
            }
        }
    }

    private void enableNotSelectedPlayerCards() {
        for (CardEnum card : playerAvailableCards) {
            if (!playerSelectedCard.equals(card)) {
                enablePlayerCard(card);
            }
        }
    }

    private void sendPlayerCardsChosenEvent() {
        System.out.println("Send player choice card event");
        if (playerSelectedCard != null) {
            VC_PlayerCardChosenEvent event = new VC_PlayerCardChosenEvent(gui.getUsername(), playerSelectedCard);
            gui.getClient().sendEvent(event);
            disableConfirmButton();
            playerSelectedCard = null;
        } else {
            System.out.println("Error");
        }

    }

    public void onClickFirsPlayerChoice(MouseEvent mouseEvent) {
        VBox vBox = (VBox) mouseEvent.getSource();
        String username = firstPlayerUsernameByVBox.get(vBox);

        if (firstPlayerSelected != null && firstPlayerSelected.equals(username)) {
            //Already selected card is selected a second time
            firstPlayerSelected = null;
            disableFirstPlayerGlow(username);
            enableNotSelectedFirstPlayer();
            disableConfirmButton();
        } else {
            firstPlayerSelected = username;
            enableFirstPlayerGlow(username);
            disableNotSelectedFirstPlayer();
            enableConfirmButton();
        }

        System.out.println("Player clicked: " + username);
    }

    private void enableFistsPlayer(String username) {
        VBox vBox = firstPlayerVBoxByUsername.get(username);
        vBox.setDisable(false);

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane greyPane = (Pane) parentPane.getChildren().get(2);
        greyPane.setVisible(false);
    }

    private void disableFirstPlayer(String username) {
        VBox vBox = firstPlayerVBoxByUsername.get(username);
        vBox.setDisable(true);

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane greyPane = (Pane) parentPane.getChildren().get(2);
        greyPane.setVisible(true);
    }

    private void enableFirstPlayerGlow(String username) {
        VBox vBox = firstPlayerVBoxByUsername.get(username);

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane glowPane = (Pane) parentPane.getChildren().get(0);
        glowPane.setVisible(true);
    }

    private void disableFirstPlayerGlow(String username) {
        VBox vBox = firstPlayerVBoxByUsername.get(username);

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane glowPane = (Pane) parentPane.getChildren().get(0);
        glowPane.setVisible(false);
    }

    private void disableNotSelectedFirstPlayer() {
        for (String username : usernamesAvailable) {
            if (!firstPlayerSelected.equals(username)) {
                disableFirstPlayer(username);
            }
        }
    }

    private void enableNotSelectedFirstPlayer() {
        for (String username : usernamesAvailable) {
            if (!firstPlayerSelected.equals(username)) {
                enableFistsPlayer(username);
            }
        }
    }

    private void sendFirstPlayerChosenEvent() {
        System.out.println("Send first player choice event");
        if (firstPlayerSelected != null) {
            VC_ChallengerChosenFirstPlayerEvent event = new VC_ChallengerChosenFirstPlayerEvent(firstPlayerSelected);
            gui.getClient().sendEvent(event);
            disableConfirmButton();
            firstPlayerSelected = null;
        } else {
            System.out.println("Error");
        }

    }

}
