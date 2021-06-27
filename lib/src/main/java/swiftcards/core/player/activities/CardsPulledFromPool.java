package swiftcards.core.player.activities;

import swiftcards.core.player.PlayerActivity;

public class CardsPulledFromPool extends PlayerActivity<Integer> {
    public CardsPulledFromPool(int amount) {
        super(amount);
    }
}
