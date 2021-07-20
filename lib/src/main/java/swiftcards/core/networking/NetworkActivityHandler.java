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
import swiftcards.core.util.Subscriber;

public class NetworkActivityHandler {

    Subscriber<ChannelIncomingData> onActivityReceived;

    public NetworkActivityHandler() {

        onActivityReceived = new Subscriber<>((rvdData) -> {

            if (!(rvdData.getData() instanceof Activity)) {
                return;
            }

            Activity<?> activity = (Activity<?>) rvdData.getData();

            if (activity instanceof CardLiedOnTable) {

                CardLiedOnTable a = (CardLiedOnTable) activity;
                ActivitySubject<CardLiedByPlayer> activitySubject = a.getActivitySubject();
                CardLiedByPlayer cardLiedByPlayer = activitySubject.getSubject();

                System.out.printf("Card lied on table: %s by player %d%n", cardLiedByPlayer.getCard(), cardLiedByPlayer.getPlayerId());
            }
            else if (activity instanceof CardColorChosen) {

                CardColorChosen a = (CardColorChosen) activity;
                ActivitySubject<CardColor> activitySubject = a.getActivitySubject();
                CardColor cardColor = activitySubject.getSubject();

                System.out.printf("Card color chosen: %s%n", cardColor);
            }
            else if (activity instanceof CardsPulledFromPool) {

                CardsPulledFromPool a = (CardsPulledFromPool) activity;
                ActivitySubject<Integer> activitySubject = a.getActivitySubject();
                int cardAmount = activitySubject.getSubject();

                System.out.printf("Cards pulled by player: %d%n", cardAmount);
            }
            else if (activity instanceof PlayerStopped) {

                PlayerStopped a = (PlayerStopped) activity;
                ActivitySubject<Integer> activitySubject = a.getActivitySubject();
                int playerId = activitySubject.getSubject();

                System.out.printf("Player %d has been stopped%n", playerId);
            }
            else if (activity instanceof PlayerTurnPending) {

                PlayerTurnPending a = (PlayerTurnPending) activity;
                ActivitySubject<Integer> activitySubject = a.getActivitySubject();
                int playerId = activitySubject.getSubject();

                System.out.printf("Player %d makes turn stopped%n", playerId);
            }
            else if (activity instanceof CardSetOnTable) {

                CardSetOnTable a = (CardSetOnTable) activity;
                ActivitySubject<Card> activitySubject = a.getActivitySubject();
                Card card = activitySubject.getSubject();

                System.out.printf("Card %s has been set on the table%n", card);
            }
            else if (activity instanceof CardsToTakeIncreased) {

                CardsToTakeIncreased a = (CardsToTakeIncreased) activity;
                ActivitySubject<Integer> activitySubject = a.getActivitySubject();
                int cardsToTake = activitySubject.getSubject();

                System.out.printf("Cards to take has been increased to %d%n", cardsToTake);
            }
            else if (activity instanceof GameQueueSequenceReverted) {
                System.out.println("Game queue has been reverted");
            }

        });


        NetworkInternalEventBus.getInstance().on(DataReceived.class, onActivityReceived);
    }
}
