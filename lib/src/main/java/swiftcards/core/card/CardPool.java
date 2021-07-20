package swiftcards.core.card;

import java.io.Serializable;
import java.util.List;

public interface CardPool extends Serializable {

    /**
     * @return List of cards
     */
    List<Card> getCards();
}
