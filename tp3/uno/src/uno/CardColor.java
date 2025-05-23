package uno;

/**
 * Interfaz para representar un color en el juego UNO.
 */
public interface CardColor {
    String getValue();
    
    /**
     * Color nulo implementando el patrón Null Object.
     * Representa la ausencia de color elegido en las cartas Wild.
     */
    class NoColor implements CardColor {
        private static final NoColor INSTANCE = new NoColor();
        
        private NoColor() {}
        
        public static NoColor getInstance() {
            return INSTANCE;
        }
        
        @Override
        public String getValue() {
            throw new IllegalStateException("WildCard color must be chosen before playing.");
        }
    }
    
    /**
     * Implementación concreta para un color válido.
     */
    class ConcreteColor implements CardColor {
        private final String colorValue;
        
        public ConcreteColor(String colorValue) {
            this.colorValue = colorValue;
        }
        
        @Override
        public String getValue() {
            return colorValue;
        }
    }
}