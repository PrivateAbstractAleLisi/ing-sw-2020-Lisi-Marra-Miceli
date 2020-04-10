package view;

import auxiliary.ANSIColors;
import auxiliary.Range;
import event.core.EventSource;
import jdk.jfr.Event;

import java.io.PrintStream;
import java.util.EventListener;
import java.util.Scanner;
import java.util.stream.Stream;

public class WelcomeView extends EventSource implements View, EventListener {

    //IN-OUT DATA FROM CONSOLE
    private final PrintStream output;
    private final Scanner input;

    public WelcomeView() {
        this.output = System.out;
        this.input = new Scanner(System.in);
    }

    public void start() {

        displayTitle();
        String str = "null";
        output.println("Insert a valid username ");
        output.println("[at least 3 alpha numeric characters] ");
        str = input.nextLine();

        while (!checkLocalUsernameAlphaNumeric(str)) {
            displayUsernameErrorMessage("⚠ Invalid username: please at least 3 alpha numeric characters");
            str = input.nextLine();
        }

        printValidMessage("Username");

        askGameRoomSize();

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
    }

    private void printValidMessage(String message) {
        output.println(ANSIColors.ANSI_GREEN + "✓ " +message+ " is valid " + ANSIColors.ANSI_RESET);
    }

    //USERNAME VALIDATION:
    private void checkUsernameOnSever() {
        //TODO sends an event to the server that the username has been inserted, the server checks if it's valid
    }

    //Called both by invalid username already taken (via server) or invalid usernamenot alphanumeric
    private boolean checkLocalUsernameAlphaNumeric(String username) {
        if (username == null || username.length() < 3) {
            return false;
        } else {
            return username.matches("^[a-zA-Z0-9]*$");

        }

    }

    //DISPLAY UTILITIES
    private void displayUsernameErrorMessage(String errorMessage) {
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
}
