package uno;
/**
 * Clase abstracta que representa una carta de UNO.
 */
public abstract class Card {
    /**
     * Devuelve el color de la carta (puede ser null para Wild).
     */
    public abstract String color();

    /**
     * Indica si la carta que se intenta jugar ('this') puede jugarse sobre la carta dada en la pila de descarte.
     * Utiliza doble despacho.
     * @param otherCardOnDiscardPile La carta que está actualmente en la cima de la pila de descarte.
     * @return true si 'this' (la carta que se juega) es compatible con 'otherCardOnDiscardPile'.
     */
    public boolean matches(Card otherCardOnDiscardPile) {
        // 'this' es la carta que se intenta jugar.
        // 'otherCardOnDiscardPile' es la carta en la cima de la pila de descarte.
        // Delega a la carta en la pila para que determine si la carta actual ('this') puede jugarse sobre ella.
        return otherCardOnDiscardPile.canBePlayedUponBy(this);
    }

    /**
     * Método para el doble despacho: Determina si la 'playingCard' puede jugarse sobre 'this' (carta en la pila).
     * @param playingCard La carta que se intenta jugar.
     * @return true si 'playingCard' puede jugarse sobre 'this'.
     */
    public abstract boolean canBePlayedUponBy(Card playingCard);

    // Métodos específicos de 'matches' para el doble despacho.
    // 'this' es la carta que se intenta jugar.
    // El argumento es la carta en la cima de la pila de descarte.
    public abstract boolean matchesSpecific(NumberCard topNumberCard);
    public abstract boolean matchesSpecific(DrawTwoCard topDrawTwoCard);
    public abstract boolean matchesSpecific(ReverseCard topReverseCard);
    public abstract boolean matchesSpecific(SkipCard topSkipCard);
    public abstract boolean matchesSpecific(WildCard topWildCard);

    /**
     * Aplica el efecto especial de la carta sobre el juego.
     * Por defecto, no hace nada (para cartas numéricas).
     */
    public abstract void applyEffect(UnoGame game);
    /**
     * Indica si la carta hace avanzar el turno automáticamente.
     * Por defecto, true (para cartas normales y Wild).
     */
    public boolean avanzaTurno() {
        return true;
    }
    /**
     * Jugar la carta (sin color extra).
     */
    public abstract void play(UnoGame game, Player player);
}