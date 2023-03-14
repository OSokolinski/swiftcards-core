package swiftcards.core.networking;

import swiftcards.core.card.Card;
import swiftcards.core.card.CardColor;

public interface NetworkActivityPresenter {
    void cardLiedOnTableByPlayer(Card card);
    void cardColorChosen(CardColor cardColor);
    void cardsPulledByPlayer(int cardAmount);
    void playerStopped(int playerId);
    void playerTurdStarted(int playerId);
    void initialCardSetOnTable(Card card);
    void cardsToTakeIncreased(int cardsToTakeAmount);
    void gameQueueSequenceReverted();
}
