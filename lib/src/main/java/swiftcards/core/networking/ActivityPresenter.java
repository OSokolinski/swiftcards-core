package swiftcards.core.networking;

import swiftcards.core.card.Card;
import swiftcards.core.card.CardColor;
import swiftcards.core.game.Summary;
import swiftcards.core.player.activities.PlayerStatusUpdated.*;

public interface ActivityPresenter {
    void cardLiedOnTableByPlayer(Card card);
    void cardColorChosen(CardColor cardColor);
    void cardsPulledByPlayer(int cardAmount, int playerId);
    void playerStopped(int playerId);
    void playerTurnStarted(int playerId);
    void initialCardSetOnTable(Card card);
    void cardsToTakeIncreased(int cardsToTakeAmount);
    void gameQueueSequenceReverted();
    void playerStatusUpdated(PlayerStatusData playerData);
    void gameFinished(Summary playerSummary);
}
