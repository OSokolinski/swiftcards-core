package swiftcards.core.player;

import swiftcards.core.card.Card;
import swiftcards.core.card.CardColor;
import swiftcards.core.networking.ChannelIncomingData;
import swiftcards.core.networking.NetworkExternalEventBus;
import swiftcards.core.networking.NetworkInternalEventBus;
import swiftcards.core.networking.event.DataReceived;
import swiftcards.core.networking.event.ingame.*;
import swiftcards.core.util.ConfigService;
import swiftcards.core.util.Event;
import swiftcards.core.util.Subscriber;

import java.util.List;

public class NetworkPlayerPrompterHandler {

    private Subscriber<ChannelIncomingData> eventSubscriber;
    private PlayerPrompter prompter;

    private final NetworkInternalEventBus networkInternalEventBus;
    private final NetworkExternalEventBus networkExternalEventBus;

    public void stop() {
        networkInternalEventBus.unsubscribe(DataReceived.class, eventSubscriber);
    }

    public NetworkPlayerPrompterHandler(NetworkInternalEventBus networkInternalEventBus, NetworkExternalEventBus networkExternalEventBus) {

        this.networkInternalEventBus = networkInternalEventBus;
        this.networkExternalEventBus = networkExternalEventBus;
        prompter = ConfigService.getInstance().getPlayerPrompter();

        eventSubscriber = new Subscriber<>((data) -> {

            if (!(data.getData() instanceof Event)) {
                return;
            }

            Event<?> event = (Event<?>) data.getData();

            if (event instanceof CardLiedOnTable) {
                CardLiedOnTable e = (CardLiedOnTable) event;
                Card card = e.getEventData();

                prompter.showCardOnTable(card);
            }
            else if(event instanceof PlayerCardsRefreshed) {
                PlayerCardsRefreshed e = (PlayerCardsRefreshed) event;
                List<Card> cards = e.getEventData();

                prompter.refreshCards(cards);
            }
            else if(event instanceof PlayerCardsShown) {
                PlayerCardsShown e = (PlayerCardsShown) event;
                List<Integer> eligibleCards = e.getEventData();

                prompter.showPlayerCards(eligibleCards);
            }
            else if(event instanceof PlayerTurnExpected) {
                Card c = prompter.selectCard();
                networkExternalEventBus.emit(new CardSelected(c));
            }
            else if(event instanceof ColorChooseExpected) {
                CardColor c = prompter.selectCardColor();
                networkExternalEventBus.emit(new ColorSelected(c));
            }

        });

        networkInternalEventBus.on(DataReceived.class, eventSubscriber);
    }
}
