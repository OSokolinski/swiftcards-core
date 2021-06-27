package swiftcards.core.player;

import swiftcards.core.card.Card;
import swiftcards.core.card.GeneralCardPool;
import swiftcards.core.card.PlayerCardPool;

public abstract class PlayerBase implements Player {

    protected Boolean hasFinishedPlay;
    protected final PlayerCardPool playerCardPool;
    protected int id;
    protected String displayName;

    /**
     * Initiating the player.
     */
    protected PlayerBase() {
        playerCardPool = new PlayerCardPool();
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public PlayerCardPool getCardPool() {
        return playerCardPool;
    }

    @Override
    public Player apply(int newId) {
        id = newId;

        return this;
    }

    /**
     * @param value Custom information, if player has finished the game
     */
    public void setHasFinishedPlay(boolean value) {
        hasFinishedPlay = value;
    }

    /**
     * @param name Player display name
     */
    public void setDisplayName(String name) {
        displayName = name;
    }

    @Override
    public boolean hasFinishedPlay() {

        if (hasFinishedPlay != null) {
            return hasFinishedPlay;
        }

        return playerCardPool.getCards().size() < 1;
    }

    @Override
    public void pullPenaltyCards(GeneralCardPool cardPool, int amount) {
        for (int i = 0; i < amount; i++) {
            playerCardPool.getCards().add(cardPool.pullNext());
        }
    }

}
