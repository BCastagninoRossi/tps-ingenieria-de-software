package uno;

/**
 * Excepción base para errores en el juego UNO.
 */
public abstract class UnoGameException extends RuntimeException {
    public UnoGameException(String message) {
        super(message);
    }
}

/**
 * Excepción lanzada cuando se intenta jugar una carta que no coincide con la carta superior.
 */
class CardDoesNotMatchException extends UnoGameException {
    private final Card attemptedCard;
    private final Card topCard;
    
    public CardDoesNotMatchException(Card attemptedCard, Card topCard) {
        super("Card does not match top card");
        this.attemptedCard = attemptedCard;
        this.topCard = topCard;
    }
    
    public Card getAttemptedCard() {
        return attemptedCard;
    }
    
    public Card getTopCard() {
        return topCard;
    }
}

/**
 * Excepción lanzada cuando un jugador intenta jugar una carta que no tiene en su mano.
 */
class PlayerDoesNotHaveCardException extends UnoGameException {
    private final Player player;
    private final Card card;
    
    public PlayerDoesNotHaveCardException(Player player, Card card) {
        super("Player does not have this card");
        this.player = player;
        this.card = card;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Card getCard() {
        return card;
    }
}