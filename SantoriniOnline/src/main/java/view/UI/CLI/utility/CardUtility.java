package view.UI.CLI.utility;

import model.CardEnum;

import java.util.List;

public class CardUtility {

    CardEnum cards;

    public static void displayAllCards() {

        System.out.println("AVAILABLE CARDS: \n");
        for (CardEnum c : CardEnum.values()) {
            System.out.println(c.getNumber() + ". " + c.getName() + ":  ");
            System.out.println("\t" + c.getDescription());

        }

    }

    public static void displayAllAvailableCards(List<CardEnum> cards) {
        System.out.println("AVAILABLE CARDS: \n");
        for (CardEnum card : cards) {
            System.out.println(card.getNumber() + ". " + card.getName() + ":  ");
            System.out.println("\t" + card.getDescription());
        }
    }
}
