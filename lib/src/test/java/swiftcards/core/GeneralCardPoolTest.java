package swiftcards.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import swiftcards.core.card.*;
import swiftcards.core.card.GeneralCardPool.UnableToGenerateCardPoolException;

import java.util.*;
import java.util.function.Consumer;


public class GeneralCardPoolTest {


    @Test
    public void DefaultCardSetGeneration_ShouldConsistOf_108Cards() throws UnableToGenerateCardPoolException {

        GeneralCardPool cardSet = GeneralCardPool
                .withDefaultConfiguration()
                .generate();

        assertEquals(108, cardSet.getCards().size());
    }

    @Test
    public void CustomCardSetGeneration_ShouldConsistOf_DeterminedElements() throws UnableToGenerateCardPoolException {

        FunctionalPlusFourCard functionalP4Prototype = new FunctionalPlusFourCard();
        FunctionalStopCard functionalStopPrototype = new FunctionalStopCard(CardColor.RED);
        StandardCard standardBluePrototype = new StandardCard(CardColor.BLUE, 5);
        StandardCard standardGreenPrototype = new StandardCard(CardColor.GREEN, 0);

        int functionalP4Amount = 3,
            functionalStopAmount = 1,
            standardBlueAmount = 9,
            standardGreenAmount = 0;

        Consumer<HashMap<Card, Integer>> schemeLambda = (HashMap<Card, Integer> prototypes) -> {

            prototypes.put(functionalP4Prototype, functionalP4Amount);
            prototypes.put(functionalStopPrototype, functionalStopAmount);
            prototypes.put(standardBluePrototype, standardBlueAmount);
            prototypes.put(standardGreenPrototype, standardGreenAmount);
        };

        GeneralCardPool cardSet = GeneralCardPool
            .withCustomConfiguration(schemeLambda)
            .generate();

        long functionalP4FinalCount = cardSet
            .getCards()
            .stream()
            .filter(c -> c instanceof FunctionalPlusFourCard
                    && c.getCardColor() == functionalP4Prototype.getCardColor()
                    && ((FunctionalPlusFourCard) c).getCardToPullAmount() == functionalP4Prototype.getCardToPullAmount())
            .count();

        long functionalStopFinalCount = cardSet
            .getCards()
            .stream()
            .filter(c -> c instanceof FunctionalStopCard
                    && c.getCardColor() == functionalStopPrototype.getCardColor())
            .count();

        long standardBlueFinalCount = cardSet
            .getCards()
            .stream()
            .filter(c -> c instanceof StandardCard
                    && c.getCardColor() == standardBluePrototype.getCardColor()
                    && ((StandardCard) c).getCardPresentation() == standardBluePrototype.getCardPresentation())
            .count();

        long standardGreenFinalCount = cardSet
            .getCards()
            .stream()
            .filter(c -> c instanceof StandardCard
                    && c.getCardColor() == standardGreenPrototype.getCardColor()
                    && ((StandardCard) c).getCardPresentation() == standardGreenPrototype.getCardPresentation())
            .count();

        assertEquals((functionalP4Amount + functionalStopAmount + standardBlueAmount + standardGreenAmount),
            cardSet.getCards().size());

        assertEquals(functionalP4Amount, functionalP4FinalCount);
        assertEquals(functionalStopAmount, functionalStopFinalCount);
        assertEquals(standardBlueAmount, standardBlueFinalCount);
        assertEquals(standardGreenAmount, standardGreenFinalCount);
    }

    @Test
    public void MainCardPoolRecyclesCardsAndResetsThem() throws UnableToGenerateCardPoolException {

        GeneralCardPool gcp = GeneralCardPool.withCustomConfiguration((cardConfig) -> {
            cardConfig.put(new StandardCard(CardColor.RED, 0), 1);
            cardConfig.put(new FunctionalPlusTwoCard(CardColor.BLUE), 2);
        })
        .generate();

        for (int i = 0; i < 3; i++) {
            Card card = gcp.pullNext();

            if (card instanceof FunctionalPlusTwoCard) {
                ((FunctionalPlusTwoCard) card).increaseCardsToPullAmount(9);
            }

            gcp.recycleCard(card);
        }

        for (int i = 0; i < 3; i++) {
            Card card = gcp.pullNext();

            if (card instanceof FunctionalPlusTwoCard) {
                assertEquals(2, ((FunctionalPlusTwoCard) card).getCardToPullAmount());
            }
        }

    }
}
