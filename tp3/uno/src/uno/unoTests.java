package uno;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class unoTests {
    /**
     * Test: El juego inicia con dos jugadores y cada uno recibe 7 cartas.
     */
    @Test
    void gameStartsWithTwoPlayersAndEachReceivesSevenCards() {
        UnoGame game = UnoGame.withPlayers("Alice", "Bob");
        assertEquals(2, game.playerCount());
        assertEquals(7, game.handOf("Alice").size());
        assertEquals(7, game.handOf("Bob").size());
    }

    /**
     * Test: Un jugador puede jugar una carta válida (mismo color o número).
     */
    @Test
    void playerCanPlayValidCard() {
        UnoGame game = UnoGame.withPlayers("Alice", "Bob");
        Card validCard = game.handOf("Alice").get(0);
        game.setTopCard(validCard);
        game.playCard("Alice", validCard);
        assertEquals(validCard, game.topCard());
        assertEquals(6, game.handOf("Alice").size());
    }

    /**
     * Test: Un jugador no puede jugar una carta inválida (no coincide color ni número).
     */
    @Test
    void playerCannotPlayInvalidCard() {
        UnoGame game = UnoGame.withPlayers("Alice", "Bob");
        NumberCard valid = new NumberCard("Red", 1);
        NumberCard invalid = new NumberCard("Blue", 5);
        game.setHand("Alice", Arrays.asList(valid, invalid));
        game.setTopCard(valid);
        assertThrows(IllegalArgumentException.class, () -> game.playCard("Alice", invalid));
        assertEquals(2, game.handOf("Alice").size());
        assertEquals(valid, game.topCard());
    }

    /**
     * Test: Draw 2 obliga al siguiente jugador a robar dos cartas y perder su turno.
     */
    @Test
    void drawTwoCardForcesNextPlayerToDrawTwoAndSkip() {
        UnoGame game = UnoGame.withPlayers("Alice", "Bob");
        DrawTwoCard drawTwo = new DrawTwoCard("Red");
        game.setHand("Alice", Arrays.asList(drawTwo));
        game.forceTopCard(new DrawTwoCard("Red"));
        int initialHand = game.handOf("Bob").size();
        game.playCard("Alice", drawTwo);
        assertEquals(initialHand + 2, game.handOf("Bob").size());
        assertEquals("Alice", game.currentPlayer().name());
    }

    /**
     * Test: Reverse cambia el sentido de juego.
     */
    @Test
    void reverseCardChangesPlayDirection() {
        UnoGame game = UnoGame.withPlayers("Alice", "Bob", "Charlie");
        ReverseCard reverse = new ReverseCard("Blue");
        game.setHand("Alice", Arrays.asList(reverse));
        game.forceTopCard(new ReverseCard("Blue"));
        game.playCard("Alice", reverse);
        assertEquals("Charlie", game.currentPlayer().name());
    }

    /**
     * Test: Skip salta el turno del siguiente jugador.
     */
    @Test
    void skipCardSkipsNextPlayer() {
        UnoGame game = UnoGame.withPlayers("Alice", "Bob", "Charlie");
        SkipCard skip = new SkipCard("Green");
        game.setHand("Alice", Arrays.asList(skip));
        game.forceTopCard(new SkipCard("Green"));
        String skippedPlayer = game.nextPlayerAfter("Alice");
        game.playCard("Alice", skip);
        assertNotEquals(skippedPlayer, game.currentPlayer());
    }

    /**
     * Test: Wild permite elegir color al jugarla.
     */
    @Test
    void wildCardAllowsPlayerToChooseColor() {
        UnoGame game = UnoGame.withPlayers("Alice", "Bob");
        WildCard wild = new WildCard();
        game.setHand("Alice", Arrays.asList(wild));
        game.forceTopCard(new WildCard());
        game.playCard("Alice", wild, "Yellow");
        assertEquals("Yellow", game.topCard().color());
    }

    /**
     * Test: Un jugador gana al quedarse sin cartas y no se puede seguir jugando.
     */
    @Test
    void playerWinsWhenNoCardsLeft() {
        UnoGame game = UnoGame.withPlayers("Alice", "Bob");
        NumberCard lastCard = new NumberCard("Red", 5);
        game.givePlayerOneCard("Alice", lastCard);
        game.forceTopCard(new NumberCard("Red", 3));
        game.playCard("Alice", lastCard);
        assertEquals("Alice", game.winner());
        assertThrows(IllegalStateException.class, () -> game.playCard("Bob", new NumberCard("Red", 7)));
    }
}
