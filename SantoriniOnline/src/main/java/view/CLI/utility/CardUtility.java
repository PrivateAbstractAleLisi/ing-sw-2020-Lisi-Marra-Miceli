package view.CLI.utility;

import model.CardEnum;

import java.util.List;

public class CardUtility {

    CardEnum cards;

    public static void displayAllCards() {

        System.out.println("AVAILABLE CARDS: ");
        for (CardEnum c : CardEnum.values()) {
            System.out.println(c.getNumber() + ". " + c.getName() + ":  ");
            System.out.println(c.getDescription());

        }

    }

    public static void displayAllAvailableCards (List<CardEnum> cards) {
        System.out.println("AVAILABLE CARDS: ");
        for (CardEnum card : cards) {
            System.out.println(card.getName() + ":  ");
            System.out.println(card.getDescription());
        }
    }
}
