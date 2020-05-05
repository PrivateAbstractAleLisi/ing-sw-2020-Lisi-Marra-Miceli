package it.polimi.ingsw.psp58.view.UI.CLI.utility;

import it.polimi.ingsw.psp58.model.CardEnum;

import java.util.List;

public class CardUtility {

    CardEnum cards;

    public static void displayAllCards() {

        System.out.println("AVAILABLE CARDS: \n");
        for (CardEnum c : CardEnum.values()) {
            if (!c.equals(CardEnum.SUPERUSER)) {
                System.out.println(c.getNumber() + ". " + c.getName() + ":  ");
                System.out.println("\t" + c.getDescription());
            }

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
