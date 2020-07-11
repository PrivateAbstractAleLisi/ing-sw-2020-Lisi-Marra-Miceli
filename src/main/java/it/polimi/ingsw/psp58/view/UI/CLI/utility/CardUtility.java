package it.polimi.ingsw.psp58.view.UI.CLI.utility;

import it.polimi.ingsw.psp58.model.CardEnum;

import java.util.List;

/**
 * static methods that help printing a list of cards
 */
public class CardUtility {

    CardEnum cards;

    /**
     * prints every card in CardEnum as a list NUMBER. CARD NAME: DESCRIPTION
     */
    public static void displayAllCards() {

        System.out.println("AVAILABLE CARDS: \n");
        for (CardEnum c : CardEnum.values()) {
            if (!c.equals(CardEnum.SUPERUSER)) {
                System.out.println(c.getNumber() + ". " + c.getName() + ":  ");
                System.out.println("\t" + c.getDescription());
            }

        }
    }

    /**
     * prints every card in a given list as a list NUMBER. CARD NAME: DESCRIPTION
     * @param cards the list of cards that will be printed
     */
    public static void displayAllAvailableCards(List<CardEnum> cards) {
        System.out.println("AVAILABLE CARDS: \n");
        for (CardEnum card : cards) {
                System.out.println(card.getNumber() + ". " + card.getName() + ":  ");
                System.out.println("\t" + card.getDescription());
        }
    }
}
