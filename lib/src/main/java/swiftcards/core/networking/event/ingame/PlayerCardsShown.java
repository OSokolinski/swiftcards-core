package swiftcards.core.networking.event.ingame;

import swiftcards.core.networking.NetworkEventBase;

import java.util.List;

public class PlayerCardsShown extends NetworkEventBase<List<Integer>> {
    public PlayerCardsShown(List<Integer> cards) {
        super(cards);
    }
}
