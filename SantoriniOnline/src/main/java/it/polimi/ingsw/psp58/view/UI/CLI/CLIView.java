package it.polimi.ingsw.psp58.view.UI.CLI;

import com.google.gson.Gson;
import it.polimi.ingsw.psp58.auxiliary.ANSIColors;
import it.polimi.ingsw.psp58.auxiliary.IslandData;
import it.polimi.ingsw.psp58.auxiliary.Range;
import it.polimi.ingsw.psp58.event.core.EventSource;
import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.connection.PlayerDisconnectedViewEvent;
import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_GameStartedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_PreGameStartedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_WorkerPlacementGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.*;
import it.polimi.ingsw.psp58.event.gameEvents.match.*;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.*;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.TurnAction;
import it.polimi.ingsw.psp58.model.WorkerColors;
import it.polimi.ingsw.psp58.networking.client.SantoriniClient;
import it.polimi.ingsw.psp58.view.UI.CLI.utility.*;

import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static it.polimi.ingsw.psp58.auxiliary.ANSIColors.*;

public class CLIView extends EventSource implements ViewListener {

    //IN-OUT DATA FROM CONSOLE
    private PrintStream output;
    private Scanner input;
    private SantoriniClient client;
    private String myUsername;
    private CardEnum myCard;
    private WorkerColors myColor;
    private String[] args;
    private boolean enablePing=true;
    private Map<String, CardEnum> opponentsCard;
    private Map<String, WorkerColors> playersColor;

    public CLIView(String[] args) {
        this.output = System.out;
        this.input = new Scanner(System.in);
        this.args = args.clone();
        //listening to each other
    }


    //MAIN METHODS


    public void start() {

        if (args != null) {
            for (String currentArgument : args) {
                switch (currentArgument) {
                    case "-ping off":
                        enablePing = false;
                        break;
                }
            }
        }

        MessageUtility.bigTitle();

        try {
            TimeUnit.SECONDS.sleep(1);
            MessageUtility.online();
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(ANSIColors.YELLOW_UNDERLINED + "Press Enter to Start:" + ANSIColors.ANSI_RESET + " ");
        input.nextLine();

        //System.out.println("WELCOME, Client Started.");
        CLIView.clearScreen();

        String IP = askIPAddress();

        //set up the client
        client = new SantoriniClient(this, IP, enablePing);
        client.begin();

        System.out.println("CLIENT: connected ");

        clearScreen();
        //MessageUtility.displayTitle();
        String userProposal = askUsername();

        //use last userProposal as my username
        myUsername = userProposal;

        //try to connect
        VC_ConnectionRequestGameEvent req = new VC_ConnectionRequestGameEvent("connection attempt", "--", 0, userProposal);
        this.client.sendEvent(req);
        new Thread(client).start();
    }

    private String askIPAddress() {
        System.out.println("Insert server IP Address (press ENTER for localhost): ");
        input.reset();
        String IP = input.nextLine();
        if (IP.equals("")) {
            IP = "127.0.0.1";
        }
        while (!checkValidIP(IP)) {
            MessageUtility.displayErrorMessage("⚠ Invalid IP: please insert a valid one!");
            IP = input.nextLine();
        }
        return IP;
    }

    private String askUsername() {

        input = new Scanner(System.in);
        input.reset();

        String str = null;
        output.println("Insert a valid username ");
        output.println("[at least 3 alpha numeric characters] ");

        str = input.nextLine();

        while (!checkLocalUsernameAlphaNumeric(str)) {
            MessageUtility.displayErrorMessage("⚠ Invalid username: please at least 3 alpha numeric characters");
            str = input.nextLine();
        }

        return str.toLowerCase();


    }

    private int askGameRoomSize() {

        System.out.print("You're the first player: ");
        int sizeIn = readRoomSize();

        while (sizeIn < 0) {
            sizeIn = readRoomSize();
        }

        return sizeIn;
    }

    private int askIntToUser() {

        boolean continueInput = true;
        int re = -1;
        do {
            input = new Scanner(System.in);
            input.reset();
            try {
                re = input.nextInt();
                continueInput = false;

            } catch (InputMismatchException ex) {
                MessageUtility.displayErrorMessage("please insert a valid integer");
            }


        } while (continueInput);
        return re;
    }

    private int readRoomSize() {


        boolean continueInput = true;
        do {
            input = new Scanner(System.in);
            input.reset();
            input.reset();
            Integer sizeIn = null;
            Range validSize = new Range(2, 3);
            System.out.println("Please choose a valid room size (2-3):");
            try {
                sizeIn = input.nextInt();
                while (!validSize.contains(sizeIn)) {
                    System.err.println("Invalid size: please enter 2 or 3");
                    sizeIn = input.nextInt();
                }
                MessageUtility.printValidMessage("room size is valid!");
                continueInput = false;
                return sizeIn;
            } catch (InputMismatchException e) {
                System.err.println("Please insert a valid NUMBER");
            }
        } while (continueInput);

        return -1;
    }


    //USERNAME VALIDATION:
    private void checkUsernameOnSever() {
        //TODO sends an it.polimi.ingsw.sp58.event to the server that the username has been inserted, the server checks if it's valid
    }


    public static boolean checkLocalUsernameAlphaNumeric(String username) {
        boolean notValid = (username == null || username.length() < 3 || username.length() > 16);
        if (notValid) {
            return false;
        } else {
            return username.matches("^[a-zA-Z0-9]*$");
        }
    }

    public static boolean checkValidIP(String IP) {
        boolean notValid = (IP == null || IP.length() < 7 || IP.length() > 15);
        if (notValid) return false;
        else {
            return IP.matches("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
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
            Integer readCard;
            boolean continueReading = true;
            do {
                input = new Scanner(System.in);
                input.reset();
                try {
                    readCard = input.nextInt();
                    choosenCardsIndex[i] = readCard;
                    continueReading = false;
                } catch (InputMismatchException e) {
                    MessageUtility.displayErrorMessage("please insert a valid integer");
                }
            }
            while (continueReading);

        }

        while (!areInsertedCardsValid(choosenCardsIndex)) {
            MessageUtility.displayErrorMessage("the cards you inserted are not valid");
            output.println("please insert " + roomSize + " cards numbers");
            output.println("(one number each line)");
            choosenCardsIndex = new int[roomSize];

            //read 3 numbers
            for (int i = 0; i < roomSize; i++) {
                Integer readCard;
                boolean continueInput = true;
                do {
                    input = new Scanner(System.in);
                    input.reset();
                    try {
                        readCard = input.nextInt();
                        continueInput = false;
                        choosenCardsIndex[i] = readCard;
                    } catch (InputMismatchException ex) {
                        System.out.println("please insert a valid integer");
                    }

                } while (continueInput);
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
    /**
     * room created, it clears and display an updated current room player list.
     */
    public void handleEvent(CV_RoomUpdateGameEvent event) {

        clearScreen();
        System.out.println("\nROOM CREATED: ");
        String[] playersIn = event.getUsersInRoom();
        RoomUtility.printPlayersInRoom(playersIn, event.getRoomSize());
        if (playersIn.length == event.getRoomSize()) {
            System.out.println("The Pre-Match is starting!!");
        } else {
            System.out.println("⌛   WAITING FOR OTHER USERS   ⌛");
        }
    }

    @Override
    public void handleEvent(CV_PreGameStartedGameEvent event) {
       //nothing
    }

    @Override
    public void handleEvent(CV_PreGameErrorGameEvent event) {
        MessageUtility.displayErrorMessage(event.getEventDescription().toUpperCase());
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
            case "SERVER_FULL":
                MessageUtility.displayErrorMessage(event.getErrorMessage());
                return;
        }

        //Force to select an username
        if (userProposal.equals("")) {
            //send the new connection request
            userProposal = askUsername();
        }

        //use last userProposal as my username
        //TODO da fare evento apposta "username approvato"
        myUsername = userProposal;

        VC_ConnectionRequestGameEvent req;
        req = new VC_ConnectionRequestGameEvent("Tentativo di connessione", "--", 0, userProposal);
        this.client.sendEvent(req);
    }

    @Override
    public void handleEvent(CV_ReconnectionRejectedErrorGameEvent event) {
        switch (event.getErrorCode()) {
            case "WAIT_FOR_CREATION":
                MessageUtility.displayErrorMessage(event.getErrorMessage());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case "SERVER_FULL":
                MessageUtility.displayErrorMessage(event.getErrorMessage());
                handleEvent(new CV_NewGameRequestEvent(""));
                return;
        }
        VC_NewGameResponseEvent responseEvent = new VC_NewGameResponseEvent("", true);
        client.sendEvent(responseEvent);
    }

    @Override
    /*
    this user is the challenger, it has to choose the 3 cards
     */
    public void handleEvent(CV_ChallengerChosenEvent event) {
        System.out.println("⌛   WAITING   ⌛");
        System.out.println("The game is choosing the challenger...\n");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        clearScreen();
        MessageUtility.printValidMessage("You're the challenger!");
        output.println("Choose " + event.getRoomSize() + " cards for this match:");
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
        System.out.println("\nPlease insert a card number:");

        Integer id;

        //read from the console

        boolean continueInput = true;


        //id = input.nextInt();
        id = askIntToUser();
        continueInput = false;

        while (!isSelectedCardValid(id, event.getAvailableCards())) {
            MessageUtility.displayErrorMessage(id + " is not a valid card number");
            input = new Scanner(System.in);
            input.reset();
            id = input.nextInt();
        }


        CardEnum selected = CardEnum.getValueFromInt(id);
        MessageUtility.printValidMessage("You made your choice");

        //notify the server
        VC_PlayerCardChosenEvent choice = new VC_PlayerCardChosenEvent(event.getUsername(), selected);
        this.client.sendEvent(choice);
    }

    @Override
    /*
    waiting both because of the challenger or because of other players choice is made
     */
    public void handleEvent(CV_WaitPreMatchGameEvent event) {
        if(!event.getActingPlayer().equals(myUsername)){
            clearScreen();
            System.out.println("⌛   WAITING   ⌛");
            System.out.println(event.getActingPlayer().toUpperCase() + " " + event.getEventDescription());
        }
    }

    @Override
    public void handleEvent(CV_WorkerPlacementGameEvent event) {
        this.playersColor=event.getPlayerWorkerColors();
        for (Map.Entry<String, WorkerColors> entry : event.getPlayerWorkerColors().entrySet()){
            if (entry.getKey().equals(myUsername)){
                myColor= entry.getValue();
            }
        }
        opponentsCard = new HashMap<>();
        for (Map.Entry<String, CardEnum> entry : event.getTurnSequence().entrySet()){
            if (!entry.getKey().equals(myUsername)){
                opponentsCard.put(entry.getKey(), entry.getValue());
            }
            else{
                myCard = entry.getValue();
            }
        }
    }

    @Override
    public void handleEvent(CV_CommandExecutedGameEvent event) {

    }

    @Override
    public void handleEvent(CV_WaitMatchGameEvent event) {
        if(!event.getActingPlayer().equals(myUsername)){
            System.out.println("\n⌛   WAITING   ⌛");
            System.out.println(event.getEventDescription() + " " + event.getActingPlayer().toUpperCase() + "\n");
            printOpponentsInfo();
        }
    }

    @Override
    public void handleEvent(CV_TurnInfoEvent event) {
        System.out.println("Available moves: "+ event.getMovementsRemaining());
        System.out.println("Available build: "+ event.getBuildRemaining());
        System.out.println("Can climb: "+ event.getCanClimb());
        System.out.println("\n");
    }


    @Override
    public void handleEvent(CV_GameErrorGameEvent event) {
        MessageUtility.displayErrorMessage(event.getEventDescription().toUpperCase());
    }

    @Override
    /*
    this user is the challenger, he has to choose the first player
     */
    public void handleEvent(CV_ChallengerChooseFirstPlayerRequestEvent event) {
        System.out.println("Since you are the Challenger, please choose the player who starts first ");
        List<String> players = event.getPlayers();

        for (String player : players) {
            System.out.println("- " + player.toUpperCase());
        }
        System.out.println("\nPlease choose a player name from this list:");

        input = new Scanner(System.in);

        String choice = input.nextLine().toLowerCase();

        while (!players.contains(choice)) {
            clearScreen();
            MessageUtility.displayErrorMessage("Invalid username");

            System.out.println("Since you are the Challenger, please choose the player who starts first ");
            players = event.getPlayers();

            for (String player : players) {
                System.out.println("- " + player.toUpperCase());
            }
            System.out.println("\nPlease choose a player name from this list:");

            input = new Scanner(System.in);

            choice = input.nextLine().toLowerCase();
        }

        MessageUtility.printValidMessage("You chose " + choice + " as the first player.");

        VC_ChallengerChosenFirstPlayerEvent choiceEvent = new VC_ChallengerChosenFirstPlayerEvent(choice);

        client.sendEvent(choiceEvent);
    }


    private void displayCommandHelp() {
        System.out.println("COMMAND HELP:");
        System.out.println("MOVE " + "<WORKER_ID> " + "<row> " + "<column> ");
        System.out.println("BUILD " + "<WORKER ID> " + "<row> " + "<column> " + "<BLOCK> ");
        System.out.println("(BLOCK: 1,2,3,D)");
        System.out.println("");
    }

    private void displayAvailableBehaviour(CV_CommandRequestEvent event) {
        boolean canBuild, canMove;

    }


    @Override
    public void handleEvent(CV_CommandRequestEvent event) {


        boolean keepAsking = true;
        Command com = null;
        while (keepAsking) {
            System.out.println("Please insert a new command (type HELP to get command help box)");

            System.out.println("Available actions to select: ");

            for (TurnAction action : event.getAvailableActions()) {
                System.out.println(" - " + action.toString());
            }
            System.out.println(" ");

            input = new Scanner(System.in);
            String typed = input.nextLine().toLowerCase();
            String[] split = typed.split("\\s");
            String command, id, block, row, column;
            command = split[0];
            VC_PlayerCommandGameEvent request;

            if (command.equals("help")) {
                displayCommandHelp();
            } else if (command.equals("pass")) {
                request = new VC_PlayerCommandGameEvent("action request", TurnAction.PASS, myUsername, null, null, null);
                client.sendEvent(request);
                keepAsking = false;
            } else if (split.length == 4 || split.length == 5) {

                id = split[1];
                row = split[2];
                column = split[3];
                if (split.length == 5) {
                    block = split[4];
                } else {
                    block = null;
                }

                com = new Command(command, column, row, id, block);
                boolean isCommandValid = com.extract();


                if (isCommandValid) {

                    if (checkCellInput(com.getFRow(), com.getFColumn())) {
                        keepAsking = false;
                        request = new VC_PlayerCommandGameEvent("action request",
                                com.getFAction(), event.getActingPlayer(), new int[]{com.getFColumn() - 1, com.getFRow() - 1},
                                com.getFWorker(), com.getFBlock());
                        client.sendEvent(request);
                    } else {
                        MessageUtility.displayErrorMessage("invalid row or column");
                    }
                } else {
                    MessageUtility.displayErrorMessage("wrong command");

                }

            } else {
                MessageUtility.displayErrorMessage("wrong command");
            }


        }

    }

    @Override
    public void handleEvent(CV_GameOverEvent event) {
        clearScreen();
        if (event.getWinner() == null) {
            //if there is no winner it means that only one player lost
            if (event.getLosers().contains(myUsername)) {
                MessageUtility.gameOver();
            } else {
                MessageUtility.printValidMessage(event.getLosers().get(0) + " has lost the Game!");
            }
        } else {
            if (event.getWinner().equals(myUsername)) {
                MessageUtility.winner();
            } else {
                MessageUtility.gameOver();
            }
        }
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        input.reset();
        output.flush();
    }

    public boolean checkCellInput(int x, int y) {

        Range oneToFive = new Range(1, 5);
        return oneToFive.contains(x) && oneToFive.contains(y);
    }

    private void displayIslandUpdateFromJson(String islaJson) {
        //display island
        Gson gson = new Gson();
        final IslandData isla = (IslandData) gson.fromJson(islaJson, IslandData.class);

        IslandUtility temp = new IslandUtility(isla);

        temp.displayIsland();
    }

    @Override
    public void handleEvent(CV_PlayerPlaceWorkerRequestEvent event) {


        displayIslandUpdateFromJson(event.getIsland());
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
        System.out.print("Please enter a row:\t");
        y = askIntToUser();
        System.out.print("\tand a column:\t");
        x = askIntToUser();


        while (!checkCellInput(x, y)) {
            MessageUtility.displayErrorMessage("Invalid row or column");
            System.out.print("Please enter a row:\t");
            y = askIntToUser();
            System.out.print("\tand a column:\t");
            x = askIntToUser();



        }

        //subtracting  1 'cause 1...5 --> 0...4 on it.polimi.ingsw.sp58.model, it.polimi.ingsw.sp58.controller
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

    //

    @Override
    public void handleEvent(CV_GameStartedGameEvent event) {


        clearScreen();
        System.out.println("GAME IS STARTING");


    }

    //TURN

    @Override
    public void handleEvent(CV_NewTurnEvent event) {

        String yourUsername = myUsername;
        if (myUsername == null) {
            System.err.println("username not set");
        }
        String currentPlayerUsername = event.getTurnRotation().get(0);

        boolean yourTurn = yourUsername.equalsIgnoreCase(currentPlayerUsername);
        clearScreen();
        System.out.println("GAME:");
        printTurnSequence(event);

        if (yourTurn) {
            System.out.println("\nIt's your turn. \n");
            printMyInfo();
            printOpponentsInfo();
        }

    }

    public void printMyInfo(){
        switch(myColor){
            case BLUE:
                System.out.println("Your"+  BLUE_BRIGHT + " color" + ANSI_RESET);
                break;
            case PINK:
                System.out.println("Your"  + MAGENTA_BRIGHT + " color" + ANSI_RESET);
                break;
            case ORANGE:
                System.out.println("Your"  + RED_BRIGHT + " color" + ANSI_RESET);
                break;
        }
        System.out.println("YOUR CARD: " + myCard.getName().toUpperCase());
        System.out.println("\t" + myCard.getDescription());
    }

    private void printOpponentsInfo(){
        for (Map.Entry<String, CardEnum> player : opponentsCard.entrySet()) {
            System.out.println("OPPONENTS: ");
            for (Map.Entry<String, WorkerColors> p : playersColor.entrySet()) {
                if(p.getKey().equals(player.getKey())){
                    switch(p.getValue()){
                        case BLUE:
                            System.out.println("- " + player.getKey().toUpperCase() + BLUE_BRIGHT + " (color)" + ANSI_RESET);
                            break;
                        case PINK:
                            System.out.println("- " +  player.getKey().toUpperCase() + MAGENTA_BRIGHT + " (color)" + ANSI_RESET);
                            break;
                        case ORANGE:
                            System.out.println("- " + player.getKey().toUpperCase() + RED_BRIGHT + " (color)" + ANSI_RESET);
                            break;
                    }
                }
            }
            System.out.println("\tCard: " + player.getValue().getName().toUpperCase());
            System.out.println("\t" + player.getValue().getDescription() + "\n");
        }
    }


    private void printTurnSequence(CV_NewTurnEvent event) {
        MessageUtility.printDivider();

        List<String> turnRotation = event.getTurnRotation();
        System.out.print("TURN: ");

        for (String s : turnRotation) {
            if (turnRotation.indexOf(s) == 0) {
                System.out.print(ANSIColors.GREEN_UNDERLINED + s.toUpperCase() + ANSIColors.ANSI_RESET);
            } else {
                System.out.print(" <<< ");
                System.out.print(ANSIColors.ANSI_RESET + s.toUpperCase() + ANSIColors.ANSI_RESET + "\n");
            }
        }
    }

    @Override
    public void handleEvent(CV_IslandUpdateEvent event) {
        displayIslandUpdateFromJson(event.getNewIsland());
        System.out.println("");
    }


    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    //NEW GAME AFTER GAME OVER

    @Override
    public void handleEvent(CV_NewGameRequestEvent event) {
        MessageUtility.printValidMessage("Would you like to play another time?");
        System.out.println("< type YES if you want to continue >");
        output.flush();
        input.reset();
        input = new Scanner(System.in);
        String message = input.nextLine().toUpperCase();
        VC_NewGameResponseEvent responseEvent;
        if (message.equals("YES")) {
            responseEvent = new VC_NewGameResponseEvent(myUsername + " wants to play gain", true);
        } else {
            responseEvent = new VC_NewGameResponseEvent(myUsername + " doesn't want to play gain", false);
        }
        client.sendEvent(responseEvent);
    }

    @Override
    public void handleEvent(PlayerDisconnectedViewEvent event) {
        clearScreen();
        MessageUtility.displayErrorMessage(event.getEventDescription());
        output.println(event.getReason());
        System.exit(0);
    }


}
