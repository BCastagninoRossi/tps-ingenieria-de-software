package uno;
/**
 * Carta Reverse de UNO.
 */
public class ReverseCard extends Card {
    private final String color;
    public ReverseCard(String color) { this.color = color; }
    @Override
    public String color() { return color; }
    @Override
    public boolean matches(Card other) {
        return (other instanceof ReverseCard) || (other.color() != null && other.color().equals(this.color));
    }
    @Override
    public void applyEffect(UnoGame game) {
        game.reverseEffect();
    }
    @Override
    public boolean avanzaTurno() {
        return false;
    }
}