package view;

import auxiliary.ANSIColors;
import auxiliary.Range;
import event.core.EventListener;
import event.core.EventSource;
import event.events.*;
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
    private final PrintStream output;
    private final Scanner input;

    public CLIView() {
        this.output = System.out;
        this.input = new Scanner(System.in);
        virtualServer.attachListenerByType(VIEW, this);
    }

    VirtualServer virtualServer;

    //MAIN METHODS


    public void start() {
        virtualServer = new VirtualServer();
        virtualServer.attachListenerByType(VIEW, this);
        MessageUtility.displayTitle();
        String userProposal = askUsername();
        this.attachListenerByType(VIEW, virtualServer);

        ConnectionRequestGameEvent req = new ConnectionRequestGameEvent("Tentativo di connessione", "--", 0, userProposal);
        this.notifyAllObserverByType(VIEW, req);
        //ask IP

    }

    private String askUsername() {

        String str = "null";
        output.println("Insert a valid username ");
        output.println("[at least 3 alpha numeric characters] ");
        str = input.nextLine();

        while (!checkLocalUsernameAlphaNumeric(str)) {
            displayUsernameErrorMessage("⚠ Invalid username: please at least 3 alpha numeric characters");
            str = input.nextLine();
        }

        return str;


    }

    private int askGameRoomSize() {
        Integer sizeIn = null;
        Range validSize = new Range(2, 3);
        System.out.println("You're the first player, please insert the room size:");
        sizeIn = input.nextInt();

        while (!validSize.contains(sizeIn)) {
            System.err.println("Invalid size: please enter 2 or 3");
            sizeIn = input.nextInt();
        }

        printValidMessage("Room size");
        return sizeIn;


    }

    /**
     * prints (message) is valid in green
     *
     * @param message
     */
    private void printValidMessage(String message) {
        output.println(ANSIColors.ANSI_GREEN + "✓ " + message + " is valid " + ANSIColors.ANSI_RESET);
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
                break;
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

    private List<CardEnum> challengerPickCards(int roomSize) {

        output.println("please insert " + roomSize + " cards numbers");
        output.println("(one number each line)");

        int[] choosenCardsIndex = null;
        choosenCardsIndex = new int[roomSize];

        //read 3 numbers
        for (int i = 0; i < roomSize; i++) {
            Integer readCard = input.nextInt();
            choosenCardsIndex[i] = readCard;
        }

        while (areInsertedCardsValid(choosenCardsIndex)) {
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

        printValidMessage("cards are valid");

        return fromIntToCardEnum(choosenCardsIndex);

        //check if they are all different


    }

    //DISPLAY UTILITIES

    //Called both by invalid username already taken (via server) or invalid username not alphanumeric
    private void displayUsernameErrorMessage(String errorMessage) {
        System.err.print("\n Invalid Username: ");
        System.err.println(errorMessage);
    }


    //EVENT HANDLING

    @Override
    public void handleEvent(GameEvent e) {
        System.err.println("generic <handleEvent> has been called ");
    }

    @Override //NO IMPL
    public void handleEvent(RoomSizeResponseGameEvent event) {
        return;
    }

    @Override
    /**
     * room created, it clears and display an updated current room player list.
     */
    public void handleEvent(RoomUpdateGameEvent event) {

        clearScreen();
        System.out.println("ROOM CREATED: ");
        String[] playersIn = event.getUsersInRoom();
        RoomUtility.printPlayersInRoom(playersIn, event.getRoomSize());

    }

    @Override //NO IMPL
    public void handleEvent(ConnectionRequestGameEvent event) {
        return;
    }

    @Override //NO IMPL
    public void handleEvent(ConnectionRequestServerGameEvent event) {
        return;
    }


    @Override
    /**
     * connection has been rejected for username already taken of room is full. It may ask to insert a new username
     */
    public void handleEvent(ConnectionRejectedErrorGameEvent event) {

        switch (event.getErrorCode()) {
            case "USER_TAKEN":
                //notify the error on screen
                System.err.print("Username " + event.getWrongUsername());
                displayUsernameErrorMessage(event.getErrorMessage());
                displayUsernameErrorMessage(event.getEventDescription());
                askUsername();
                break;

            case "ROOM_FULL":
                displayUsernameErrorMessage("Server room is currently full");
                return;
        }


        //read a new username if it's already taken
        String userProposal = askUsername();


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

    public void handleEvent(ChallengerChosenFirstPlayerEvent event) {

    }

    @Override
    /*
    this user is the challenger, it has to choose the 3 cards
     */
    public void handleEvent(ChallengerChosenEvent event) {
        output.println("You're the challenger!");
        output.println("Choose 3 cards for this match:");
        CardUtility.displayAllCards();
        List<CardEnum> gameCards = challengerPickCards(event.getRoomSize());
    }

    @Override
    /**
     * you're the first player so you have to insert a room size to create a room.
     */
    public void handleEvent(RoomSizeRequestGameEvent event) {
        int size = askGameRoomSize();
        RoomSizeResponseGameEvent response;
        response = new RoomSizeResponseGameEvent("first player sends the chosen size", size);
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
