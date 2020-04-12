package view;

import auxiliary.ANSIColors;
import auxiliary.Range;
import event.core.EventListener;
import event.core.EventSource;
import event.events.*;
import placeholders.VirtualServer;

import java.io.PrintStream;
import java.util.Scanner;

import static event.core.ListenerType.VIEW;

public class WelcomeView extends EventSource implements EventListener, View {

    //IN-OUT DATA FROM CONSOLE
    private final PrintStream output;
    private final Scanner input;

    public WelcomeView() {
        this.output = System.out;
        this.input = new Scanner(System.in);
    }

    VirtualServer virtualServer;

    //MAIN METHODS


    public void start() {
        virtualServer = new VirtualServer();
        virtualServer.attachListenerByType(VIEW, this);
        displayTitle();
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
        //avvia una richiesta di connessione

        //send username
        //printValidMessage("Username");

    }

    private void askGameRoomSize() {
        Integer sizeIn = null;
        Range validSize = new Range(2, 3);
        System.out.println("You're the first player, please insert the room size:");
        sizeIn = input.nextInt();

        while (!validSize.contains(sizeIn)) {
            System.err.println("Invalid size: please enter 2 or 3");
            sizeIn = input.nextInt();
        }

        printValidMessage("Room size");
        RoomSizeResponseGameEvent response;
        response = new RoomSizeResponseGameEvent("first player sends the chosen size", sizeIn);
        notifyAllObserverByType(VIEW, response);
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

    //DISPLAY UTILITIES

    //Called both by invalid username already taken (via server) or invalid username not alphanumeric
    private void displayUsernameErrorMessage(String errorMessage) {
        System.err.print("\n Invalid Username: ");
        System.err.println(errorMessage);
    }

    private void displayTitle() {


        output.println("\n\t\t\t\t\tWELCOME TO:\t\t");
        output.println("  __   ___  __  __ ______   ___   ____  __ __  __ __\n" +
                " (( \\ // \\\\ ||\\ || | || |  // \\\\  || \\\\ || ||\\ || ||\n" +
                "  \\\\  ||=|| ||\\\\||   ||   ((   )) ||_// || ||\\\\|| ||\n" +
                " \\_)) || || || \\||   ||    \\\\_//  || \\\\ || || \\|| ||\n" +
                "                                                    ");

        output.println(" _______  __    _  ___      ___   __    _  _______ \n" +
                "|       ||  |  | ||   |    |   | |  |  | ||       |\n" +
                "|   _   ||   |_| ||   |    |   | |   |_| ||    ___|\n" +
                "|  | |  ||       ||   |    |   | |       ||   |___ \n" +
                "|  |_|  ||  _    ||   |___ |   | |  _    ||    ___|\n" +
                "|       || | |   ||       ||   | | | |   ||   |___ \n" +
                "|_______||_|  |__||_______||___| |_|  |__||_______|");


    }


    //INTERFACE IMPLEMENTATION
    @Override
    public void clearScreen() {

        System.out.print("\033[H\033[2J");
        System.out.flush();

    }


    @Override
    public void updateScreen() {

    }

    @Override
    public void handleEvent(GameEvent e) {
        System.out.println("dioooooo");
    }

    @Override
    public void handleEvent(RoomSizeResponseGameEvent event) {

    }

    @Override
    public void handleEvent(RoomUpdateGameEvent event) {
        System.out.println("Ciaooo Stanza Creata");
        for (int i = 0; i < event.getUsersInRoom().length; i++) {
            int j = i + 1;
            System.out.println(j + ") " + event.getUsersInRoom()[i]);
        }
    }

    @Override
    public void handleEvent(ConnectionRequestGameEvent event) {

    }

    @Override
    public void handleEvent(ConnectionRequestServerGameEvent event) {

    }


    @Override
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
        askUsername();


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
    public void handleEvent(RoomSizeRequestGameEvent event) {
        askGameRoomSize();
    }
}
