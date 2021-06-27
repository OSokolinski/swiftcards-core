package swiftcards.core.player;

import swiftcards.core.card.Card;
import swiftcards.core.card.CardColor;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class ConsolePlayerPrompter implements PlayerPrompter {

    private List<Card> cachedCardList;
    private List<Integer> cachedEligibleCardIds;
    private final Scanner scanner;

    private final String CONSOLE_COLOR_DEFAULT = "\033[0;37m";
    private final String CONSOLE_COLOR_GREEN = "\033[0;32m";

    public ConsolePlayerPrompter() {
        cachedCardList = new LinkedList<>();
        cachedEligibleCardIds = new LinkedList<>();
        scanner = new Scanner(System.in);
        System.out.print(CONSOLE_COLOR_DEFAULT);
    }

    @Override
    public void showCardOnTable(Card cardOnTable) {
        System.out.printf("Card on table: %s \n", cardOnTable.toString());
    }

    @Override
    public void refreshCards(List<Card> cards) {
        cachedCardList = cards;
    }

    @Override
    public void showPlayerCards(final List<Integer> eligibleCardIds) {

        cachedEligibleCardIds = eligibleCardIds;
        StringBuilder strBuilder = new StringBuilder("Your cards are: \n");

        int iterator = 0;

        for (Card c : cachedCardList) {

            String openingColor = eligibleCardIds.contains(c.getId()) ? CONSOLE_COLOR_GREEN : CONSOLE_COLOR_DEFAULT;

            strBuilder.append(String.format("%s %d: %s %s \n", openingColor, iterator, c.toString(), CONSOLE_COLOR_DEFAULT));
            iterator++;
        }

        if (eligibleCardIds.size() < 1) {
            strBuilder.append(String.format("%s %d: I've got none matching %s \n", CONSOLE_COLOR_GREEN, iterator, CONSOLE_COLOR_DEFAULT));
        }

        System.out.print(strBuilder.toString());
    }

    @Override
    public Card selectCard() {

        int selection = -1;

        while (selection == -1) {
            System.out.print("Select card: ");
            String selectInput = scanner.nextLine();
            int selectionCandidate = -1;

            try {
                selectionCandidate = Integer.parseInt(selectInput);
            }
            catch (NumberFormatException e) {
                System.out.println("Invalid selection.");
                continue;
            }

            if (selectionCandidate >= 0 && selectionCandidate < cachedCardList.size()) {
                selection = selectionCandidate;
            }
            else if (selectionCandidate == cachedCardList.size() && cachedEligibleCardIds.size() < 1) {
                return null;
            }
            else {
                System.out.println("Invalid selection.");
                continue;
            }

            if (!cachedEligibleCardIds.contains(cachedCardList.get(selection).getId())) {
                System.out.println("Disallowed selection.");
                selection = -1;
            }
        }

        return cachedCardList.get(selection);
    }

    @Override
    public CardColor selectCardColor() {

        StringBuilder strBuilder = new StringBuilder("Available card colors: \n");

        int iterator = 0;
        int selection = -1;

        for (CardColor c : CardColor.values()) {
            strBuilder.append(String.format("%s %d: %s %s \n", CONSOLE_COLOR_GREEN, iterator, c.toString(), CONSOLE_COLOR_DEFAULT));
            iterator++;
        }

        System.out.print(strBuilder.toString());

        while (selection == -1) {
            System.out.print("Select color: ");
            String selectInput = scanner.nextLine();
            int selectionCandidate = -1;

            try {
                selectionCandidate = Integer.parseInt(selectInput);
            }
            catch (NumberFormatException e) {
                System.out.println("Invalid selection.");
                continue;
            }

            if (selectionCandidate >= 0 && selectionCandidate < CardColor.values().length) {
                selection = selectionCandidate;
            }
            else {
                System.out.println("Invalid selection.");
            }
        }

        return CardColor.values()[selection];
    }

}
