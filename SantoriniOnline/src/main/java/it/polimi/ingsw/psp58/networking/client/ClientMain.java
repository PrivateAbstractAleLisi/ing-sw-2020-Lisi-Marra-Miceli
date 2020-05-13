package it.polimi.ingsw.psp58.networking.client;

import it.polimi.ingsw.psp58.view.UI.CLI.CLIView;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.application.Application;

public class ClientMain {

    public static void main(String[] args) {
        //starts the cli
        CLIView cli = new CLIView();
//        cli.start();

//        //starts the gui
      Application.launch(GUI.class);


    }
}
