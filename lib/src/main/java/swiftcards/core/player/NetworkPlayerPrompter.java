package swiftcards.core.player;

import swiftcards.core.card.Card;
import swiftcards.core.card.CardColor;
import swiftcards.core.networking.ExternalEventEmitted;
import swiftcards.core.networking.NetworkExternalEventBus;
import swiftcards.core.networking.NetworkInternalEventBus;
import swiftcards.core.networking.event.IncomingEvent;
import swiftcards.core.networking.event.ingame.*;
import swiftcards.core.util.Subscriber;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NetworkPlayerPrompter implements PlayerPrompter {

    private final int connectionId;
    private final Subscriber<IncomingEvent> eventHandler;

    private volatile Lock threadLock;

    private Card cachedSelectedCard;
    private CardColor cachedSelectedColor;

    public NetworkPlayerPrompter(int networkConnectionId) {

        connectionId = networkConnectionId;
        threadLock = new ReentrantLock();

        eventHandler = new Subscriber<IncomingEvent>((event) -> {

            if (connectionId != event.getConnectionId()) {
                return;
            }

            if (event.getEvent() instanceof CardSelected) {
                onCardSelected((Card) event.getEvent().getEventData());
            }
            else if (event.getEvent() instanceof ColorSelected) {
                onColorSelected((CardColor) event.getEvent().getEventData());
            }
            else return;

            threadLock.unlock();
        });

        NetworkInternalEventBus.getInstance().on(ExternalEventEmitted.class, eventHandler);
    }

    private void onCardSelected(Card selectedCard) {
        cachedSelectedCard = selectedCard;
    }

    private void onColorSelected(CardColor selectedColor) {
        cachedSelectedColor= selectedColor;
    }

    @Override
    public void showCardOnTable(Card cardOnTable) {
        NetworkExternalEventBus.getInstance().emit(new CardLiedOnTable(cardOnTable), connectionId);
    }

    @Override
    public void showPlayerCards(List<Integer> eligibleCardIds) {
        NetworkExternalEventBus.getInstance().emit(new PlayerCardsShown(eligibleCardIds), connectionId);
    }

    @Override
    public Card selectCard() {

        NetworkExternalEventBus.getInstance().emit(new PlayerTurnExpected(), connectionId);
        threadLock.lock();

        return cachedSelectedCard;
    }

    @Override
    public CardColor selectCardColor() {

        NetworkExternalEventBus.getInstance().emit(new ColorChooseExpected(), connectionId);
        threadLock.lock();

        return cachedSelectedColor;
    }

    @Override
    public void refreshCards(List<Card> cards) {
        NetworkExternalEventBus.getInstance().emit(new PlayerCardsRefreshed(cards), connectionId);
    }

    public void cleanUp() {
        threadLock.unlock();
        eventHandler.unsubscribe();
    }
}
