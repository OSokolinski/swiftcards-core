package swiftcards.core.player;

import swiftcards.core.card.Card;
import swiftcards.core.card.CardColor;
import swiftcards.core.card.GeneralCardPool;
import swiftcards.core.card.PlayerCardPool;

public interface Player {

    /**
     * @return Information, if player has finished the game
     */
    boolean hasFinishedPlay();

    /**
     * @param cardOnTable Covering card, that player should lay
     * @return Previous card, that player is expected to cover. Value might be NULL
     */
    Card layCard(Card cardOnTable);

    /**
     * @param cardPool GeneralCardPool, that player is going to pull a card from
     */
    void pullCard(GeneralCardPool cardPool);

    /**
     * @param cardPool GeneralCardPool, that player is going to pull cards from
     * @param amount Amount of cards, that player should pull
     */
    void pullPenaltyCards(GeneralCardPool cardPool, int amount);

    /**
     * @return Card pool of the player
     */
    PlayerCardPool getCardPool();

    /**
     * @return CardColor chosen by the player
     */
    CardColor choosePoolColor();

    /**
     * @return Player display name
     */
    String getDisplayName();

    /**
     * @return ID of the player
     */
    int getId();

    /**
     * Assigning unique ID to the player.
     *
     * @param newId Player ID
     * @return Player instance
     */
    Player apply(int newId);
}
