package swiftcards.core.game;

import swiftcards.core.card.Card;
import swiftcards.core.card.CardColor;
import swiftcards.core.game.activities.CardSetOnTable;
import swiftcards.core.game.activities.CardsToTakeIncreased;
import swiftcards.core.game.activities.GameFinished;
import swiftcards.core.game.activities.GameQueueSequenceReverted;
import swiftcards.core.networking.ActivityPresenter;
import swiftcards.core.player.activities.*;
import swiftcards.core.util.ConfigService;

public class ActivityHandler {

    public void handle(Activity<?> activity) {
        ActivityPresenter activityPresenter = ConfigService.getInstance().getNetworkActivityPresenter();

        if (activity instanceof CardLiedOnTable) {

            CardLiedOnTable a = (CardLiedOnTable) activity;
            Activity.ActivitySubject<CardLiedOnTable.CardLiedByPlayer> activitySubject = a.getActivitySubject();
            CardLiedOnTable.CardLiedByPlayer cardLiedByPlayer = activitySubject.getSubject();
            Card card = cardLiedByPlayer.getCard();

            activityPresenter.cardLiedOnTableByPlayer(card);
        }
        else if (activity instanceof CardColorChosen) {

            CardColorChosen a = (CardColorChosen) activity;
            Activity.ActivitySubject<CardColor> activitySubject = a.getActivitySubject();
            CardColor cardColor = activitySubject.getSubject();

            activityPresenter.cardColorChosen(cardColor);
        }
        else if (activity instanceof CardsPulledFromPool) {

            CardsPulledFromPool a = (CardsPulledFromPool) activity;
            Activity.ActivitySubject<CardsPulledFromPool.CardsPulledByPlayer> activitySubject = a.getActivitySubject();
            CardsPulledFromPool.CardsPulledByPlayer cardsPulledByPlayer = activitySubject.getSubject();
            int cardAmount = cardsPulledByPlayer.getAmount();
            int playerId = cardsPulledByPlayer.getPlayerId();

            activityPresenter.cardsPulledByPlayer(cardAmount, playerId);
        }
        else if (activity instanceof PlayerStopped) {

            PlayerStopped a = (PlayerStopped) activity;
            Activity.ActivitySubject<Integer> activitySubject = a.getActivitySubject();
            int playerId = activitySubject.getSubject();

            activityPresenter.playerStopped(playerId);
        }
        else if (activity instanceof PlayerTurnPending) {

            PlayerTurnPending a = (PlayerTurnPending) activity;
            Activity.ActivitySubject<Integer> activitySubject = a.getActivitySubject();
            int playerId = activitySubject.getSubject();

            activityPresenter.playerTurnStarted(playerId);
        }
        else if (activity instanceof CardSetOnTable) {

            CardSetOnTable a = (CardSetOnTable) activity;
            Activity.ActivitySubject<Card> activitySubject = a.getActivitySubject();
            Card card = activitySubject.getSubject();

            activityPresenter.initialCardSetOnTable(card);
        }
        else if (activity instanceof CardsToTakeIncreased) {

            CardsToTakeIncreased a = (CardsToTakeIncreased) activity;
            Activity.ActivitySubject<Integer> activitySubject = a.getActivitySubject();
            int cardsToTake = activitySubject.getSubject();

            activityPresenter.cardsToTakeIncreased(cardsToTake);
        }
        else if (activity instanceof GameQueueSequenceReverted) {
            activityPresenter.gameQueueSequenceReverted();
        }
        else if (activity instanceof PlayerStatusUpdated) {

            PlayerStatusUpdated a = (PlayerStatusUpdated) activity;
            Activity.ActivitySubject<PlayerStatusUpdated.PlayerStatusData> activitySubject = a.getActivitySubject();
            PlayerStatusUpdated.PlayerStatusData playerStatusData = activitySubject.getSubject();

            activityPresenter.playerStatusUpdated(playerStatusData);
        }
        else if (activity instanceof GameFinished) {

            GameFinished a = (GameFinished) activity;
            Activity.ActivitySubject<Summary> activitySubject = a.getActivitySubject();
            Summary summary = activitySubject.getSubject();

            activityPresenter.gameFinished(summary);
        }
    }
}
