package uno;
/**
 * Carta Draw Two de UNO.
 */
public class DrawTwoCard extends Card {
    private final String color;
    public DrawTwoCard(String color) { this.color = color; }
    @Override
    public String color() { return color; }
    @Override
    public boolean matches(Card other) {
        return (other instanceof DrawTwoCard) || (other.color() != null && other.color().equals(this.color));
    }
    @Override
    public void applyEffect(UnoGame game) {
        game.drawTwoEffect();
    }
    @Override
    public boolean avanzaTurno() {
        return false;
    }
}