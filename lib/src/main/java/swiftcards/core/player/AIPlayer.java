package swiftcards.core.player;

import swiftcards.core.card.*;

import java.util.*;
import java.util.stream.Collectors;

public class AIPlayer extends PlayerBase implements Player {

    private final boolean keepLogging;

    /**
     * Initiating the player. Debug logging will not be active.
     */
    public AIPlayer() {
        this(false);
    }

    /**
     * Initiating the player.
     *
     * @param enableLogging Setting, if debug logging should be active
     */
    public AIPlayer(boolean enableLogging) {
        super();
        keepLogging = enableLogging;
    }

    /**
     * @param newId New player ID
     */
    public void setId(int newId) {
        id = newId;
    }

    @Override
    public Card layCard(Card cardOnTable) {

        List<Card> matchingCards = playerCardPool.pullMatchingCards(cardOnTable);
        log(String.format("I've got %d matching cards", matchingCards.size()));

        if (matchingCards.size() > 0) {

            Card chosenCard = chooseCardFromMatchingOnes(matchingCards);
            log("I lay card: " + chosenCard.toString());

            playerCardPool.removeCardFromPool(chosenCard);
            return chosenCard;
        }
        else {
            log("I don't lay anything");
            return null;
        }
    }

    @Override
    public void pullCard(GeneralCardPool cardPool) {
        Card pulledCard = cardPool.pullNext();

        log("I pulled card: " + pulledCard.toString());
        playerCardPool.getCards().add(pulledCard);
    }

    @Override
    public void pullPenaltyCards(GeneralCardPool cardPool, int amount) {
        super.pullPenaltyCards(cardPool, amount);

        log(String.format("I've pulled %d penalty cards", amount));
    }

    @Override
    public PlayerCardPool getCardPool() {
        return playerCardPool;
    }

    @Override
    public CardColor choosePoolColor() {

        CardColor chosenColor = getRepetitiveColor();

        if (chosenColor == null) {

            Random random = new Random();

            CardColor[] colors = CardColor.values();
            chosenColor = colors[random.nextInt(colors.length)];
        }

        log("I choose color: " + chosenColor.toString());
        return chosenColor;
    }

    /**
     * @return Most common color in player's card pool. If player has no colorful cards, NULL will be returned
     */
    private CardColor getRepetitiveColor() {

        LinkedHashMap<CardColor, Integer> colorCounts = new LinkedHashMap<>();

        for (Card card : playerCardPool.getCards()) {

            if (card instanceof FunctionalSwitchColorCard) {
                continue;
            }

            if (colorCounts.containsKey(card.getCardColor())) {
                colorCounts.put(card.getCardColor(), colorCounts.get(card.getCardColor()) + 1);
            }
            else {
                colorCounts.put(card.getCardColor(), 1);
            }
        }

        int topCount = 0;
        CardColor topColor = null;

        for (Map.Entry<CardColor, Integer> mapEntry : colorCounts.entrySet()) {

            int count = mapEntry.getValue();

            if (count > topCount) {
                topColor = mapEntry.getKey();
                topCount = count;
            }
        }

        return topColor;
    }

    /**
     * @param matchingCards List of available cards
     * @return One of cards, that may be lied. Standard cards will be used first.
     */
    private Card chooseCardFromMatchingOnes(List<Card> matchingCards) {

        return matchingCards.stream()
            .sorted((c1, c2) -> {
                if (c1 instanceof FunctionalCard && c2 instanceof FunctionalCard) {
                    return 0;
                }
                else if (c1 instanceof FunctionalCard) {
                    return 1;
                }
                else return -1;
            })
            .collect(Collectors.toList())
            .get(0);

    }

    /**
     * @param logText Debug log, that should be printed. It is going to have player's prefix
     */
    private void log(String logText) {
        if (keepLogging) {
            System.out.printf("[Player %d]: %s.%n", id, logText);
        }
    }
}
