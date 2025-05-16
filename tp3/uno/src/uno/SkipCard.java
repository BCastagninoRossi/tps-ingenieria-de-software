package uno;
/**
 * Carta Skip de UNO.
 */
public class SkipCard extends Card {
    private final String color;
    public SkipCard(String color) { this.color = color; }
    @Override
    public String color() { return color; }
    @Override
    public boolean matches(Card other) {
        return (other instanceof SkipCard) || (other.color() != null && other.color().equals(this.color));
    }
    @Override
    public void applyEffect(UnoGame game) {
        game.skipEffect();
    }
    @Override
    public boolean avanzaTurno() {
        return false;
    }
}