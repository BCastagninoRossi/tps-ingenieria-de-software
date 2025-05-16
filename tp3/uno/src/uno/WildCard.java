package uno;
/**
 * Carta Wild de UNO.
 */
public class WildCard extends Card {
    private String chosenColor = null;
    public WildCard() {}
    public WildCard(String chosenColor) { this.chosenColor = chosenColor; }
    @Override
    public String color() { return chosenColor; }
    public void chooseColor(String color) { this.chosenColor = color; }
    @Override
    public boolean matches(Card other) { return true; }
    @Override
    public void applyEffect(UnoGame game) {
        // El efecto de WildCard se maneja en playCard con color elegido
    }
    @Override
    public void play(UnoGame game, Player player, String color) {
        player.playWildCard(this, game, color);
    }
}