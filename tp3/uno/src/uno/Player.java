package uno;
import java.util.*;
/**
 * Representa un jugador de UNO.
 */
public class Player {
    private final String name;
    private final List<Card> hand = new ArrayList<>();
    private boolean unoDeclared = false;

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
        if (hand.size() > 1) {
            resetUnoDeclaration();
        }
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
            throw new PlayerDoesNotHaveCardException(this, card);
        }
        if (!card.matches(game.topCard())) {
            throw new CardDoesNotMatchException(card, game.topCard());
        }
        
        // Si es una WildCard, se asume que su color ya fue seteado antes de llamar a este método.
        // Por ejemplo, a través de un wildCard.chooseColor("Red") ANTES de game.playCard(...).

        hand.remove(card);
        game.setTopCard(card); // La carta jugada, con su color (si es Wild), es ahora la topCard.
        
        card.play(game, this); // La carta aplica su efecto (si lo tiene).

        if (hand.size() > 1) {
            this.unoDeclared = false;
        }
    }

    public boolean hasNoCards() {
        return hand.isEmpty();
    }

    public void declareUno() {
        this.unoDeclared = true;
    }

    public boolean hasDeclaredUno() {
        return this.unoDeclared;
    }

    public void resetUnoDeclaration() {
        this.unoDeclared = false;
    }
}
