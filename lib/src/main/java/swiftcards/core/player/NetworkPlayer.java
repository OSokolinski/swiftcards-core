package swiftcards.core.player;

import swiftcards.core.card.Card;
import swiftcards.core.card.CardColor;
import swiftcards.core.card.GeneralCardPool;

import java.util.List;
import java.util.stream.Collectors;

public class NetworkPlayer extends PlayerBase implements Player {

    private final NetworkPlayerPrompter prompter;

    public NetworkPlayer(int networkConnectionId) {
        super();
        prompter = new NetworkPlayerPrompter(networkConnectionId);
    }

    @Override
    public Card layCard(Card cardOnTable) {

        prompter.showCardOnTable(cardOnTable);
        prompter.refreshCards(playerCardPool.getCards());

        List<Integer> eligibleCardIds = playerCardPool.pullMatchingCards(cardOnTable)
            .stream()
            .map(Card::getId)
            .collect(Collectors.toList());

        prompter.showPlayerCards(eligibleCardIds);

        Card chosenCard = prompter.selectCard();

        if (chosenCard != null) {
            playerCardPool.removeCardFromPool(chosenCard);
        }

        return chosenCard;
    }

    @Override
    public void pullCard(GeneralCardPool cardPool) {
        playerCardPool.getCards().add(cardPool.pullNext());
    }

    @Override
    public CardColor choosePoolColor() {
        return prompter.selectCardColor();
    }
}
