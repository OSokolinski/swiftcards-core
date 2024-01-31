package swiftcards.core.ui.cli;

import swiftcards.core.card.Card;
import swiftcards.core.card.CardColor;
import swiftcards.core.game.Summary;
import swiftcards.core.networking.ActivityPresenter;
import swiftcards.core.player.activities.PlayerStatusUpdated;

public class ConsoleNetworkActivityPresenter implements ActivityPresenter {

    int currentPlayerId = -1;

    @Override
    public void cardLiedOnTableByPlayer(Card card) {
        System.out.printf("Card lied on table: %s%n", card);
    }

    @Override
    public void cardColorChosen(CardColor cardColor) {
        System.out.printf("Card color chosen: %s%n", cardColor);
    }

    @Override
    public void cardsPulledByPlayer(int cardAmount, int playerId) {
        System.out.printf("Cards pulled by player: %d%n", cardAmount);
    }

    @Override
    public void playerStopped(int playerId) {
        System.out.printf("Player %d has been stopped%n", playerId);
    }

    @Override
    public void playerTurnStarted(int playerId) {
        currentPlayerId = playerId;
        System.out.printf("Player %d makes turn%n", playerId);
    }

    @Override
    public void initialCardSetOnTable(Card card) {
        System.out.printf("Card %s has been set on the table%n", card);
    }

    @Override
    public void cardsToTakeIncreased(int cardsToTakeAmount) {
        System.out.printf("Cards to take has been increased to %d%n", cardsToTakeAmount);
    }

    @Override
    public void gameQueueSequenceReverted() {
        System.out.println("Game queue has been reverted");
    }

    @Override
    public void playerStatusUpdated(PlayerStatusUpdated.PlayerStatusData playerData) {

    }

    @Override
    public void gameFinished(Summary playerSummary) {

    }
}
