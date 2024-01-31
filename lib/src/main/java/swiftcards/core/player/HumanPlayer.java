package swiftcards.core.player;

import swiftcards.core.card.Card;
import swiftcards.core.card.CardColor;
import swiftcards.core.card.GeneralCardPool;
import swiftcards.core.util.ConfigService;

import java.util.List;
import java.util.stream.Collectors;

public class HumanPlayer extends PlayerBase implements Player {

    public HumanPlayer() throws Exception {
        super();
        displayName = ConfigService.getInstance().getPlayerName();
    }

    @Override
    public Card layCard(Card cardOnTable) {

        getPrompter().showCardOnTable(cardOnTable);

        List<Integer> eligibleCardIds = playerCardPool.pullMatchingCards(cardOnTable)
            .stream()
            .map(Card::getId)
            .collect(Collectors.toList());

        getPrompter().refreshCards(playerCardPool.getCards());
        getPrompter().showPlayerCards(eligibleCardIds);

        Card chosenCard = getPrompter().selectCard();

        if (chosenCard != null) {
            playerCardPool.removeCardFromPool(chosenCard);
        }

        getPrompter().refreshCards(playerCardPool.getCards());

        return chosenCard;
    }

    @Override
    public void pullCard(GeneralCardPool cardPool) {
        playerCardPool.getCards().add(cardPool.pullNext());
    }


    @Override
    public CardColor choosePoolColor() {
        return getPrompter().selectCardColor();
    }

    @Override
    public void pullPenaltyCards(GeneralCardPool cardPool, int amount) {
        super.pullPenaltyCards(cardPool, amount);
        getPrompter().refreshCards(playerCardPool.getCards());
    }

    private PlayerPrompter getPrompter() {
        return ConfigService.getInstance().getPlayerPrompter();
    }
}
