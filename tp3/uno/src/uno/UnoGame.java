package uno;
import java.util.*;
/**
 * Clase principal que representa una partida de UNO.
 */
public class UnoGame {
    private final List<Player> players = new ArrayList<>();
    private Card topCard;
    private int currentPlayerIndex = 0;
    private int direction = 1; // 1: horario, -1: antihorario

    private UnoGame() {}

    public static UnoGame withPlayers(String... playerNames) {
        UnoGame game = new UnoGame();
        for (String name : playerNames) {
            Player player = new Player(name);
            for (int i = 0; i < 7; i++) {
                player.receiveCard(new NumberCard("Red", 1)); // Dummy card - Si se cambia, va a haber que cambiar el test para que coincida la priemr cara
            }
            game.players.add(player);
        }
        game.topCard = new NumberCard("Red", 9);
        return game;
    }

    public int playerCount() {
        return players.size();
    }

    public List<Card> handOf(String playerName) { //handOf se refiere a la mano de un jugador
        return players.stream()
            .filter(p -> p.name().equals(playerName))
            .findFirst()
            .map(Player::hand)
            .orElse(Collections.emptyList());
    }

    public Card topCard() {
        return topCard;
    }

    Player currentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public String currentPlayerName() {
        return currentPlayer().name();
    }

    public void playCard(String playerName, Card card) {
        if (winner() != null) {
            throw new IllegalStateException("Game is over");
        }
        if (!currentPlayer().name().equals(playerName)) {
            throw new IllegalArgumentException("Not this player's turn");
        }
        card.play(this, currentPlayer());
        if (winner() != null) {
            return;
        }
        if (card.avanzaTurno()) {
            currentPlayerIndex = (currentPlayerIndex + direction + players.size()) % players.size();
        }
    }

    public void playCard(String playerName, Card card, String color) {
        if (winner() != null) {
            throw new IllegalStateException("Game is over");
        }
        if (!currentPlayer().name().equals(playerName)) {
            throw new IllegalArgumentException("Not this player's turn");
        }
        card.play(this, currentPlayer(), color);
        if (currentPlayer().hasNoCards()) {
            return;
        }
        if (card.avanzaTurno()) {
            currentPlayerIndex = (currentPlayerIndex + direction + players.size()) % players.size();
        }
    }

    // Métodos de efectos especiales para delegar desde las cartas
    void drawTwoEffect() {
        int nextIndex = (currentPlayerIndex + direction + players.size()) % players.size();
        Player nextPlayer = players.get(nextIndex);
        nextPlayer.receiveCard(new NumberCard("Red", 1));
        nextPlayer.receiveCard(new NumberCard("Red", 1));
        currentPlayerIndex = (currentPlayerIndex + 2 * direction + players.size()) % players.size();
    }
    void reverseEffect() {
        direction *= -1;
        currentPlayerIndex = (currentPlayerIndex + direction + players.size()) % players.size();
    }
    void skipEffect() {
        currentPlayerIndex = (currentPlayerIndex + 2 * direction + players.size()) % players.size();
    }

    public void setHand(String playerName, List<Card> hand) {
        players.stream()
            .filter(p -> p.name().equals(playerName))
            .findFirst()
            .ifPresent(p -> p.setHand(hand));
    }

    public void givePlayerOneCard(String playerName, Card card) {
        players.stream()
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
        int idx = java.util.stream.IntStream.range(0, players.size())
            .filter(i -> players.get(i).name().equals(playerName))
            .findFirst()
            .orElse(-1);
        if (idx == -1) return null;
        int nextIdx = (idx + direction + players.size()) % players.size();
        return players.get(nextIdx).name();
    }

    public String winner() {
        return players.stream()
            .filter(Player::hasNoCards)
            .map(Player::name)
            .findFirst()
            .orElse(null);
    }
}