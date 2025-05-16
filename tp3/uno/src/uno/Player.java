package uno;
import java.util.*;
/**
 * Representa un jugador de UNO.
 */
public class Player {
    private final String name;
    private final List<Card> hand = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public List<Card> hand() {
        return Collections.unmodifiableList(hand);
    }

    public void receiveCard(Card card) {
        hand.add(card);
    }

    public void setHand(List<Card> cards) {
        hand.clear();
        hand.addAll(cards);
    }

    public void giveOneCard(Card card) {
        hand.clear();
        hand.add(card);
    }

    public void playCard(Card card, UnoGame game) {
        if (!hand.contains(card)) {
            throw new IllegalArgumentException("Player does not have this card");
        }
        if (!card.matches(game.topCard())) {
            throw new IllegalArgumentException("Card does not match top card");
        }
        hand.remove(card);
        game.setTopCard(card);
        card.applyEffect(game);
    }

    public void playWildCard(WildCard card, UnoGame game, String color) {
        if (!hand.contains(card)) {
            throw new IllegalArgumentException("Player does not have this card");
        }
        hand.remove(card);
        card.chooseColor(color);
        game.setTopCard(card);
    }

    public boolean hasNoCards() {
        return hand.isEmpty();
    }
}
