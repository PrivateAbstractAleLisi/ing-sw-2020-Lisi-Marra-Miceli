package view.CLI.utility;

import auxiliary.ANSIColors;

import java.io.PrintStream;
import java.util.stream.Stream;

public class MessageUtility {

    //Called both by invalid username already taken (via server) or invalid username not alphanumeric
    public static void displayErrorMessage(String errorMessage) {
        System.err.println("⚠ ERROR: "  + "errorMessage");

    }

    /**
     * prints (message) is valid in green
     *
     * @param message
     */
    public static void printValidMessage(String message) {
        System.out.println(ANSIColors.ANSI_GREEN + "✓ " + message + ANSIColors.ANSI_RESET);
    }

    public static void  displayTitle() {

        PrintStream output = (PrintStream) System.out;
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
}
