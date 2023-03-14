package swiftcards.core.networking;

import swiftcards.core.card.Card;
import swiftcards.core.card.CardColor;
import swiftcards.core.game.Activity;
import swiftcards.core.game.Activity.*;
import swiftcards.core.game.activities.CardSetOnTable;
import swiftcards.core.game.activities.CardsToTakeIncreased;
import swiftcards.core.game.activities.GameQueueSequenceReverted;
import swiftcards.core.networking.event.DataReceived;
import swiftcards.core.player.PlayerActivity;
import swiftcards.core.player.activities.*;
import swiftcards.core.player.activities.CardLiedOnTable.*;
import swiftcards.core.util.ConfigService;
import swiftcards.core.util.Subscriber;

public class NetworkActivityHandler {

    private final Subscriber<ChannelIncomingData> onActivityReceived;
    private final NetworkInternalEventBus networkInternalEventBus;

    public void stop() {
        networkInternalEventBus.unsubscribe(DataReceived.class, onActivityReceived);
    }

    public NetworkActivityHandler(NetworkInternalEventBus networkInternalEventBus) {

        NetworkActivityPresenter activityPresenter = ConfigService.getInstance().getNetworkActivityPresenter();
        this.networkInternalEventBus = networkInternalEventBus;

        onActivityReceived = new Subscriber<>((rvdData) -> {

            if (!(rvdData.getData() instanceof Activity)) {
                return;
            }

            Activity<?> activity = (Activity<?>) rvdData.getData();

            if (activity instanceof CardLiedOnTable) {

                CardLiedOnTable a = (CardLiedOnTable) activity;
                ActivitySubject<CardLiedByPlayer> activitySubject = a.getActivitySubject();
                CardLiedByPlayer cardLiedByPlayer = activitySubject.getSubject();
                Card card = cardLiedByPlayer.getCard();

                activityPresenter.cardLiedOnTableByPlayer(card);
            }
            else if (activity instanceof CardColorChosen) {

                CardColorChosen a = (CardColorChosen) activity;
                ActivitySubject<CardColor> activitySubject = a.getActivitySubject();
                CardColor cardColor = activitySubject.getSubject();

                activityPresenter.cardColorChosen(cardColor);
            }
            else if (activity instanceof CardsPulledFromPool) {

                CardsPulledFromPool a = (CardsPulledFromPool) activity;
                ActivitySubject<Integer> activitySubject = a.getActivitySubject();
                int cardAmount = activitySubject.getSubject();

                activityPresenter.cardsPulledByPlayer(cardAmount);
            }
            else if (activity instanceof PlayerStopped) {

                PlayerStopped a = (PlayerStopped) activity;
                ActivitySubject<Integer> activitySubject = a.getActivitySubject();
                int playerId = activitySubject.getSubject();

                activityPresenter.playerStopped(playerId);
            }
            else if (activity instanceof PlayerTurnPending) {

                PlayerTurnPending a = (PlayerTurnPending) activity;
                ActivitySubject<Integer> activitySubject = a.getActivitySubject();
                int playerId = activitySubject.getSubject();

                activityPresenter.playerTurdStarted(playerId);
            }
            else if (activity instanceof CardSetOnTable) {

                CardSetOnTable a = (CardSetOnTable) activity;
                ActivitySubject<Card> activitySubject = a.getActivitySubject();
                Card card = activitySubject.getSubject();

                activityPresenter.initialCardSetOnTable(card);
            }
            else if (activity instanceof CardsToTakeIncreased) {

                CardsToTakeIncreased a = (CardsToTakeIncreased) activity;
                ActivitySubject<Integer> activitySubject = a.getActivitySubject();
                int cardsToTake = activitySubject.getSubject();

                activityPresenter.cardsToTakeIncreased(cardsToTake);
            }
            else if (activity instanceof GameQueueSequenceReverted) {
                activityPresenter.gameQueueSequenceReverted();
            }

        });

        this.networkInternalEventBus.on(DataReceived.class, onActivityReceived);
    }
}
