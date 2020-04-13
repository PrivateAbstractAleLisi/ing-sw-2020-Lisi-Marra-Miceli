package view;

import auxiliary.ANSIColors;
import auxiliary.Range;
import event.core.EventListener;
import event.core.EventSource;
import event.events.*;
import event.events.prematch.ToViewCardChoiceRequestGameEvent;
import event.events.prematch.ToViewWaitGameEvent;
import model.CardEnum;
import placeholders.VirtualServer;
import view.CLI.utility.CardUtility;
import view.CLI.utility.MessageUtility;
import view.CLI.utility.RoomUtility;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static event.core.ListenerType.VIEW;

public class CLIView extends EventSource implements EventListener {

    //IN-OUT DATA FROM CONSOLE
    private PrintStream output;
    private Scanner input;

    public CLIView() {
        this.output = System.out;
        this.input = new Scanner(System.in);
        virtualServer = new VirtualServer();

        //listening to each other
        virtualServer.attachListenerByType(VIEW, this);
        this.attachListenerByType(VIEW, virtualServer);
    }

    VirtualServer virtualServer;

    //MAIN METHODS


    public void start() {

        virtualServer = new VirtualServer();
        virtualServer.attachListenerByType(VIEW, this);
        MessageUtility.displayTitle();
        String userProposal = askUsername();

        //try to connect
        ConnectionRequestGameEvent req = new ConnectionRequestGameEvent("Tentativo di connessione", "--", 0, userProposal);
        this.notifyAllObserverByType(VIEW, req);


    }

    private String askUsername() {

        input = new Scanner(System.in);

        String str = null;
        output.println("Insert a valid username ");
        output.println("[at least 3 alpha numeric characters] ");

        str = input.nextLine();

        while (!checkLocalUsernameAlphaNumeric(str)) {
            MessageUtility.displayErrorMessage("⚠ Invalid username: please at least 3 alpha numeric characters");
            str = input.nextLine();
        }

        return str;


    }

    private int askGameRoomSize() {

        input = new Scanner(System.in);
        Integer sizeIn = null;
        Range validSize = new Range(2, 3);
        System.out.println("You're the first player, please choose a valid room size (2-3):");
        sizeIn = input.nextInt();

        while (!validSize.contains(sizeIn)) {
            System.err.println("Invalid size: please enter 2 or 3");
            sizeIn = input.nextInt();
        }

        MessageUtility.printValidMessage("room size is valid!");
        return sizeIn;


    }


    //USERNAME VALIDATION:
    private void checkUsernameOnSever() {

        //TODO sends an event to the server that the username has been inserted, the server checks if it's valid
    }


    private boolean checkLocalUsernameAlphaNumeric(String username) {
        boolean notValid = (username == null || username.length() < 3 || username.length() > 16);
        if (notValid) {
            return false;
        } else {
            return username.matches("^[a-zA-Z0-9]*$");

        }

    }


    //CHALLENGER

    private List<CardEnum> fromIntToCardEnum(int[] cardUID) {

        List<CardEnum> cardListEnum = new ArrayList<>();
        for (int i = 0; i < cardUID.length; i++) {

            cardListEnum.add(CardEnum.getValueFromInt(cardUID[i]));


        }

        return cardListEnum;


    }

    private boolean checkCardIndexValid(int i) {
        boolean ok = false;
        for (CardEnum card : CardEnum.values()) {
            if (card.getNumber() == i) {
                ok = true;
                return true;
            }

        }
        return false;

    }

    private boolean areInsertedCardsValid(int[] cards) {

        if (cards == null) {
            return false;
        }
        for (int i = 0; i < cards.length; i++) {
            for (int j = 0; j < i; j++) {
                if (cards[i] == cards[j]) {
                    return false;
                }
            }
            if (!checkCardIndexValid(cards[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * reads 2 or 3 cards IDs from the input as integers
     * @param roomSize how many cards you want to read from the challenger
     * @return a list of enum representing chosen cards
     */
    private List<CardEnum> challengerPickCards(int roomSize) {

        output.println("\nPlease insert " + roomSize + " cards numbers");
        output.println("(one number each line)");

        int[] choosenCardsIndex = null;
        choosenCardsIndex = new int[roomSize];

        //read 3 numbers
        for (int i = 0; i < roomSize; i++) {
            Integer readCard = input.nextInt();
            choosenCardsIndex[i] = readCard;
        }

        while (!areInsertedCardsValid(choosenCardsIndex)) {
            MessageUtility.displayErrorMessage("the cards you inserted are not valid");
            output.println("please insert " + roomSize + " cards numbers");
            output.println("(one number each line)");
            choosenCardsIndex = new int[roomSize];

            //read 3 numbers
            for (int i = 0; i < roomSize; i++) {
                Integer readCard = input.nextInt();
                choosenCardsIndex[i] = readCard;
            }

        }

        MessageUtility.printValidMessage("cards are valid, thank you Challenger. ");
        output.println("Now wait for the other players to choose their cards...");

        return fromIntToCardEnum(choosenCardsIndex);

        //check if they are all different


    }


    //EVENT HANDLING

    @Override
    public void handleEvent(GameEvent e) {
        System.err.println("generic <handleEvent> has been called ");
    }

    @Override
    /**
     * room created, it clears and display an updated current room player list.
     */
    public void handleEvent(RoomUpdateGameEvent event) {

        clearScreen();
        System.out.println("\nROOM CREATED: ");
        String[] playersIn = event.getUsersInRoom();
        RoomUtility.printPlayersInRoom(playersIn, event.getRoomSize());

    }


    @Override
    /**
     * connection has been rejected for username already taken of room is full. It may ask to insert a new username
     */
    public void handleEvent(ConnectionRejectedErrorGameEvent event) {

        switch (event.getErrorCode()) {
            case "USER_TAKEN":
                //notify the error on screen
                MessageUtility.displayErrorMessage(event.getErrorMessage());
                break;

            case "ROOM_FULL":
                MessageUtility.displayErrorMessage(event.getErrorMessage());

                return;
        }

        //read a new username if it's already taken
        String userProposal = askUsername();
        //send the new connection request

        ConnectionRequestGameEvent req;
        req = new ConnectionRequestGameEvent("Tentativo di connessione", "--", 0, userProposal);
        this.notifyAllObserverByType(VIEW, req);


    }

    @Override
    public void handleEvent(ChallengerCardsChosenEvent event) {

    }

    @Override
    public void handleEvent(PlayerCardChosenEvent event) {

    }

    @Override
    /*
    this user is the challenger, it has to choose the 3 cards
     */
    public void handleEvent(ChallengerChosenEvent event) {
        MessageUtility.printValidMessage("You're the challenger!");
        output.println("Choose 3 cards for this match:");
        CardUtility.displayAllCards();
        List<CardEnum> gameCards = challengerPickCards(event.getRoomSize());
    }

    //CARD CHOICE
    @Override
    public void handleEvent(ToViewCardChoiceRequestGameEvent event) {

    }

    @Override
    public void handleEvent(ToViewWaitGameEvent event) {
        clearScreen();
        System.out.println("⌛︎ WAITING ︎⌛︎");
        System.out.println(event.getEventDescription());
    }

    @Override
    /**
     * you're the first player so you have to insert a room size to create a room.
     */
    public void handleEvent(RoomSizeRequestGameEvent event) {
        int size = askGameRoomSize();
        RoomSizeResponseGameEvent response;
        response = new RoomSizeResponseGameEvent("first player sends the chosen size", size);
        notifyAllObserverByType(VIEW, response);
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }


    /*                              /*

                NOT IMPLEMENTED
     */                             //


    @Override //NO IMPL
    public void handleEvent(ConnectionRequestGameEvent event) {
        return;
    }

    @Override //NO IMPL
    public void handleEvent(ConnectionRequestServerGameEvent event) {
        return;
    }

    @Override

    public void handleEvent(ChallengerChosenFirstPlayerEvent event) { return;

    }


    @Override //NO IMPL
    public void handleEvent(RoomSizeResponseGameEvent event) {
        return;
    }

}
