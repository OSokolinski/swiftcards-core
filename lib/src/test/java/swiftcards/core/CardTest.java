package swiftcards.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import swiftcards.core.card.*;

public class CardTest {

    @Test
    public void CardCloning_ShouldNot_CreateDuplicates() throws CloneNotSupportedException {

        StandardCard originStandard = new StandardCard(CardColor.RED, 0);
        FunctionalCard originFunctional = new FunctionalPlusFourCard();

        StandardCard cloneStandard = (StandardCard) originStandard.clone();
        FunctionalCard cloneFunctional = (FunctionalCard) originFunctional.clone();

        assertNotEquals(originStandard, cloneStandard);
        assertNotEquals(originFunctional, cloneFunctional);
    }

    @Test
    public void CardId_ShouldNotBeOverwritten_When_AppliedTwice() throws CloneNotSupportedException {

        int initialId = 19, duplicateId = 102;

        StandardCard standardCard = new StandardCard(CardColor.BLUE, 0);
        FunctionalCard functionalCard = new FunctionalStopCard(CardColor.BLUE);

        StandardCard scClone = (StandardCard) standardCard.clone().apply(initialId);
        FunctionalCard fcClone = (FunctionalCard) functionalCard.clone().apply(initialId);

        scClone.apply(duplicateId);
        fcClone.apply(duplicateId);

        assertEquals(scClone.getId(), initialId);
        assertEquals(fcClone.getId(), initialId);
    }
}
