package view.CLI.utility;

import model.CardEnum;

public class CardUtility {

    CardEnum cards;

    public static void displayAllCards() {

        System.out.println("AVAILABLE CARDS: ");
        for (CardEnum c : CardEnum.values()) {
            System.out.println(c.getNumber() + ". " + c.getName() + ":  ");
            System.out.println(c.getDescription());

        }

    }
}
