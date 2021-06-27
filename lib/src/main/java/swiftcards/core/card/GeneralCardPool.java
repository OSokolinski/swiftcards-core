package swiftcards.core.card;

import java.util.*;
import java.util.function.Consumer;

public class GeneralCardPool implements CardPool {

    private List<Card> cards;
    private List<Card> recycledCards;
    private Consumer<HashMap<Card, Integer>> customCardConfiguration;
    private int cardIdIterator;

    /**
     * Initiating general card pool.
     */
    private GeneralCardPool() {
        customCardConfiguration = null;
        cards = new LinkedList<>();
        recycledCards = new ArrayList<>();
        cardIdIterator = 0;
    }

    /**
     * Getting another card from the main pool.
     * After returning the card, it will be removed from the list.
     * If main pool is empty, recycled cards are going to be re-shuffled and moved to the main pool.
     *
     * @return Next card
     */
    public Card pullNext() {

        if (cards.size() < 1) {

            Collections.shuffle(recycledCards);

            cards = new LinkedList<>(recycledCards);
            recycledCards = new ArrayList<>();
        }

        Card pulledCard = cards.get(0);
        cards.remove(0);

        return pulledCard;
    }

    /**
     * Adding the card to recycled card pool. Those cards are going to be reused and re-shuffled, after the main pool
     * is run out of cards. Card-to-take amount of PenaltyPullingCard will be set to its default value.
     *
     * @param recycleCard Card to lay on recycled card pool
     */
    public void recycleCard(Card recycleCard) {

        if (recycleCard instanceof PenaltyPullingCard) {
            ((PenaltyPullingCard) recycleCard).resetCardsToPullAmount();
        }

        recycledCards.add(recycleCard);
    }

    @Override
    public List<Card> getCards() {
        return cards;
    }

    /**
     * Using default card configuration.
     *
     * @return Instance of GeneralCardPool
     */
    public static GeneralCardPool withDefaultConfiguration() {

        return new GeneralCardPool();
    }

    /**
     * Using custom card configuration.
     *
     * @param configurationLambda Lambda setting card configuration. Key of the HashMap is type of card, and value is
     * is an amount of cards, that are expected to be generated
     * @return Instance of GeneralCardPool
     */
    public static GeneralCardPool withCustomConfiguration(Consumer<HashMap<Card, Integer>> configurationLambda) {

        GeneralCardPool cardSet = new GeneralCardPool();
        cardSet.customCardConfiguration = configurationLambda;

        return cardSet;
    }

    /**
     * Generates general card set. Card sequence is going to be mixed.
     *
     * @throws UnableToGenerateCardPoolException Thrown, when card set could not be generated
     */
    public GeneralCardPool generate() throws UnableToGenerateCardPoolException {

        HashMap<Card, Integer> cardPrototypes;

        if (customCardConfiguration == null) {
            cardPrototypes = getCardPrototypeMap();
        }
        else {
            cardPrototypes = new HashMap<>();
            customCardConfiguration.accept(cardPrototypes);
        }

        generateInitialCardSet(cardPrototypes);
        Collections.shuffle(cards);

        return this;
    }

    /**
     * Generates default card set.
     *
     * @param cardPrototypes Hash map of prototypes to generate
     * @throws UnableToGenerateCardPoolException Thrown, when card set could not be generated
     */
    private void generateInitialCardSet(HashMap<Card, Integer> cardPrototypes) throws UnableToGenerateCardPoolException {

        for (Map.Entry<Card, Integer> prototypeEntry: cardPrototypes.entrySet()) {

            int amount = prototypeEntry.getValue();
            Card cardPrototype = prototypeEntry.getKey();

            for (int i = 0; i < amount; i++) {

                try {
                    cards.add(cardPrototype.clone().apply(cardIdIterator));
                }
                catch (CloneNotSupportedException e) {
                    throw new UnableToGenerateCardPoolException("General card set could not be generated," +
                            " due prototype to cloning exception.");
                }

                cardIdIterator++;
            }
        }
    }

    /**
     * Generates default card set.
     *
     * @return Prototype hash map
     */
    private HashMap<Card, Integer> getCardPrototypeMap() {

        HashMap<Card, Integer> prototypes = new HashMap<>();

        getDefaultStandardCardMap(prototypes);
        getDefaultFunctionalCardMap(prototypes);

        return prototypes;
    }

    /**
     * Gets default standard card configuration.
     * <br>
     * 0 -numbered cards amount: 1, 1-9 -numbered cards amount: 2.
     *
     * @param existingPrototypeMap Hash map of card prototypes
     */
    private void getDefaultStandardCardMap(HashMap<Card, Integer> existingPrototypeMap) {

        for (CardColor color : CardColor.values()) {

            for (int presentationIterator = 0; presentationIterator <= 9; presentationIterator++) {

                int copies = presentationIterator == 0 ? 1 : 2;
                StandardCard card = new StandardCard(color, presentationIterator);

                existingPrototypeMap.put(card, copies);
            }
        }
    }

    /**
     * Gets default functional card configuration.
     * <br>
     * 4 change color/take four cards; 2 stop/change direction/take two cards for each color.
     *
     * @param existingHashMap Hash map of card prototypes
     */
    private void getDefaultFunctionalCardMap(HashMap<Card, Integer> existingHashMap) {

        for (CardColor color : CardColor.values()) {

            FunctionalCard stopCard = new FunctionalStopCard(color);
            FunctionalCard changeDirectionCard = new FunctionalReturnCard(color);
            FunctionalCard plusTwoCard = new FunctionalPlusTwoCard(color);

            existingHashMap.put(stopCard, 2);
            existingHashMap.put(changeDirectionCard, 2);
            existingHashMap.put(plusTwoCard, 2);
        }

        FunctionalCard plusFourCard = new FunctionalPlusFourCard();
        FunctionalCard switchColorCard = new FunctionalSwitchColorCard();

        existingHashMap.put(plusFourCard, 4);
        existingHashMap.put(switchColorCard, 4);
    }

    public static class UnableToGenerateCardPoolException extends Exception {

        /**
         * Custom Exception. Indicates, that because of some reason, card set generation couldn't have been succeeded.
         */
        public UnableToGenerateCardPoolException(String errorMessage) {
            super(errorMessage);
        }
    }
}
