package uno;
/**
 * Carta Wild de UNO.
 */
public class WildCard extends Card {
    private CardColor chosenColor = CardColor.NoColor.getInstance(); // Inicializado con el Null Object
    
    public WildCard() {}
    
    public WildCard(String chosenColorValue) { 
        if (chosenColorValue != null) {
            this.chosenColor = new CardColor.ConcreteColor(chosenColorValue);
        }
    }
    
    @Override
    public String color() { 
        return chosenColor.getValue(); // Delegamos al objeto CardColor
    }
    
    public void chooseColor(String color) { 
        this.chosenColor = new CardColor.ConcreteColor(color);
    }

    @Override
    public boolean canBePlayedUponBy(Card playingCard) {
        // Una WildCard (que está en la pila) permite que cualquier carta se juegue sobre ella,
        // siempre y cuando la carta que se juega coincida con el color elegido de la WildCard (si tiene uno).
        // La lógica de "matches" de la carta que se juega se encargará de esto.
        return playingCard.matchesSpecific(this);
    }

    // 'this' es la WildCard que se intenta jugar.
    // El argumento es la carta en la cima de la pila de descarte.
    @Override
    public boolean matchesSpecific(NumberCard topNumberCard) {
        return true; // Una WildCard siempre puede jugarse sobre una NumberCard.
    }

    @Override
    public boolean matchesSpecific(DrawTwoCard topDrawTwoCard) {
        return true; // Una WildCard siempre puede jugarse sobre una DrawTwoCard.
    }

    @Override
    public boolean matchesSpecific(ReverseCard topReverseCard) {
        return true; // Una WildCard siempre puede jugarse sobre una ReverseCard.
    }

    @Override
    public boolean matchesSpecific(SkipCard topSkipCard) {
        return true; // Una WildCard siempre puede jugarse sobre una SkipCard.
    }

    @Override
    public boolean matchesSpecific(WildCard topWildCard) {
        return true; // Una WildCard siempre puede jugarse sobre otra WildCard.
    }


    @Override
    public void applyEffect(UnoGame game) {
        // El efecto de WildCard se maneja en playCard con color elegido
    }
    @Override
    public void play(UnoGame game, Player player) {
        // No necesitamos verificar si chosenColor es null gracias al patrón Null Object
        // Al llamar a color() se verifica implícitamente, ya que NoColor.getValue() lanzará la excepción
        color(); // Esto lanzará la excepción apropiada si no se ha elegido un color
        
        // El "efecto" de la WildCard (cambiar el color de juego) 
        // se logra porque esta carta (con su chosenColor) se convierte en la topCard.
        applyEffect(game); // Llama al applyEffect de WildCard que está vacío.
    }
}