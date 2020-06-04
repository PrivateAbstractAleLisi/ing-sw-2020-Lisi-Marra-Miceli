package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_PreGameStartedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.*;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    private String challengerUsername;

    private String STATE;
    /*
    States:
    - CARD_CHOICE
    - CHALLENGER_CARDS_CHOICE
    - FIRST_PLAYER_CHOICE
    - WAIT_CHALLENGER_CARDS
    - WAIT_PLAYER_CARD
    - WAIT_FIRST_PLAYER
     */

    //CENTER
    @FXML
    private HBox extWaitHBox;//InvisibleByDefault
    @FXML
    private Text upperText;
    @FXML
    private HBox waitHBox;

    //Challenger
    @FXML
    private ScrollPane challengerScrollPane; //InvisibleByDefault
    @FXML
    private FlowPane challengerCardsFlowPane;

    //CardChoice
    @FXML
    private TilePane cardChoiceTilePane;//InvisibleByDefault
    @FXML
    private HBox cardChoiceHBox;
    //CardsButton
    @FXML
    private VBox cardChoice_VBox1;//InvisibleByDefault
    @FXML
    private VBox cardChoice_VBox2;//InvisibleByDefault
    @FXML
    private VBox cardChoice_VBox3;//InvisibleByDefault


    //FirstPlayerChoice
    @FXML
    private TilePane challengerFirstPlayerPane;//InvisibleByDefault
    @FXML
    private HBox challengerFirstPlayerHBox;
    @FXML
    private VBox challengerFirstPlayerVBox1;//InvisibleByDefault
    @FXML
    private VBox challengerFirstPlayerVBox2;//InvisibleByDefault
    @FXML
    private VBox challengerFirstPlayerVBox3;//InvisibleByDefault

    //LEFT
    @FXML
    private HBox playerName_HBox1;
    @FXML
    private HBox playerName_HBox2;
    @FXML
    private HBox playerName_HBox3;

    //RIGHT
    @FXML
    private Button confirmButton;
    @FXML
    private HBox rightChoiceHBox1;
    @FXML
    private HBox rightChoiceHBox2;
    @FXML
    private HBox rightChoiceHBox3;

    private ArrayList<HBox> rightHBoxes;
    private int indexFirstFreeHBox;
    private int actualEnableHBox;

    //Maps
    private Map<HBox, CardEnum> challengerCardMapByHBox;
    private Map<CardEnum, HBox> challengerHBoxMapByCard;
    private Map<VBox, CardEnum> playerCardMapByVBox;
    private Map<CardEnum, VBox> playerVBoxMapByCard;
    private Map<VBox, String> firstPlayerUsernameByVBox;
    private Map<String, VBox> firstPlayerVBoxByUsername;
    private Map<String, CardEnum> firstPlayerCardByName;
    private Map<CardEnum, HBox> rightHBoxesMapByCard;
    private Map<String, HBox> rightHBoxesMapByName;

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

    public void update(CV_PreGameStartedGameEvent event) {
        setLeftPlayerNames(event);

        challengerUsername=event.getChallenger().toLowerCase();
        setLeftChallenger();
    }

    private void setLeftPlayerNames (CV_PreGameStartedGameEvent event){
        //Create a Queue
        LinkedList<HBox> playerNameHBoxes = new LinkedList<>();
        playerNameHBoxes.add(playerName_HBox1);
        playerNameHBoxes.add(playerName_HBox2);
        playerNameHBoxes.add(playerName_HBox3);

        int actualPlayersInRoom = event.getPlayersList().size();

        List<String> playerNames = event.getPlayersList();
        int index = 0;
        for (String player : playerNames) {
            //For each player i add the name to the left
            index = playerNames.indexOf(player);
            int position = index + 1;
            Text text = (Text) playerNameHBoxes.get(index).getChildren().get(0);
            text.setText(position + ") " + player.toUpperCase());
            if (player.toLowerCase().equals(gui.getUsername().toLowerCase())) {
                setLeftTextMyUsername(playerNameHBoxes.get(index));
            }
            playerNameHBoxes.get(index).setVisible(true);
        }
        //remove the already set HBoxes from the Queue
        playerNameHBoxes.subList(0, index + 1).clear();

        int remainingHBoxes = playerNameHBoxes.size();
        for (int i = 0; i < remainingHBoxes; i++) {
            //set invisible remaining HBoxes (using index 0 because I remove the HBoxes)
            playerNameHBoxes.remove(0).setVisible(false);
        }
    }

    private void setLeftTextMyUsername(Node node) {
        node.getStyleClass().add("my-username-hbox");
    }

    private void setLeftChallenger() {
        LinkedList<HBox> playerNameHBoxes = new LinkedList<>();
        playerNameHBoxes.add(playerName_HBox1);
        playerNameHBoxes.add(playerName_HBox2);
        playerNameHBoxes.add(playerName_HBox3);

        for (HBox hBox : playerNameHBoxes) {
            Text text = (Text) hBox.getChildren().get(0);
            String textShowed = text.getText().toLowerCase();

            String textToCompare = textShowed.substring(3);
            if (textToCompare.equals(challengerUsername.toLowerCase())) {
                //set image to visible
                hBox.getChildren().get(1).setVisible(true);
            }
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
                STATE = "WAIT_CHALLENGER_CARDS";
                waitText.setText(event.getActingPlayer().toUpperCase() + " is the challenger and he's choosing the cards.\nPlease wait");
                break;
            case "PLAYER_CARD":
                STATE = "WAIT_PLAYER_CARD";
                waitText.setText(event.getActingPlayer().toUpperCase() + " is choosing his card.\nPlease wait");
                break;
            case "FIRST_PLAYER":
                STATE = "WAIT_FIRST_PLAYER";
                waitText.setText("The challenger (" + event.getActingPlayer().toUpperCase() + ") is choosing the first player.\nPlease wait");
                break;
            default:
                waitText.setText("Please wait");
        }

        if (!extWaitHBox.isVisible()) {
            hourglassRotation = new Thread(() -> {
                ImageView hourglass = (ImageView) waitHBox.getChildren().get(1);
                try {
                    Thread.sleep(500);
                    while (waitHBox.isVisible()) {
                        hourglass.setRotate(hourglass.getRotate() + 2);
                        Thread.sleep(11);
                    }
                } catch (InterruptedException e) {
                    System.out.println("Turning Off the hourGlass");
                } finally {
                    Thread.currentThread().interrupt();
                    hourglass.setRotate(0);
                }
            });
            hourglassRotation.start();
        }
        showXRightHBoxes(0);
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

        showXRightHBoxes(1);
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
        showXRightHBoxes(cardToChoose);
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
        showXRightHBoxes(1);
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
        firstPlayerUsernameByVBox.put(vBox, username.toLowerCase());
        firstPlayerVBoxByUsername.put(username.toLowerCase(), vBox);
        firstPlayerCardByName.put(username.toLowerCase(),card);

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
            disableChallengerCardGreen(card);
            cleanRightHBox(card);
            if (challengerSelectedCards.size() < cardToChoose) {
                enableNotSelectedChallengerCards();
                disableConfirmButton();
            }
        } else {
            challengerSelectedCards.add(card);
            enableChallengerCardGreen(card);
            fillFirstFreeHBox(card);
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

    private void enableChallengerCardGreen(CardEnum card) {
        HBox hBox = challengerHBoxMapByCard.get(card);

        StackPane parentPane = (StackPane) hBox.getParent();
        Pane greenPane = (Pane) parentPane.getChildren().get(0);
        greenPane.setVisible(true);
    }

    private void disableChallengerCardGreen(CardEnum card) {
        HBox hBox = challengerHBoxMapByCard.get(card);

        StackPane parentPane = (StackPane) hBox.getParent();
        Pane greenPane = (Pane) parentPane.getChildren().get(0);
        greenPane.setVisible(false);
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
            disablePlayerCardGreen(card);
            cleanRightHBox(card);
            enableNotSelectedPlayerCards();
            playerSelectedCard = null;
            disableConfirmButton();
        } else {
            playerSelectedCard = card;
            enablePlayerCardGreen(card);
            fillFirstFreeHBox(card);
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

    private void enablePlayerCardGreen(CardEnum card) {
        VBox vBox = playerVBoxMapByCard.get(card);

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane greenPane = (Pane) parentPane.getChildren().get(0);
        greenPane.setVisible(true);
    }

    private void disablePlayerCardGreen(CardEnum card) {
        VBox vBox = playerVBoxMapByCard.get(card);

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane greenPane = (Pane) parentPane.getChildren().get(0);
        greenPane.setVisible(false);
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
            disableFirstPlayerGreen(username);
            cleanRightHBox(username);
            enableNotSelectedFirstPlayer();
            firstPlayerSelected = null;
            disableConfirmButton();
        } else {
            firstPlayerSelected = username;
            enableFirstPlayerGreen(username);
            fillFirstFreeHBox(username);
            disableNotSelectedFirstPlayer();
            enableConfirmButton();
        }

        System.out.println("Player clicked: " + username);
    }

    private void enableFistsPlayer(String username) {
        VBox vBox = firstPlayerVBoxByUsername.get(username.toLowerCase());
        vBox.setDisable(false);

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane greyPane = (Pane) parentPane.getChildren().get(2);
        greyPane.setVisible(false);
    }

    private void disableFirstPlayer(String username) {
        VBox vBox = firstPlayerVBoxByUsername.get(username.toLowerCase());
        vBox.setDisable(true);

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane greyPane = (Pane) parentPane.getChildren().get(2);
        greyPane.setVisible(true);
    }

    private void enableFirstPlayerGreen(String username) {
        VBox vBox = firstPlayerVBoxByUsername.get(username.toLowerCase());

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane greenPane = (Pane) parentPane.getChildren().get(0);
        greenPane.setVisible(true);
    }

    private void disableFirstPlayerGreen(String username) {
        VBox vBox = firstPlayerVBoxByUsername.get(username.toLowerCase());

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane greenPane = (Pane) parentPane.getChildren().get(0);
        greenPane.setVisible(false);
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

    private void showXRightHBoxes(int numberOfHBoxesToEnable) {
        rightHBoxes = new ArrayList<>();
        rightHBoxes.add(rightChoiceHBox1);
        rightHBoxes.add(rightChoiceHBox2);
        rightHBoxes.add(rightChoiceHBox3);

        if (numberOfHBoxesToEnable <= 3) {
            int index;
            for (index = 0; index < numberOfHBoxesToEnable; index++) {
                showCleanRightHBox(rightHBoxes.get(index));
            }
            while (index < 3) {
                hideRightHBox(rightHBoxes.get(index));
                index++;
            }
            indexFirstFreeHBox = 0;
            actualEnableHBox = numberOfHBoxesToEnable;

            rightHBoxesMapByCard = new HashMap<>();
            rightHBoxesMapByName = new HashMap<>();
            firstPlayerCardByName = new HashMap<>();
        } else {
            System.out.println("Not enough HBoxes");
        }
    }

    private void showCleanRightHBox(HBox hBox) {
        hBox.setVisible(true);
        hBox.getChildren().get(0).setVisible(false);
        hBox.getChildren().get(1).setVisible(false);
        VBox vBox = (VBox) hBox.getChildren().get(1);
        vBox.getChildren().get(0).setVisible(false);
        vBox.getChildren().get(1).setVisible(false);
    }

    private void hideRightHBox(HBox hBox) {
        hBox.setVisible(false);
        hBox.getChildren().get(0).setVisible(false);
        hBox.getChildren().get(1).setVisible(false);
        VBox vBox = (VBox) hBox.getChildren().get(1);
        vBox.getChildren().get(0).setVisible(false);
        vBox.getChildren().get(1).setVisible(false);
    }

    private void fillFirstFreeHBox(CardEnum card) {
        if (indexFirstFreeHBox < actualEnableHBox) {
            fillFirstFreeHBox(card, card.getName(), card.getDescription());
        }
    }

    private void fillFirstFreeHBox(String name) {
        CardEnum card = firstPlayerCardByName.get(name.toLowerCase());
        if (indexFirstFreeHBox < actualEnableHBox) {
            fillFirstFreeHBox(card, name, "");
        }
    }

    private void fillFirstFreeHBox(CardEnum card, String bigText, String smallText) {
        if (indexFirstFreeHBox < actualEnableHBox) {
            rightHBoxes = new ArrayList<>();
            rightHBoxes.add(rightChoiceHBox1);
            rightHBoxes.add(rightChoiceHBox2);
            rightHBoxes.add(rightChoiceHBox3);

            HBox hBox = rightHBoxes.get(indexFirstFreeHBox);
            ImageView imageView = (ImageView) hBox.getChildren().get(0);
            imageView.setImage(new Image(card.getImgUrl()));
            imageView.setVisible(true);

            VBox vBox = (VBox) hBox.getChildren().get(1);
            vBox.setVisible(true);
            Text name = (Text) vBox.getChildren().get(0);
            name.setVisible(true);

            Label longText = (Label) vBox.getChildren().get(1);
            if (smallText.equals("")) {
                //I'm setting a player
                name.setText(bigText.toUpperCase());
                longText.setVisible(false);
                rightHBoxesMapByName.put(bigText.toLowerCase(), hBox);
            } else {
                //I'm setting a CardEnum
                name.setText(bigText);
                longText.setText(smallText);
                longText.setVisible(true);
                rightHBoxesMapByCard.put(card, hBox);
            }

            resetIndexFreeHBox();
        }
    }

    private void resetIndexFreeHBox() {
        rightHBoxes = new ArrayList<>();
        rightHBoxes.add(rightChoiceHBox1);
        rightHBoxes.add(rightChoiceHBox2);
        rightHBoxes.add(rightChoiceHBox3);

        for (int index = 0; index < 3; index++) {
            ImageView imageView = (ImageView) rightHBoxes.get(index).getChildren().get(0);
            if (!imageView.isVisible()) {
                indexFirstFreeHBox = index;
                return;
            }
        }
        indexFirstFreeHBox = 3;
    }

    private void cleanRightHBox(CardEnum card) {
        HBox hBox = rightHBoxesMapByCard.get(card);
        showCleanRightHBox(hBox);
        rightHBoxesMapByCard.remove(card);
        resetIndexFreeHBox();
    }

    private void cleanRightHBox(String name) {
        HBox hBox = rightHBoxesMapByName.get(name.toLowerCase());
        showCleanRightHBox(hBox);
        rightHBoxesMapByName.remove(name.toLowerCase());
        resetIndexFreeHBox();
    }


}
