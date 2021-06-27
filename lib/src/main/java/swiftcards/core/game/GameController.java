package swiftcards.core.game;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import swiftcards.core.card.*;
import swiftcards.core.card.GeneralCardPool.UnableToGenerateCardPoolException;
import swiftcards.core.game.activities.CardSetOnTable;
import swiftcards.core.game.activities.CardsToTakeIncreased;
import swiftcards.core.game.activities.GameQueueSequenceReverted;
import swiftcards.core.player.Player;
import swiftcards.core.player.activities.*;

public class GameController {

    private ArrayList<Player> players;
    private GeneralCardPool mainCardPool;
    private PlayerIterator playerIterator;
    private ActivityPropagator activityPropagator;
    private int turnCounter = 0;

    private final int initialCardCount = 7;
    private int turnLimit = -1;
    private boolean keepLogging;
    private int sleepBetweenTurns;

    public GameController() {

        players = new ArrayList<>();
        mainCardPool = null;
        keepLogging = false;
        sleepBetweenTurns = 0;
    }

    public GameController(boolean enableLogging, int sleep) {
        this();
        keepLogging = enableLogging;
        sleepBetweenTurns = sleep;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Starts the game
     *
     * @throws UnableToGenerateCardPoolException Thrown, when card pool cannot be generated
     */
    public void startGame() throws UnableToGenerateCardPoolException, InterruptedException {

        mainCardPool = GeneralCardPool
            .withDefaultConfiguration()
            .generate();

        supplyPlayerCards();
        playerIterator = new PlayerIterator(players);
        manageGame();
    }

    public void setTurnLimit(int limit) {
        turnLimit = limit;
    }

    /**
     * Manages game sequence
     */
    private void manageGame() throws InterruptedException {

        Card cardOnTable = mainCardPool.pullNext();
        boolean isGamePending = playerIterator.getPlayersInGameCount() > 1;

        while (isGamePending) {

            // **** Breaking game, when conditions are filled ****

            isGamePending = playerIterator.getPlayersInGameCount() > 1
                && (turnLimit == -1 || turnLimit > turnCounter);

            if (!isGamePending) {
                break;
            }

            // ****

            log(String.format("Card on table: %s", cardOnTable.toString()));
            activityPropagator.propagate(new CardSetOnTable(cardOnTable));

            Player player = playerIterator.getPlayer();
            activityPropagator.propagate(new PlayerTurnPending(player.getId()));
            listPlayerCards(player);

            Card setCard = player.layCard(cardOnTable);
            activityPropagator.propagate(new CardLiedOnTable(setCard, player.getId()));

            if (setCard == null) {
                if (!(cardOnTable instanceof FunctionalCard) || !((FunctionalCard) cardOnTable).isAffective()) {
                    player.pullCard(mainCardPool);
                    setCard = player.layCard(cardOnTable);
                    activityPropagator.propagate(new CardLiedOnTable(setCard, player.getId()));
                }
            }

            if (setCard != null) {

                // Increasing card to take amount when PenaltyPullingCard is lied down on the same active card
                if (cardOnTable instanceof PenaltyPullingCard
                    && cardOnTable.getClass().equals(setCard.getClass())
                    && ((FunctionalCard) cardOnTable).isAffective()) {

                    ((PenaltyPullingCard) setCard).increaseCardsToPullAmount(((PenaltyPullingCard) cardOnTable).getCardToPullAmount());
                    activityPropagator.propagate(new CardsToTakeIncreased(((PenaltyPullingCard) setCard).getCardToPullAmount()));
                }


                mainCardPool.recycleCard(cardOnTable);

                cardOnTable = setCard;

                if (cardOnTable instanceof ColorChangingCard) {
                    CardColor chosenColor = player.choosePoolColor();
                    ((ColorChangingCard) cardOnTable).chooseColor(chosenColor);
                    activityPropagator.propagate(new CardColorChosen(chosenColor));

                }
                else if (cardOnTable instanceof FunctionalStopCard) {
                    playerIterator.stopNextPlayer();
                    ((FunctionalStopCard) cardOnTable).setIsAffective(false);
                    activityPropagator.propagate(new PlayerStopped(playerIterator.getStoppedPlayer()));
                }
                else if (cardOnTable instanceof FunctionalReturnCard) {
                    playerIterator.turnBackSequence();
                    ((FunctionalReturnCard) cardOnTable).setIsAffective(false);
                    activityPropagator.propagate(new GameQueueSequenceReverted());
                }
            }
            else {
                if (cardOnTable instanceof FunctionalCard && ((FunctionalCard) cardOnTable).isAffective()) {

                    if (cardOnTable instanceof PenaltyPullingCard) {
                        int cardToPullAmount = ((PenaltyPullingCard) cardOnTable).getCardToPullAmount();
                        player.pullPenaltyCards(mainCardPool, cardToPullAmount);
                        activityPropagator.propagate(new CardsPulledFromPool(cardToPullAmount));
                    }

                    // Deactivating previous functional card
                    ((FunctionalCard) cardOnTable).setIsAffective(false);
                }
            }

            turnCounter++;

            if (sleepBetweenTurns != 0) {
                TimeUnit.MILLISECONDS.sleep(sleepBetweenTurns);
            }
        }
    }

    /**
     * Supplies player with starting cards
     */
    private void supplyPlayerCards() {

        for (Player player : players) {

            for (int i = 0; i < initialCardCount; i++) {

                player.getCardPool().getCards().add(mainCardPool.pullNext());
            }

        }
    }

    private void listPlayerCards(Player player) {

        StringBuilder cardStr = new StringBuilder();

        for (Card c : player.getCardPool().getCards()) {
            cardStr.append(cardStr.length() < 1 ? "" : " ").append(c.toString());
        }

        log(String.format("[Player %d]: %s", player.getId(), cardStr.toString()));
    }

    private void log(String logText) {
        if (keepLogging) {
            System.out.printf("%s.%n", logText);
        }
    }
}
