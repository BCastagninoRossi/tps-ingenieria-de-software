package uno;
import java.util.*;

/**
 * Clase principal que representa una partida de UNO.
 */
public class UnoGame {
    private final Deque<Player> playersQueue = new ArrayDeque<>();
    private Card topCard;
    private PlayDirectionState directionState = new ForwardDirectionState();
    private GameState gameState = new InProgressState(); // Estado inicial: en curso


    private UnoGame() {}

    public static UnoGame withPlayers(String... playerNames) {
        UnoGame game = new UnoGame();
        for (String name : playerNames) {
            Player player = new Player(name);
            for (int i = 0; i < 7; i++) {
                // Dummy card - Si se cambia, va a haber que cambiar el test para que coincida la primera carta
                // Se mantiene la carta inicial para no romper los tests existentes que dependen de ella.
                player.receiveCard(new NumberCard("Red", 1)); 
            }
            game.playersQueue.add(player);
        }
        game.topCard = new NumberCard("Red", 9); // Carta inicial en la pila
        return game;
    }

    private void advancePlayerTurn() {
        directionState.advancePlayerTurn(playersQueue);
    }

    public int playerCount() {
        return playersQueue.size();
    }

    public List<Card> handOf(String playerName) {
        return playersQueue.stream()
            .filter(p -> p.name().equals(playerName))
            .findFirst()
            .map(Player::hand)
            .orElse(Collections.emptyList());
    }

    public Card topCard() {
        return topCard;
    }

    Player currentPlayer() {
        return playersQueue.peekFirst();
    }

    public String currentPlayerName() {
        Player currentPlayer = currentPlayer();
        return currentPlayer != null ? currentPlayer.name() : null;
    }

    public void playCard(String playerName, Card card) {
        // Delegamos al estado actual del juego
        gameState.playCard(this, playerName, card);
    }

    // Este método ahora será usado por los estados
    void playCardInternal(String playerName, Card card) {
        Player currentPlayer = currentPlayer();
        if (currentPlayer == null || !currentPlayer.name().equals(playerName)) {
            throw new IllegalArgumentException("Not this player's turn or player does not exist");
        }

        try {
            // Player.playCard lanzará excepciones específicas si hay problemas
            currentPlayer.playCard(card, this); 
            
            // Verificamos si este movimiento ha producido un ganador
            String possibleWinner = checkWinner();
            if (possibleWinner != null) {
                // Transición al estado FinishedState si hay un ganador
                this.gameState = new FinishedState(possibleWinner);
                return; // El juego terminó, no avanzar turno.
            }
            
            if (card.avanzaTurno()) {
                advancePlayerTurn();
            }
        } catch (CardDoesNotMatchException e) {
            // Penalización: la carta no coincide.
            System.out.println(currentPlayer.name() + " played an invalid card. Penalty: draws 2 cards and loses turn.");
            currentPlayer.receiveCard(new NumberCard("Penalty", 1)); 
            currentPlayer.receiveCard(new NumberCard("Penalty", 2)); 
            advancePlayerTurn(); // El jugador pierde su turno.
        } catch (PlayerDoesNotHaveCardException e) {
            // Error de integridad del juego, relanzamos
            throw new IllegalArgumentException("Player does not have this card", e);
        }
    }

    // Método actualizado para comprobar si hay un ganador
    private String checkWinner() {
        return playersQueue.stream()
            .filter(p -> p.hasNoCards() && p.hasDeclaredUno())
            .map(Player::name)
            .findFirst()
            .orElse(null);
    }

    // Método público para consultar el ganador
    public String winner() {
        return gameState.getWinner();
    }

    // Nuevo método para obtener el estado actual del juego
    public GameState getGameState() {
        return gameState;
    }

    // Métodos de efectos especiales para delegar desde las cartas
    void drawTwoEffect() {
        advancePlayerTurn(); // Avanza al jugador que debe robar
        Player affectedPlayer = playersQueue.peekFirst();
        if (affectedPlayer != null) {
            affectedPlayer.receiveCard(new NumberCard("Dummy", 0)); // Carta genérica
            affectedPlayer.receiveCard(new NumberCard("Dummy", 0)); // Carta genérica
        }
        advancePlayerTurn(); // Salta el turno del jugador que robó (avanza al siguiente)
    }

    void reverseEffect() {
        this.directionState = this.directionState.reverseDirection();
        if (playersQueue.size() > 2) { // Solo avanzar si hay más de 2 jugadores.
            advancePlayerTurn();
        } 
        // Si hay 2 jugadores, no se llama a advancePlayerTurn() aquí.
        // El jugador actual (quien jugó el Reverse) permanece como jugador actual,
        // y como ReverseCard.avanzaTurno() es false, no habrá otro advancePlayerTurn()
        // en el método playCard, logrando el efecto de que el turno vuelve al mismo jugador.
    }

    void skipEffect() {
        advancePlayerTurn(); // Avanza al jugador que es saltado
        advancePlayerTurn(); // Avanza al siguiente jugador (efectivamente saltando al anterior)
    }

    public void setHand(String playerName, List<Card> hand) {
        playersQueue.stream()
            .filter(p -> p.name().equals(playerName))
            .findFirst()
            .ifPresent(p -> p.setHand(hand));
    }

    public void givePlayerOneCard(String playerName, Card card) {
        playersQueue.stream()
            .filter(p -> p.name().equals(playerName))
            .findFirst()
            .ifPresent(p -> p.giveOneCard(card));
    }

    public void setTopCard(Card card) {
        this.topCard = card;
    }

    public void forceTopCard(Card card) {
        this.topCard = card;
    }

    public String nextPlayerAfter(String playerName) {
        Player currentPlayerObject = null;
        for (Player p : playersQueue) {
            if (p.name().equals(playerName)) {
                currentPlayerObject = p;
                break;
            }
        }
        
        if (currentPlayerObject == null) { 
            return null;
        }

        // La lógica compleja de rotación y decisión basada en isForwardDirection se delega al estado.
        // Se pasa una copia de la cola para que el estado no la modifique si no es su intención.
        Player nextPlayerObject = directionState.getNextPlayer(new ArrayDeque<>(playersQueue), currentPlayerObject);
        
        return nextPlayerObject != null ? nextPlayerObject.name() : null;
    }

    /**
     * Permite a un jugador declarar "UNO" cuando le queda una sola carta.
     * @param playerName Nombre del jugador que declara UNO.
     * @throws IllegalStateException si no es el turno del jugador o si tiene más de una carta.
     */
    public void playerDeclaresUno(String playerName) {
        Player player = null;
        for (Player p : playersQueue) {
            if (p.name().equals(playerName)) {
                player = p;
                break;
            }
        }
        
        if (player == null) {
            throw new IllegalArgumentException("Player does not exist");
        }
        
        if (!player.equals(currentPlayer())) {
            throw new IllegalStateException("Not this player's turn");
        }
        
        if (player.hand().size() == 1) {
            player.declareUno();
        } else {
            throw new IllegalStateException("Player can only declare UNO when they have exactly one card");
        }
    }

    // Clase abstracta para el patrón State del estado del juego
    public abstract class GameState {
        public abstract void playCard(UnoGame game, String playerName, Card card);
        public abstract String getWinner();
    }

    // Estado cuando el juego está en curso
    class InProgressState extends GameState {
        @Override
        public void playCard(UnoGame game, String playerName, Card card) {
            // Cuando el juego está en curso, procesamos la jugada
            game.playCardInternal(playerName, card);
        }

        @Override
        public String getWinner() {
            // En un juego en progreso, aún no hay ganador
            return null;
        }
    }

    // Estado cuando el juego ha finalizado
    class FinishedState extends GameState {
        private final String winnerName;
        
        public FinishedState(String winnerName) {
            this.winnerName = winnerName;
        }
        
        @Override
        public void playCard(UnoGame game, String playerName, Card card) {
            // Si el juego ha terminado, no se permiten más jugadas
            throw new IllegalStateException("Game is over");
        }

        @Override
        public String getWinner() {
            // Devolvemos el nombre del ganador
            return winnerName;
        }
    }

    // Interfaz para el patrón State de dirección de juego
    interface PlayDirectionState {
        void advancePlayerTurn(Deque<Player> playersQueue);
        Player getNextPlayer(Deque<Player> playersQueue, Player currentPlayer);
        PlayDirectionState reverseDirection();
    }

    // Estado para la dirección hacia adelante (horaria)
    static class ForwardDirectionState implements PlayDirectionState {
        @Override
        public void advancePlayerTurn(Deque<Player> playersQueue) {
            if (!playersQueue.isEmpty()) {
                playersQueue.addLast(playersQueue.removeFirst());
            }
        }

        @Override
        public Player getNextPlayer(Deque<Player> playersQueue, Player currentPlayer) {
            if (playersQueue.isEmpty() || currentPlayer == null) return null;
            Deque<Player> tempQueue = new ArrayDeque<>(playersQueue);
            // Rotar hasta que currentPlayer esté al frente
            while (tempQueue.peekFirst() != null && !tempQueue.peekFirst().equals(currentPlayer)) {
                tempQueue.addLast(tempQueue.removeFirst());
            }
            if (tempQueue.peekFirst() == null) return null; // No se encontró currentPlayer

            tempQueue.addLast(tempQueue.removeFirst()); // Avanzar una posición
            return tempQueue.peekFirst();
        }

        @Override
        public PlayDirectionState reverseDirection() {
            return new BackwardDirectionState();
        }
    }

    // Estado para la dirección hacia atrás (antihoraria)
    static class BackwardDirectionState implements PlayDirectionState {
        @Override
        public void advancePlayerTurn(Deque<Player> playersQueue) {
            if (!playersQueue.isEmpty()) {
                playersQueue.addFirst(playersQueue.removeLast());
            }
        }

        @Override
        public Player getNextPlayer(Deque<Player> playersQueue, Player currentPlayer) {
            if (playersQueue.isEmpty() || currentPlayer == null) return null;
            Deque<Player> tempQueue = new ArrayDeque<>(playersQueue);
            // Rotar hasta que currentPlayer esté al frente
            while (tempQueue.peekFirst() != null && !tempQueue.peekFirst().equals(currentPlayer)) {
                tempQueue.addLast(tempQueue.removeFirst());
            }
            if (tempQueue.peekFirst() == null) return null; // No se encontró currentPlayer

            tempQueue.addFirst(tempQueue.removeLast()); // Retroceder una posición (mover el último al frente)
            return tempQueue.peekFirst();
        }

        @Override
        public PlayDirectionState reverseDirection() {
            return new ForwardDirectionState();
        }
    }
}