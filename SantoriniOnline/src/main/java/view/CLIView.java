package view;

import auxiliary.Range;
import com.google.gson.Gson;
import event.core.EventListener;
import event.core.EventSource;
import event.gameEvents.GameEvent;
import event.gameEvents.lobby.*;
import event.gameEvents.match.CV_GameStartedGameEvent;
import event.gameEvents.prematch.*;
import model.CardEnum;
import networking.client.SantoriniClient;
import placeholders.IslandData;
import view.CLI.utility.CardUtility;
import view.CLI.utility.IslandUtility;
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
    private SantoriniClient client;

    public CLIView(SantoriniClient client) {
        this.output = System.out;
        this.input = new Scanner(System.in);
        this.client = client;
        //listening to each other
        client.attachListenerByType(VIEW, this);
    }


    //MAIN METHODS


    public void start() {

        clearScreen();
        //MessageUtility.displayTitle();
        String userProposal = askUsername();

        //try to connect
        VC_ConnectionRequestGameEvent req = new VC_ConnectionRequestGameEvent("connection attempt", "--", 0, userProposal);
        this.client.sendEvent(req);
        new Thread(client).start();
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
     *
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

    //CARD CHOICE

    private boolean isSelectedCardValid(int cardID, List<CardEnum> available) {
        boolean ok = false;
        for (CardEnum card : available) {
            if (card.getNumber() == cardID) {
                ok = true;
                return true;
            }

        }
        return false;
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
    public void handleEvent(CV_RoomUpdateGameEvent event) {

        clearScreen();
        System.out.println("\nROOM CREATED: ");
        String[] playersIn = event.getUsersInRoom();
        RoomUtility.printPlayersInRoom(playersIn, event.getRoomSize());
    }


    @Override
    /**
     * connection has been rejected for username already taken of room is full. It may ask to insert a new username
     */
    public void handleEvent(CV_ConnectionRejectedErrorGameEvent event) {
        String userProposal = "";
        switch (event.getErrorCode()) {
            case "USER_TAKEN":
                //notify the error on screen
                MessageUtility.displayErrorMessage(event.getErrorMessage());

                //read a new username if it's already taken
                userProposal = askUsername();

                break;
            case "WAIT_FOR_CREATION":
                MessageUtility.displayErrorMessage(event.getErrorMessage());
                try {
                    Thread.sleep(5000);
                    userProposal = event.getWrongUsername();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case "ROOM_FULL":
                MessageUtility.displayErrorMessage(event.getErrorMessage());
                return;
        }

        //Force to select an username
        if (userProposal.equals("")) {
            //send the new connection request
            userProposal = askUsername();
        }

        VC_ConnectionRequestGameEvent req;
        req = new VC_ConnectionRequestGameEvent("Tentativo di connessione", "--", 0, userProposal);
        this.client.sendEvent(req);
    }

    @Override
    /*
    this user is the challenger, it has to choose the 3 cards
     */
    public void handleEvent(CV_ChallengerChosenEvent event) {
        System.out.println("⌛︎ WAITING   ⌛︎");
        System.out.println("The game is choosing the challenger\n");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        clearScreen();
        MessageUtility.printValidMessage("You're the challenger!");
        output.println("Choose 3 cards for this match:");
        CardUtility.displayAllCards();
        List<CardEnum> gameCards = challengerPickCards(event.getRoomSize());
        VC_ChallengerCardsChosenEvent response = new VC_ChallengerCardsChosenEvent("", gameCards);
        client.sendEvent(response);
    }

    //CARD CHOICE

    @Override
    /*
    this user has to choose a card from the remaining list
     */
    public void handleEvent(CV_CardChoiceRequestGameEvent event) {

        clearScreen();

        System.out.println("It's time to choose the card to play with");
        CardUtility.displayAllAvailableCards(event.getAvailableCards());
        System.out.println("\nPlease insert a card number");

        Integer id;

        //read from the console
        input = new Scanner(System.in);
        id = input.nextInt();

        while (!isSelectedCardValid(id, event.getAvailableCards())) {
            MessageUtility.displayErrorMessage(id + " is not a valid card number");
            id = input.nextInt();
        }

        CardEnum selected = CardEnum.getValueFromInt(id);
        MessageUtility.printValidMessage("you made your choice");

        //notify the server
        VC_PlayerCardChosenEvent choice = new VC_PlayerCardChosenEvent(event.getUsername(), selected);
        this.client.sendEvent(choice);
    }

    @Override
    /*
    waiting both because of the challenger or because of other players choice is made
     */
    public void handleEvent(CV_WaitGameEvent event) {
        clearScreen();
        System.out.println("⌛︎ WAITING ︎⌛︎");
        System.out.println(event.getActingPlayer().toUpperCase() + " " + event.getEventDescription());
    }

    @Override
    /*
    this user is the challenger, he has to choose the first player
     */
    public void handleEvent(CV_ChallengerChooseFirstPlayerRequestEvent event) {
        System.out.println("Since you are the Challenger, please choose the player who starts first ");
        List<String> players = event.getPlayers();

        for (String player : players) {
            System.out.println("- " + player);
        }
        System.out.println("\nPlease choose a player name from this list:");

        input = new Scanner(System.in);

        String choice = input.nextLine();

        while (!players.contains(choice)) {
            clearScreen();
            MessageUtility.displayErrorMessage("Invalid username");

            System.out.println("Since you are the Challenger, please choose the player who starts first ");
            players = event.getPlayers();

            for (String player : players) {
                System.out.println("- " + player);
            }
            System.out.println("\nPlease choose a player name from this list:");

            input = new Scanner(System.in);

            choice = input.nextLine();
        }

        MessageUtility.printValidMessage("You chose " + choice + " as the first player.");

        VC_ChallengerChosenFirstPlayerEvent choiceEvent = new VC_ChallengerChosenFirstPlayerEvent(choice);

        client.sendEvent(choiceEvent);
    }

    @Override
    public void handleEvent(VC_PlayerPlacedWorkerEvent event) {
        //not implemente
    }

    public boolean checkCellInput(int x, int y) {

        Range oneToFive = new Range(1, 5);
        return oneToFive.contains(x) && oneToFive.contains(y);
    }

    @Override
    public void handleEvent(CV_PlayerPlaceWorkerRequestEvent event) {

        //display island
        Gson gson = new Gson();
        final IslandData isla = (IslandData) gson.fromJson(event.getIsland(), IslandData.class);

        IslandUtility temp = new IslandUtility(isla);

        temp.displayIsland();

        String workerFirstOrSecond = null;
        switch (event.getWorkerToPlace()) {

            case A:
                workerFirstOrSecond = "first";
                break;
            case B:
                workerFirstOrSecond = "second";
                break;
        }

        input = new Scanner(System.in);

        int x, y;
        System.out.println("It's your turn to place your " + workerFirstOrSecond + " worker");
        System.out.print(" please enter a row :");
        x = input.nextInt();
        System.out.print(" and a column :");
        y = input.nextInt();

        while (!checkCellInput(x, y)) {
            MessageUtility.displayErrorMessage("invalid row or column");
            System.out.print("please enter a row :");
            x = input.nextInt();
            System.out.print(" and a column :");
            y = input.nextInt();

        }

        //subtracting  1 'cause 1...5 --> 0...4 on model, controller
        VC_PlayerPlacedWorkerEvent response =
                new VC_PlayerPlacedWorkerEvent("sending an x, y proposal ",
                        event.getActingPlayer(),
                        x - 1,
                        y - 1,
                        event.getWorkerToPlace());

        client.sendEvent(response);

    }

    @Override
    /**
     * you're the first player so you have to insert a room size to create a room.
     */
    public void handleEvent(CV_RoomSizeRequestGameEvent event) {
        int size = askGameRoomSize();
        VC_RoomSizeResponseGameEvent response;
        response = new VC_RoomSizeResponseGameEvent("first player sends the chosen size", size);
        client.sendEvent(response);
    }

    @Override
    public void handleEvent(CV_GameStartedGameEvent event) {
        clearScreen();
        //TODO BIG TITLE
        System.out.println("GAME IS STARTING");
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }


    /*                              /*

                NOT IMPLEMENTED
     */                             //

    @Override //NO IMPL
    public void handleEvent(VC_ConnectionRequestGameEvent event) {
        return;
    }

    @Override //NO IMPL
    public void handleEvent(CC_ConnectionRequestGameEvent event) {
        return;
    }

    @Override

    public void handleEvent(VC_ChallengerChosenFirstPlayerEvent event) {
        return;

    }


    @Override //NO IMPL
    public void handleEvent(VC_RoomSizeResponseGameEvent event) {
        return;
    }

    @Override
    public void handleEvent(VC_ChallengerCardsChosenEvent event) {

    }

    @Override
    public void handleEvent(VC_PlayerCardChosenEvent event) {

    }

}
