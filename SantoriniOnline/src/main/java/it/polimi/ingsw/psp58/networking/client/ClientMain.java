package it.polimi.ingsw.psp58.networking.client;

import it.polimi.ingsw.psp58.view.UI.CLI.CLIView;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.application.Application;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {
        //starts the cli
        CLIView cli = new CLIView();

        System.out.println("Please enter the interface you would like to play with");
        System.out.println("1. CLI");
        System.out.println("2. GUI (Suggested)");
        Scanner input = new Scanner(System.in);
        int choice = -1;
        boolean continueInput = true;
        do {
            try {
                choice = input.nextInt();
                if (choice != 1 && choice != 2) {
                    System.out.println("ERROR: please insert a valid integer.");
                }
                else {
                    continueInput = false;
                }

            }
            catch (InputMismatchException e) {
                System.out.println("ERROR: please insert a valid integer.");
            }

        }
        while(continueInput);
        switch(choice) {
            case 1:
                cli.start();
                break;
            case 2:
                Application.launch(GUI.class);
                break;
            default:
                System.out.println("ERROR: please insert a valid integer.");
                System.exit(0);
                break;


        }


//        //starts the gui
        //



    }
}
