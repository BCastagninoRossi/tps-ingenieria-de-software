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
    public boolean canBePlayedUponBy(Card playingCard) {
        return playingCard.matchesSpecific(this);
    }

    @Override
    public boolean matchesSpecific(NumberCard topNumberCard) {
        return topNumberCard.color() != null && topNumberCard.color().equals(this.color);
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
        // Un Skip puede jugarse sobre otro Skip o si coincide el color.
        return topSkipCard.color() != null && topSkipCard.color().equals(this.color);
    }

    @Override
    public boolean matchesSpecific(WildCard topWildCard) {
        return topWildCard.color() == null || topWildCard.color().equals(this.color);
    }

    @Override
    public void applyEffect(UnoGame game) {
        game.skipEffect();
    }
    @Override
    public boolean avanzaTurno() {
        return false;
    }
    @Override
    public void play(UnoGame game, Player player) {
        applyEffect(game);
    }
}