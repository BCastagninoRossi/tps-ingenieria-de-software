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
     * Indica si la carta puede jugarse sobre la carta dada.
     */
    public abstract boolean matches(Card other);
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
     * Jugar la carta con color (por defecto, ignora el color).
     */
    public void play(UnoGame game, Player player, String color) {
        play(game, player);
    }
    /**
     * Jugar la carta (sin color extra).
     */
    public void play(UnoGame game, Player player) {
        player.playCard(this, game);
    }
}