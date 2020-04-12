package view;

import auxiliary.ANSIColors;
import auxiliary.Range;
import event.core.ViewEventListener;
import event.events.*;
import event.events.temporary.WelcomeGameEvent;

import java.io.PrintStream;

import java.util.Scanner;

public class WelcomeView implements ViewEventListener, View {

    //IN-OUT DATA FROM CONSOLE
    private final PrintStream output;
    private final Scanner input;

    public WelcomeView() {
        this.output = System.out;
        this.input = new Scanner(System.in);
    }

    //MAIN METHODS


    public void start() {

        displayTitle();
        askUsername();
        //ask IP

    }

    private void askUsername() {

        String str = "null";
        output.println("Insert a valid username ");
        output.println("[at least 3 alpha numeric characters] ");
        str = input.nextLine();

        while (!checkLocalUsernameAlphaNumeric(str)) {
            displayUsernameErrorMessage("⚠ Invalid username: please at least 3 alpha numeric characters");
            str = input.nextLine();
        }

        //avvia una richiesta di connessione
        //send username
        //printValidMessage("Username");

    }

    private void askGameRoomSize() {
        Integer sizeIn = null;
        Range validSize = new Range(2,3);
        System.out.println("You're the first player, please insert the room size:");
        sizeIn = input.nextInt();

        while(!validSize.contains(sizeIn)) {
            System.err.println("Invalid size: please enter 2 or 3");
            sizeIn = input.nextInt();
        }

        printValidMessage("Room size");
        RoomSizeResponseGameEvent response;
        response = new RoomSizeResponseGameEvent("first player sends the chosen size", sizeIn);

    }

    /**
     * prints (message) is valid in green
     * @param message
     */
    private void printValidMessage(String message) {
        output.println(ANSIColors.ANSI_GREEN + "✓ " +message+ " is valid " + ANSIColors.ANSI_RESET);
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

    }


    @Override
    public void handleEvent(ConnectionRejectedErrorGameEvent event) {

        switch (event.getErrorCode()) {
            case "USER_TAKEN" :
                //notify the error on screen
                System.err.print("Username " + event.getWrongUsername() );
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
    public void handleEvent(RoomSizeRequestGameEvent event) {
        askGameRoomSize();
    }
}
