package swiftcards.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import swiftcards.core.card.*;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerCardPoolTest {

    private static final PlayerCardPool playerCardPool = new PlayerCardPool();

    @BeforeAll
    public static void before() {

        // ID 0 - 9; Standard cards

        playerCardPool.getCards().add(new StandardCard(CardColor.YELLOW, 7).apply(0));
        playerCardPool.getCards().add(new StandardCard(CardColor.YELLOW, 2).apply(1));
        playerCardPool.getCards().add(new StandardCard(CardColor.GREEN, 7).apply(2));

        // ID 10 - 19; Functional cards

        playerCardPool.getCards().add(new FunctionalPlusTwoCard(CardColor.YELLOW).apply(10));
        playerCardPool.getCards().add(new FunctionalPlusFourCard().apply(11));
        playerCardPool.getCards().add(new FunctionalReturnCard(CardColor.RED).apply(12));
        playerCardPool.getCards().add(new FunctionalSwitchColorCard().apply(13));
        playerCardPool.getCards().add(new FunctionalStopCard(CardColor.BLUE).apply(14));

    }

    @Test
    public void StandardCard_ShouldBeCovered_BySpecificCards() {

        StandardCard red7 = new StandardCard(CardColor.RED, 7);

        Integer[] red7Matches = playerCardPool
                .pullMatchingCards(red7)
                .stream()
                .map(Card::getId)
                .toArray(Integer[]::new);

        Integer[] red7Expected = new Integer[] {0, 2, 11, 12, 13};

        assertArrayEquals(red7Expected, red7Matches);

        StandardCard yellow3 = new StandardCard(CardColor.YELLOW, 3);

        Integer[] yellow3Matches = playerCardPool
                .pullMatchingCards(yellow3)
                .stream()
                .map(Card::getId)
                .toArray(Integer[]::new);

        Integer[] yellow3Expected = new Integer[] {0, 1, 10, 11, 13};

        assertArrayEquals(yellow3Expected, yellow3Matches);

        StandardCard blue9 = new StandardCard(CardColor.BLUE, 9);

        Integer[] blue9Matches = playerCardPool
                .pullMatchingCards(blue9)
                .stream()
                .map(Card::getId)
                .toArray(Integer[]::new);

        Integer[] blue9Expected = new Integer[] {11, 13, 14};

        assertArrayEquals(blue9Expected, blue9Matches);
    }

    @Test
    public void FunctionalCard_ShouldBeCovered_BySpecificCards() {

        FunctionalPlusFourCard activeBluePlus4 = new FunctionalPlusFourCard();
        activeBluePlus4.chooseColor(CardColor.BLUE);

        Integer[] activeBluePlus4Matches = playerCardPool
                .pullMatchingCards(activeBluePlus4)
                .stream()
                .map(Card::getId)
                .toArray(Integer[]::new);

        Integer[] activeBluePlus4Expected = new Integer[] {11};

        assertArrayEquals(activeBluePlus4Expected, activeBluePlus4Matches);

        FunctionalPlusFourCard inactiveRedPlus4 = new FunctionalPlusFourCard();
        inactiveRedPlus4.chooseColor(CardColor.RED);
        inactiveRedPlus4.setIsAffective(false);

        Integer[] inactiveRedPlus4Matches = playerCardPool
                .pullMatchingCards(inactiveRedPlus4)
                .stream()
                .map(Card::getId)
                .toArray(Integer[]::new);

        Integer[] inactiveRedPlus4Expected = new Integer[] {11, 12, 13};

        assertArrayEquals(inactiveRedPlus4Expected, inactiveRedPlus4Matches);

        FunctionalStopCard activeRedStop = new FunctionalStopCard(CardColor.RED);

        Integer[] activeRedStopMatches = playerCardPool
                .pullMatchingCards(activeRedStop)
                .stream()
                .map(Card::getId)
                .toArray(Integer[]::new);

        Integer[] activeRedStopExpected = new Integer[] {};

        assertArrayEquals(activeRedStopExpected, activeRedStopMatches);

        FunctionalPlusTwoCard activeGreenTakeTwo = new FunctionalPlusTwoCard(CardColor.GREEN);

        Integer[] activeGreenTakeTwoMatches = playerCardPool
                .pullMatchingCards(activeGreenTakeTwo)
                .stream()
                .map(Card::getId)
                .toArray(Integer[]::new);

        Integer[] activeGreenTakeTwoExpected = new Integer[] {10};

        assertArrayEquals(activeGreenTakeTwoExpected, activeGreenTakeTwoMatches);

        FunctionalReturnCard inactiveYellowReturn = new FunctionalReturnCard(CardColor.YELLOW);
        inactiveYellowReturn.setIsAffective(false);

        Integer[] inactiveYellowReturnMatches = playerCardPool
                .pullMatchingCards(inactiveYellowReturn)
                .stream()
                .map(Card::getId)
                .toArray(Integer[]::new);

        Integer[] inactiveYellowReturnExpected = new Integer[] {0, 1, 10, 11, 12, 13};

        assertArrayEquals(inactiveYellowReturnExpected, inactiveYellowReturnMatches);

        FunctionalPlusTwoCard inactiveBlueTakeTwo = new FunctionalPlusTwoCard(CardColor.BLUE);
        inactiveBlueTakeTwo.setIsAffective(false);

        Integer[] inactiveBlueTakeTwoMatches = playerCardPool
                .pullMatchingCards(inactiveBlueTakeTwo)
                .stream()
                .map(Card::getId)
                .toArray(Integer[]::new);

        Integer[] inactiveBlueTakeTwoExpected = new Integer[] {10, 11, 13, 14};

        assertArrayEquals(inactiveBlueTakeTwoExpected, inactiveBlueTakeTwoMatches);

        FunctionalStopCard inactiveYellowStop = new FunctionalStopCard(CardColor.YELLOW);
        inactiveYellowStop.setIsAffective(false);

        Integer[] inactiveYellowStopMatches = playerCardPool
                .pullMatchingCards(inactiveYellowStop)
                .stream()
                .map(Card::getId)
                .toArray(Integer[]::new);

        Integer[] inactiveYellowStopExpected = new Integer[] {0, 1, 10, 11, 13, 14};

        assertArrayEquals(inactiveYellowStopExpected, inactiveYellowStopMatches);
    }
}
