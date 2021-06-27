package swiftcards.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import swiftcards.core.card.*;
import swiftcards.core.player.AIPlayer;

public class AIPlayerTest {

    @Test
    public void PlayerChoosesTheMostPopularCardColor() {
        AIPlayer player = new AIPlayer(false);

        player.getCardPool().getCards().add(new StandardCard(CardColor.RED, 7));
        player.getCardPool().getCards().add(new StandardCard(CardColor.BLUE, 1));
        player.getCardPool().getCards().add(new StandardCard(CardColor.BLUE, 2));
        player.getCardPool().getCards().add(new FunctionalPlusFourCard());
        player.getCardPool().getCards().add(new FunctionalPlusTwoCard(CardColor.RED));
        player.getCardPool().getCards().add(new FunctionalStopCard(CardColor.RED));


        assertEquals(CardColor.RED, player.choosePoolColor());
    }

    @Test
    public void PlayerChoosesRandomCard_WhenHasNoColorfulCard() {
        AIPlayer player = new AIPlayer();
        player.getCardPool().getCards().add(new FunctionalPlusFourCard());

        assertNotNull(player.choosePoolColor());
    }

    @Test
    public void PlayerInformsAboutEndedGame_WhenHasNoCards() {
        AIPlayer player = new AIPlayer();

        player.getCardPool().getCards().add(new StandardCard(CardColor.YELLOW, 3).apply(0));
        player.getCardPool().getCards().add(new FunctionalStopCard(CardColor.BLUE).apply(1));
        player.getCardPool().getCards().add(new FunctionalPlusFourCard().apply(2));

        player.layCard(new StandardCard(CardColor.BLUE, 5));
        player.layCard(new StandardCard(CardColor.GREEN, 3));

        assertFalse(player.hasFinishedPlay());

        player.layCard(new StandardCard(CardColor.RED, 9));

        assertTrue(player.hasFinishedPlay());
    }

    @Test
    public void PlayerSelectsStandardCards_BeforeFunctionalOnes() {
        AIPlayer player = new AIPlayer();

        player.getCardPool().getCards().add(new StandardCard(CardColor.RED, 4).apply(0));
        player.getCardPool().getCards().add(new FunctionalSwitchColorCard().apply(1));
        player.getCardPool().getCards().add(new FunctionalPlusFourCard().apply(2));
        player.getCardPool().getCards().add(new FunctionalPlusTwoCard(CardColor.RED).apply(3));
        player.getCardPool().getCards().add(new StandardCard(CardColor.GREEN, 4).apply(4));

        assertTrue(player.layCard(new StandardCard(CardColor.BLUE, 4)) instanceof StandardCard);
        assertTrue(player.layCard(new StandardCard(CardColor.RED, 1)) instanceof StandardCard);
        assertTrue(player.layCard(new StandardCard(CardColor.RED, 1)) instanceof FunctionalCard);
        assertTrue(player.layCard(new StandardCard(CardColor.RED, 1)) instanceof FunctionalCard);
        assertTrue(player.layCard(new StandardCard(CardColor.RED, 1)) instanceof FunctionalCard);
    }
}
