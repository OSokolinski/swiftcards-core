package swiftcards.core.player;

import swiftcards.core.card.Card;
import swiftcards.core.card.CardColor;
import swiftcards.core.card.GeneralCardPool;
import swiftcards.core.card.PlayerCardPool;

import java.util.List;
import java.util.stream.Collectors;

public class HumanPlayer extends PlayerBase implements Player {

    private final PlayerPrompter prompter;

    public HumanPlayer(Class<? extends PlayerPrompter> playerPrompterClass) throws Exception {
        super();
        prompter = playerPrompterClass.getConstructor().newInstance();
    }

    @Override
    public Card layCard(Card cardOnTable) {

        prompter.showCardOnTable(cardOnTable);

        List<Integer> eligibleCardIds = playerCardPool.pullMatchingCards(cardOnTable)
            .stream()
            .map(Card::getId)
            .collect(Collectors.toList());


        prompter.refreshCards(playerCardPool.getCards());
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
