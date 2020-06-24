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
import javafx.scene.text.Text;

import java.util.*;

/**
 * This class handle the choose of the Cards and the choose of the first player, loading all the cards and the player dynamically.
 */
public class PreGameSceneController {
    /**
     * Main {@link GUI} object.
     */
    private GUI gui;

    /**
     * Define the Max Number of card to show on the screen.
     * To increase the number of cards, please update this value and create new cards in the FXML file.
     */
    public static final double MAX_CARDS_NUMBERS = 15;

    /**
     * The username of the challenger.
     */
    private String challengerUsername;

    /**
     * It represents the actual state of the prematch for this player.
     * The possibles STATES are:
     * - CARD_CHOICE
     * - CHALLENGER_CARDS_CHOICE
     * - FIRST_PLAYER_CHOICE
     * - WAIT_CHALLENGER_CARDS
     * - WAIT_PLAYER_CARD
     * - WAIT_FIRST_PLAYER
     */
    private String STATE;

    /* ----------------------------------------------------------------------------------------------
                                         CENTER OF BORDER PANE
       ----------------------------------------------------------------------------------------------*/
    /**
     * HBox containing the Nodes for Wait State, Not visible by default.
     */
    @FXML
    private HBox extWaitHBox;//InvisibleByDefault
    /**
     * HBox containing the Hourglass Image.
     */
    @FXML
    private HBox waitHBox;
    /**
     * Title and Upper text of the entire Scene with title or command to execute.
     */
    @FXML
    private Text upperText;

    /* ----------------------------------------------------------------------------------------------
                                         Challenger Section
       ----------------------------------------------------------------------------------------------*/
    /**
     * {@link ScrollPane} showed to the challenger containing the {@link FlowPane} with the Cards, not visible by default.
     */
    @FXML
    private ScrollPane challengerScrollPane; //InvisibleByDefault
    /**
     * {@link FlowPane} showed to the challenger containing all the Cards.
     */
    @FXML
    private FlowPane challengerCardsFlowPane;

    /* ----------------------------------------------------------------------------------------------
                                         PlayerCardChoice section
       ----------------------------------------------------------------------------------------------*/
    /**
     * Pane containing the Nodes to show and select the Card for each player, not visible by default.
     */
    @FXML
    private TilePane cardChoiceTilePane;//InvisibleByDefault
    /**
     * HBox containing the Nodes to show and select the Card for each player.
     */
    @FXML
    private HBox cardChoiceHBox;
    //CardsButton
    /**
     * VBox containing the ImageView of the first God, the Name and the description, not visible by default.
     */
    @FXML
    private VBox cardChoice_VBox1;//InvisibleByDefault
    /**
     * VBox containing the ImageView of the second God, the Name and the description, not visible by default.
     */
    @FXML
    private VBox cardChoice_VBox2;//InvisibleByDefault
    /**
     * VBox containing the ImageView of the third God, the Name and the description, not visible by default.
     */
    @FXML
    private VBox cardChoice_VBox3;//InvisibleByDefault


   /* ----------------------------------------------------------------------------------------------
                                          FirstPlayerChoice Section
       ----------------------------------------------------------------------------------------------*/
    /**
     * Pane showed to the challenger containing the Nodes to show and select the first Player, not visible by default.
     */
    @FXML
    private TilePane challengerFirstPlayerPane;//InvisibleByDefault
    /**
     * HBox containing the Nodes to show and select the first Player.
     */
    @FXML
    private HBox challengerFirstPlayerHBox;
    /**
     * VBox containing the ImageView of the first God selected by the player and the Name of the player, not visible by default.
     */
    @FXML
    private VBox challengerFirstPlayerVBox1;//InvisibleByDefault
    /**
     * VBox containing the ImageView of the first God selected by the player and the Name of the player, not visible by default.
     */
    @FXML
    private VBox challengerFirstPlayerVBox2;//InvisibleByDefault
    /**
     * VBox containing the ImageView of the first God selected by the player and the Name of the player, not visible by default.
     */
    @FXML
    private VBox challengerFirstPlayerVBox3;//InvisibleByDefault

    /* ----------------------------------------------------------------------------------------------
                                         LEFT SECTION
       ----------------------------------------------------------------------------------------------*/
    /**
     * HBox containing the player name. If the challenger, contains also an Image.
     */
    @FXML
    private HBox playerName_HBox1;
    /**
     * HBox containing the player name. If the challenger, contains also an Image.
     */
    @FXML
    private HBox playerName_HBox2;
    /**
     * HBox containing the player name. If the challenger, contains also an Image.
     */
    @FXML
    private HBox playerName_HBox3;
    @FXML
    private Button superUserButton;

    /* ----------------------------------------------------------------------------------------------
                                         RIGHT SECTION
       ----------------------------------------------------------------------------------------------*/
    /**
     * Confirm Button on the bottom-right.
     */
    @FXML
    private Button confirmButton;
    /**
     * HBox containing the Card or Player selected by the current user
     */
    @FXML
    private HBox rightChoiceHBox1;
    /**
     * HBox containing the Card or Player selected by the current user
     */
    @FXML
    private HBox rightChoiceHBox2;
    /**
     * HBox containing the Card or Player selected by the current user
     */
    @FXML
    private HBox rightChoiceHBox3;

    /**
     * ArrayList of HBox containing the HBoxes on the upper-right section of the Scene
     */
    private ArrayList<HBox> rightHBoxes;
    /**
     * Index of the first HBox not set, next to be filled.
     */
    private int indexFirstFreeHBox;
    /**
     * Number of HBoxes actually enable.
     */
    private int numberActualEnableHBoxes;

    /* ----------------------------------------------------------------------------------------------
                                         MAPS
       ----------------------------------------------------------------------------------------------*/
    /**
     * Map to find Card from HBox, used during Challenger Cards selection
     */
    private Map<HBox, CardEnum> challengerCardMapByHBox;
    /**
     * Map to find HBox from Card, used during Challenger Cards selection
     */
    private Map<CardEnum, HBox> challengerHBoxMapByCard;
    /**
     * Map to find Card from VBox, used during Player Card selection
     */
    private Map<VBox, CardEnum> playerCardMapByVBox;
    /**
     * Map to find VBox from Card, used during Player Card selection
     */
    private Map<CardEnum, VBox> playerVBoxMapByCard;
    /**
     * Map to find Player's username from VBox, used during First Player selection
     */
    private Map<VBox, String> firstPlayerUsernameByVBox;
    /**
     * Map to find VBox from Player's username, used during First Player selection
     */
    private Map<String, VBox> firstPlayerVBoxByUsername;
    /**
     * Map to find Card from Player's username, used during First Player selection
     */
    private Map<String, CardEnum> firstPlayerCardByName;
    /**
     * Map to find HBoxes on the Right from Card.
     */
    private Map<CardEnum, HBox> rightHBoxesMapByCard;
    /**
     * Map to find HBoxes on the Right from Player's username.
     */
    private Map<String, HBox> rightHBoxesMapByName;

    /* ----------------------------------------------------------------------------------------------
                                         GENERIC CARD SELECTION
       ----------------------------------------------------------------------------------------------*/
    /**
     * Number of card to choose.
     */
    private int cardToChoose;
    /**
     * ArrayList with the Cards selected by the challenger, before Confirm Button click.
     */
    private ArrayList<CardEnum> challengerSelectedCards = new ArrayList<>();
    /**
     * Card selected by the player, before Confirm Button click.
     */
    private CardEnum playerSelectedCard;
    /**
     * Available cards to chose for the player.
     */
    private ArrayList<CardEnum> playerAvailableCards;

    /* ----------------------------------------------------------------------------------------------
                                         FIRST PLAYER SELECTION
       ----------------------------------------------------------------------------------------------*/
    /**
     * Username of the selected First Player, before Confirm Button click.
     */
    private String firstPlayerSelected;
    /**
     * Usernames of all Players, to chose the firs player.
     */
    private ArrayList<String> usernamesAvailable;

    /**
     * Thread for HourGlassRotation
     */
    Thread hourglassRotation;

    /* ----------------------------------------------------------------------------------------------
                                          UTILITY AND SETTER METHODS
       ----------------------------------------------------------------------------------------------*/

    /**
     * Set GUI
     *
     * @param gui Gui
     */
    public void setGui(GUI gui) {
        this.gui = gui;
    }

    /**
     * Show only one Node, hide the others.
     *
     * @param node the Node to be shown
     */
    private void setVisibleOnlyThisNode(Node node) {
        cardChoiceTilePane.setVisible(false);
        challengerScrollPane.setVisible(false);
        challengerFirstPlayerPane.setVisible(false);
        extWaitHBox.setVisible(false);

        if (!node.equals(waitHBox) && hourglassRotation != null && hourglassRotation.isAlive()) {
            hourglassRotation.interrupt();
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

    /**
     * Set the title and upper text
     *
     * @param title String with title or message
     */
    private void setTitleCenter(String title) {
        upperText.setText(title.toUpperCase());
    }

    /**
     * Set the Player Names on the Left
     *
     * @param event {@link CV_PreGameStartedGameEvent} event that contains required info.
     */
    private void setLeftPlayerNames(CV_PreGameStartedGameEvent event) {
        //Create a Queue
        LinkedList<HBox> playerNameHBoxes = new LinkedList<>();
        playerNameHBoxes.add(playerName_HBox1);
        playerNameHBoxes.add(playerName_HBox2);
        playerNameHBoxes.add(playerName_HBox3);

        List<String> playerNames = event.getPlayersList();
        int index = 0;
        for (String player : playerNames) {
            //For each player i add the name to the left
            index = playerNames.indexOf(player);
            int position = index + 1;
            Text text = (Text) playerNameHBoxes.get(index).getChildren().get(0);
            text.setText(position + ") " + player.toUpperCase());
            if (player.equalsIgnoreCase(gui.getUsername())) {
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

    /**
     * Set a Red background around my Name on the left
     *
     * @param node Node with my Username
     */
    private void setLeftTextMyUsername(Node node) {
        node.getStyleClass().add("my-username-hBox");
    }

    /**
     * Show the Star ImageView near the ChallengerUsername
     */
    private void setLeftChallenger() {
        LinkedList<HBox> playerNameHBoxes = new LinkedList<>();
        playerNameHBoxes.add(playerName_HBox1);
        playerNameHBoxes.add(playerName_HBox2);
        playerNameHBoxes.add(playerName_HBox3);

        for (HBox hBox : playerNameHBoxes) {
            Text text = (Text) hBox.getChildren().get(0);
            String textShowed = text.getText().toLowerCase();

            String textToCompare = textShowed.substring(3);
            if (textToCompare.equalsIgnoreCase(challengerUsername)) {
                //set image to visible
                hBox.getChildren().get(1).setVisible(true);
            }
        }
    }

    /* ----------------------------------------------------------------------------------------------
                                          UPDATE EVENTS METHODS
       ----------------------------------------------------------------------------------------------*/

    /**
     * Update the Scene with the info contained in the event
     *
     * @param event {@link CV_PreGameStartedGameEvent} event that contains required info.
     */
    public void update(CV_PreGameStartedGameEvent event) {
        setLeftPlayerNames(event);

        challengerUsername = event.getChallenger().toLowerCase();
        setLeftChallenger();
    }

    /**
     * Show the Wait Scene and wait until next event.
     *
     * @param event {@link CV_WaitPreMatchGameEvent} event.
     */
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

    /**
     * Receive the event CV_CardChoiceRequestGameEvent and handle the choice of the card for this single user.
     *
     * @param event Contains the available cards.
     */
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

        ArrayList<CardEnum> allCards = new ArrayList<>(event.getAvailableCards());
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
        //enable SuperUserVBox
        superUserButton.setDisable(false);
    }

    /**
     * Receive the event CV_ChallengerChooseFirstPlayerRequestEvent and handle the choice of the first player.
     *
     * @param event the name of the players and the cards that each one has chosen.
     */
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

    /**
     * Receive the event CV_ChallengerChosenEvent and handle the choice of the cards.
     *
     * @param event Contains some data like the size of the room.
     */
    public void update(CV_ChallengerChosenEvent event) {
        STATE = "CHALLENGER_CARDS_CHOICE";
        cardToChoose = event.getRoomSize();
        showXRightHBoxes(cardToChoose);
        challengerSelectedCards = new ArrayList<>();
        setTitleCenter("YOU ARE THE CHALLENGER!! \tCHOOSE " + event.getRoomSize() + " CARDS");

        fillAllChallengerCards();
        showAllChallengerCards();
    }

    /* ----------------------------------------------------------------------------------------------
                                          CHALLENGER CARDS CHOICE
       ----------------------------------------------------------------------------------------------*/

    /**
     * Set Visible all the Challenger Cards
     */
    private void showAllChallengerCards() {
        setVisibleOnlyThisNode(challengerScrollPane);
    }

    /**
     * Fill all the card to show them to the challenger
     */
    private void fillAllChallengerCards() {
        challengerCardMapByHBox = new HashMap<>();
        challengerHBoxMapByCard = new HashMap<>();
        ObservableList<Node> children = challengerCardsFlowPane.getChildren();

        int actualNode = 0;
        for (CardEnum card : CardEnum.values()) {
            if (card != CardEnum.SUPERUSER) {
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
        }
        if (actualNode < MAX_CARDS_NUMBERS) {
            hideNextCardsHBoxes(actualNode);
            deleteNextCardsHBoxes(actualNode);
        }
    }

    /**
     * Hide remaining Challenger cards from the actualHBox param
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

    /**
     * Delete remaining Challenger cards from the actualHBox param
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
     * Handle the click on a Challenger Card and select the God.
     *
     * @param mouseEvent MouseEvent with the source of the event.
     */
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
    }

    /**
     * Enable the click on the card.
     *
     * @param card Card to enable
     */
    private void enableChallengerCard(CardEnum card) {
        HBox hBox = challengerHBoxMapByCard.get(card);
        hBox.setDisable(false);

        StackPane parentPane = (StackPane) hBox.getParent();
        Pane greyPane = (Pane) parentPane.getChildren().get(2);
        greyPane.setVisible(false);
    }

    /**
     * Disable the click on the card.
     *
     * @param card Card to enable
     */
    private void disableChallengerCard(CardEnum card) {
        HBox hBox = challengerHBoxMapByCard.get(card);
        hBox.setDisable(true);

        StackPane parentPane = (StackPane) hBox.getParent();
        Pane greyPane = (Pane) parentPane.getChildren().get(2);
        greyPane.setVisible(true);
    }

    /**
     * Enable the Green Pane behind the selected card.
     *
     * @param card card clicked
     */
    private void enableChallengerCardGreen(CardEnum card) {
        HBox hBox = challengerHBoxMapByCard.get(card);

        StackPane parentPane = (StackPane) hBox.getParent();
        Pane greenPane = (Pane) parentPane.getChildren().get(0);
        greenPane.setVisible(true);
    }

    /**
     * Disable the Green Pane behind the selected card.
     *
     * @param card card clicked
     */
    private void disableChallengerCardGreen(CardEnum card) {
        HBox hBox = challengerHBoxMapByCard.get(card);

        StackPane parentPane = (StackPane) hBox.getParent();
        Pane greenPane = (Pane) parentPane.getChildren().get(0);
        greenPane.setVisible(false);
    }

    /**
     * Disable the click on all the not selected cards.
     */
    private void disableNotSelectedChallengerCards() {
        for (CardEnum card : CardEnum.values()) {
            if (!challengerSelectedCards.contains(card) && card != CardEnum.SUPERUSER) {
                disableChallengerCard(card);
            }
        }
    }

    /**
     * Enable the click on all the not selected cards.
     */
    private void enableNotSelectedChallengerCards() {
        for (CardEnum card : CardEnum.values()) {
            if (!challengerSelectedCards.contains(card) && card != CardEnum.SUPERUSER) {
                enableChallengerCard(card);
            }
        }
    }

    /**
     * Send the event with the Cards selected by the Challenger
     */
    private void sendChallengerCardsChosenEvent() {
        if (cardToChoose == challengerSelectedCards.size()) {
            VC_ChallengerCardsChosenEvent event = new VC_ChallengerCardsChosenEvent("", challengerSelectedCards);
            gui.getClient().sendEvent(event);
            disableConfirmButton();
            challengerSelectedCards = new ArrayList<>();
        } else {
            System.out.println("Error");
        }
    }

    /* ----------------------------------------------------------------------------------------------
                                          PLAYER CARDS CHOICE
       ----------------------------------------------------------------------------------------------*/

    /**
     * Fill a single Player Card with all the data.
     *
     * @param vBox      VBox to fill
     * @param card      Card to add and source of data
     * @param available Boolean value to enable or disable the card
     */
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
     * Handle the click on a Player Card and select the God.
     *
     * @param mouseEvent MouseEvent with the source of the event.
     */
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
    }

    /**
     * Enable the click on the card.
     *
     * @param card Card to enable
     */
    private void enablePlayerCard(CardEnum card) {
        VBox vBox = playerVBoxMapByCard.get(card);
        vBox.setDisable(false);

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane greyPane = (Pane) parentPane.getChildren().get(2);
        greyPane.setVisible(false);
    }

    /**
     * Disable the click on the card.
     *
     * @param card Card to enable
     */
    private void disablePlayerCard(CardEnum card) {
        VBox vBox = playerVBoxMapByCard.get(card);
        vBox.setDisable(true);

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane greyPane = (Pane) parentPane.getChildren().get(2);
        greyPane.setVisible(true);
    }

    /**
     * Enable the Green Pane behind the selected card.
     *
     * @param card card clicked
     */
    private void enablePlayerCardGreen(CardEnum card) {
        VBox vBox = playerVBoxMapByCard.get(card);

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane greenPane = (Pane) parentPane.getChildren().get(0);
        greenPane.setVisible(true);
    }

    /**
     * Disable the Green Pane behind the selected card.
     *
     * @param card card clicked
     */
    private void disablePlayerCardGreen(CardEnum card) {
        VBox vBox = playerVBoxMapByCard.get(card);

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane greenPane = (Pane) parentPane.getChildren().get(0);
        greenPane.setVisible(false);
    }

    /**
     * Disable the click on all the not selected cards.
     */
    private void disableNotSelectedPlayerCards() {
        for (CardEnum card : playerAvailableCards) {
            if (!playerSelectedCard.equals(card)) {
                disablePlayerCard(card);
            }
        }
    }

    /**
     * Enable the click on all the not selected cards.
     */
    private void enableNotSelectedPlayerCards() {
        for (CardEnum card : playerAvailableCards) {
            if (!playerSelectedCard.equals(card)) {
                enablePlayerCard(card);
            }
        }
    }

    /**
     * Send the event with the Card selected by the User
     */
    private void sendPlayerCardsChosenEvent() {
        if (playerSelectedCard != null) {
            VC_PlayerCardChosenEvent event = new VC_PlayerCardChosenEvent(gui.getUsername(), playerSelectedCard);
            gui.getClient().sendEvent(event);
            disableConfirmButton();
            playerSelectedCard = null;
        } else {
            System.out.println("Error");
        }

    }

    public void onClickSuperUserButton(MouseEvent mouseEvent) {
        CardEnum card = CardEnum.SUPERUSER;

        if (playerSelectedCard != null && playerSelectedCard.equals(card)) {
            //Already selected card is selected a second time
            cleanRightHBox(card);
            enableNotSelectedPlayerCards();
            playerSelectedCard = null;
            disableConfirmButton();
        } else {
            playerSelectedCard = card;
            fillFirstFreeHBox(card);
            disableNotSelectedPlayerCards();
            enableConfirmButton();
        }

        System.out.println("Card clicked: " + card);
    }

    /* ----------------------------------------------------------------------------------------------
                                          FIRST PLAYER CHOICE
       ----------------------------------------------------------------------------------------------*/

    /**
     * Fill the VBox with name and card Image during the First Player Selection
     *
     * @param vBox     VBox to fill
     * @param card     Card chosen by the user
     * @param username Name of the user
     */
    private void fillFirstPlayerChoice(VBox vBox, CardEnum card, String username) {
        firstPlayerUsernameByVBox.put(vBox, username.toLowerCase());
        firstPlayerVBoxByUsername.put(username.toLowerCase(), vBox);
        firstPlayerCardByName.put(username.toLowerCase(), card);

        ImageView image = (ImageView) vBox.getChildren().get(0);
        image.setImage(new Image(card.getImgUrl()));

        VBox internalVBox = (VBox) vBox.getChildren().get(1);
        Text playerName = (Text) internalVBox.getChildren().get(0);
        playerName.setText(username.toUpperCase());

        vBox.setVisible(true);
    }

    /**
     * Handle the click on the Name of the First Player and select this player as First Player.
     *
     * @param mouseEvent MouseEvent with the source of the event.
     */
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
    }

    /**
     * Enable the click on the Player
     *
     * @param username Username of the player
     */
    private void enableFistsPlayer(String username) {
        VBox vBox = firstPlayerVBoxByUsername.get(username.toLowerCase());
        vBox.setDisable(false);

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane greyPane = (Pane) parentPane.getChildren().get(2);
        greyPane.setVisible(false);
    }

    /**
     * Disable the click on the Player
     *
     * @param username Username of the player
     */
    private void disableFirstPlayer(String username) {
        VBox vBox = firstPlayerVBoxByUsername.get(username.toLowerCase());
        vBox.setDisable(true);

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane greyPane = (Pane) parentPane.getChildren().get(2);
        greyPane.setVisible(true);
    }

    /**
     * Enable the Green Pane behind the selected player.
     *
     * @param username Username of the player.
     */
    private void enableFirstPlayerGreen(String username) {
        VBox vBox = firstPlayerVBoxByUsername.get(username.toLowerCase());

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane greenPane = (Pane) parentPane.getChildren().get(0);
        greenPane.setVisible(true);
    }

    /**
     * Disable the Green Pane behind the selected player.
     *
     * @param username Username of the player.
     */
    private void disableFirstPlayerGreen(String username) {
        VBox vBox = firstPlayerVBoxByUsername.get(username.toLowerCase());

        StackPane parentPane = (StackPane) vBox.getParent();
        Pane greenPane = (Pane) parentPane.getChildren().get(0);
        greenPane.setVisible(false);
    }

    /**
     * Disable the click on all the not selected players.
     */
    private void disableNotSelectedFirstPlayer() {
        for (String username : usernamesAvailable) {
            if (!firstPlayerSelected.equalsIgnoreCase(username)) {
                disableFirstPlayer(username);
            }
        }
    }

    /**
     * Enable the click on all the not selected players.
     */
    private void enableNotSelectedFirstPlayer() {
        for (String username : usernamesAvailable) {
            if (!firstPlayerSelected.equalsIgnoreCase(username)) {
                enableFistsPlayer(username);
            }
        }
    }

    /**
     * Send the event with the First Player selected by the Challenger
     */
    private void sendFirstPlayerChosenEvent() {
        if (firstPlayerSelected != null) {
            VC_ChallengerChosenFirstPlayerEvent event = new VC_ChallengerChosenFirstPlayerEvent(firstPlayerSelected);
            gui.getClient().sendEvent(event);
            disableConfirmButton();
            firstPlayerSelected = null;
        } else {
            System.out.println("Error");
        }

    }

    /* ----------------------------------------------------------------------------------------------
                                          PREVIEW HBOXES ON THE RIGHT
       ----------------------------------------------------------------------------------------------*/

    /**
     * Enable and show the HBoxes on the right section.
     *
     * @param numberOfHBoxesToEnable Number of HBoxes to show
     */
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
            numberActualEnableHBoxes = numberOfHBoxesToEnable;

            rightHBoxesMapByCard = new HashMap<>();
            rightHBoxesMapByName = new HashMap<>();
            firstPlayerCardByName = new HashMap<>();
        } else {
            System.out.println("Not enough HBoxes");
        }
    }

    /**
     * Show the HBox but hide the content.
     *
     * @param hBox HBox to show and clean.
     */
    private void showCleanRightHBox(HBox hBox) {
        hBox.setVisible(true);
        hBox.getChildren().get(0).setVisible(false);
        hBox.getChildren().get(1).setVisible(false);
        VBox vBox = (VBox) hBox.getChildren().get(1);
        vBox.getChildren().get(0).setVisible(false);
        vBox.getChildren().get(1).setVisible(false);
    }

    /**
     * Hide the HBox and hide the content.
     *
     * @param hBox HBox to hide and clean
     */
    private void hideRightHBox(HBox hBox) {
        showCleanRightHBox(hBox);
        hBox.setVisible(false);
    }

    /**
     * Fill with Card Info the first free HBox. It uses the {@code indexFirstFreeHBox} value to find the first free.
     *
     * @param card Card to fill
     */
    private void fillFirstFreeHBox(CardEnum card) {
        if (indexFirstFreeHBox < numberActualEnableHBoxes) {
            fillFirstFreeHBox(card, card.getName(), card.getDescription());
        }
    }

    /**
     * Fill with Name Info the first free HBox. It uses the {@code indexFirstFreeHBox} value to find the first free.
     *
     * @param name Username to fill
     */
    private void fillFirstFreeHBox(String name) {
        CardEnum card = firstPlayerCardByName.get(name.toLowerCase());
        if (indexFirstFreeHBox < numberActualEnableHBoxes) {
            fillFirstFreeHBox(card, name, "");
        }
    }

    /**
     * Fill with selected first player info. It uses the {@code indexFirstFreeHBox} value to find the first free.
     *
     * @param card      Card to get the Image
     * @param bigText   Big text, usually is the Player's username or the Card Name
     * @param smallText If set it's use like card description, if is void ("") it's not used
     */
    private void fillFirstFreeHBox(CardEnum card, String bigText, String smallText) {
        if (indexFirstFreeHBox < numberActualEnableHBoxes) {
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

    /**
     * Search the first Free HBox and set the value of the index.
     */
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
        //if not found, there's no free HBox
        indexFirstFreeHBox = 3;
    }

    /**
     * Clean the right HBox using a CardEnum to find the HBox
     *
     * @param card Card to clean
     */
    private void cleanRightHBox(CardEnum card) {
        HBox hBox = rightHBoxesMapByCard.get(card);
        showCleanRightHBox(hBox);
        rightHBoxesMapByCard.remove(card);
        resetIndexFreeHBox();
    }

    /**
     * Clean the right HBox using the username to find the HBox
     *
     * @param name Username of the player
     */
    private void cleanRightHBox(String name) {
        HBox hBox = rightHBoxesMapByName.get(name.toLowerCase());
        showCleanRightHBox(hBox);
        rightHBoxesMapByName.remove(name.toLowerCase());
        resetIndexFreeHBox();
    }

    /* ----------------------------------------------------------------------------------------------
                                          CONFIRM BUTTON
       ----------------------------------------------------------------------------------------------*/

    /**
     * Handle the click on the confirm Button. The action depends on the actual state of the Class.
     */
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

    /**
     * Enable the click on the confirm Button
     */
    private void enableConfirmButton() {
        confirmButton.setDisable(false);
        StackPane parentPane = (StackPane) confirmButton.getParent();
        Pane greyPane = (Pane) parentPane.getChildren().get(3);
        greyPane.setVisible(false);
    }

    /**
     * Disable the click on the confirm Button
     */
    private void disableConfirmButton() {
        confirmButton.setDisable(true);
        StackPane parentPane = (StackPane) confirmButton.getParent();
        Pane greyPane = (Pane) parentPane.getChildren().get(3);
        greyPane.setVisible(true);
    }
}
