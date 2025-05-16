package uno;
/**
 * Carta numérica de UNO.
 */
public class NumberCard extends Card {
    private final String color;
    private final int number;
    public NumberCard(String color, int number) {
        this.color = color;
        this.number = number;
    }
    @Override
    public String color() { return color; }
    public int number() { return number; }
    @Override
    public boolean matches(Card other) {
        return (other instanceof NumberCard && ((NumberCard) other).number == this.number) ||
               (other.color() != null && other.color().equals(this.color));
    }
    @Override
    public void applyEffect(UnoGame game) {
        // Las cartas numéricas no tienen efecto especial
    }
}