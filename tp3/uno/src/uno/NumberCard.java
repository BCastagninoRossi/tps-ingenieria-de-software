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
    public boolean canBePlayedUponBy(Card playingCard) {
        return playingCard.matchesSpecific(this);
    }

    @Override
    public boolean matchesSpecific(NumberCard topNumberCard) {
        return topNumberCard.number == this.number || (topNumberCard.color() != null && topNumberCard.color().equals(this.color));
    }

    @Override
    public boolean matchesSpecific(DrawTwoCard topDrawTwoCard) {
        return topDrawTwoCard.color() != null && topDrawTwoCard.color().equals(this.color);
    }

    @Override
    public boolean matchesSpecific(ReverseCard topReverseCard) {
        return topReverseCard.color() != null && topReverseCard.color().equals(this.color);
    }

    @Override
    public boolean matchesSpecific(SkipCard topSkipCard) {
        return topSkipCard.color() != null && topSkipCard.color().equals(this.color);
    }

    @Override
    public boolean matchesSpecific(WildCard topWildCard) {
        // Una carta numérica siempre puede jugarse sobre una WildCard si el color elegido coincide.
        // O si la WildCard aún no ha elegido color (aunque esto no debería pasar en el flujo normal del juego si la WildCard está en la pila).
        return topWildCard.color() == null || topWildCard.color().equals(this.color);
    }

    @Override
    public void applyEffect(UnoGame game) {
        // Las cartas numéricas no tienen efecto especial
    }

    @Override
    public void play(UnoGame game, Player player) {
        applyEffect(game); // NumberCard no tiene efecto, pero la llamada está por consistencia.
    }
}