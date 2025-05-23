package uno;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
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
        game.playCard("Alice", validCard); // Llamada unificada
        assertEquals(validCard, game.topCard());
        assertEquals(6, game.handOf("Alice").size());
    }

    /**
     * Test: Un jugador no puede jugar una carta inválida (no coincide color ni número).
     * ESTE TEST SE ELIMINA PORQUE SU LÓGICA AHORA ESTÁ CUBIERTA POR playingNonMatchingCardResultsInPenalty
     * Y LA IllegalArgumentException ESPERADA YA NO SE LANZA DEBIDO A LA NUEVA REGLA DE PENALIZACIÓN.
     */
    // @Test
    // void playerCannotPlayInvalidCard() {
    //     UnoGame game = UnoGame.withPlayers("Alice", "Bob");
    //     NumberCard valid = new NumberCard("Red", 1);
    //     NumberCard invalid = new NumberCard("Blue", 5);
    //     game.setHand("Alice", Arrays.asList(valid, invalid));
    //     game.setTopCard(valid);
    //     assertThrows(IllegalArgumentException.class, () -> game.playCard("Alice", invalid)); // Llamada unificada
    //     assertEquals(2, game.handOf("Alice").size());
    //     assertEquals(valid, game.topCard());
    // }

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
        game.playCard("Alice", drawTwo); // Llamada unificada
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
        game.playCard("Alice", reverse); // Llamada unificada
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
        game.playCard("Alice", skip); // Llamada unificada
        assertEquals("Charlie", game.currentPlayer().name());
    }

    /**
     * Test: Wild permite elegir color al jugarla.
     */
    @Test
    void wildCardAllowsPlayerToChooseColor() {
        UnoGame game = UnoGame.withPlayers("Alice", "Bob");
        WildCard wild = new WildCard();
        game.setHand("Alice", Arrays.asList(wild));
        game.forceTopCard(new WildCard()); // Poner una Wild en la pila para que sea compatible
        
        // El color se elige ANTES de llamar a playCard
        wild.chooseColor("Yellow"); 
        
        game.playCard("Alice", wild); // Llamada unificada
        assertEquals("Yellow", game.topCard().color());
        // Verificar que la carta en la pila es la misma instancia de WildCard con el color elegido
        assertEquals(wild, game.topCard()); 
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
        game.playerDeclaresUno("Alice"); 
        game.playCard("Alice", lastCard); // Llamada unificada
        assertEquals("Alice", game.winner());
        assertThrows(IllegalStateException.class, () -> game.playCard("Bob", new NumberCard("Red", 7)));
    }

    @Test
    void playerCannotWinWithoutDeclaringUno() {
        UnoGame game = UnoGame.withPlayers("Alice", "Bob");
        NumberCard lastCard = new NumberCard("Red", 5);
        game.givePlayerOneCard("Alice", lastCard); 
        game.forceTopCard(new NumberCard("Red", 3)); 
        game.playCard("Alice", lastCard); // Llamada unificada
        assertEquals(null, game.winner()); 
        assertEquals(0, game.handOf("Alice").size()); 
        assertEquals("Bob", game.currentPlayerName());
    }

    @Test
    void playerCannotDeclareUnoWithMoreThanOneCard() {
        UnoGame game = UnoGame.withPlayers("Alice", "Bob");
        // Alice tiene 7 cartas por defecto
        assertThrows(IllegalStateException.class, () -> game.playerDeclaresUno("Alice"));
    }

    @Test
    void playerCannotDeclareUnoIfNotTheirTurn() {
        UnoGame game = UnoGame.withPlayers("Alice", "Bob");
        // Es el turno de Alice. Bob tiene 7 cartas.
        // Damos una sola carta a Bob para el escenario, aunque no sea su turno.
        game.givePlayerOneCard("Bob", new NumberCard("Blue", 1));
        assertThrows(IllegalStateException.class, () -> game.playerDeclaresUno("Bob"));
    }

    @Test
    void unoDeclarationResetsIfCardCountIncreases() {
        UnoGame game = UnoGame.withPlayers("Alice", "Bob");
        game.givePlayerOneCard("Alice", new NumberCard("Red", 1));
        game.forceTopCard(new NumberCard("Red", 9)); // Para que Alice pueda declarar
        game.playerDeclaresUno("Alice");
        assertEquals(true, game.currentPlayer().hasDeclaredUno());

        // Simulamos que Alice roba una carta (ej. por un efecto no modelado aquí directamente)
        // o simplemente recibe una carta por otra razón.
        game.currentPlayer().receiveCard(new NumberCard("Blue", 2));
        assertEquals(false, game.currentPlayer().hasDeclaredUno());
    }

    @Test
    void playerCannotPlayIfNotTheirTurn() {
        UnoGame game = UnoGame.withPlayers("Alice", "Bob");
        Card bobCard = game.handOf("Bob").get(0);
        game.forceTopCard(new NumberCard(bobCard.color(), 9)); 
        assertThrows(IllegalArgumentException.class, () -> game.playCard("Bob", bobCard)); // Llamada unificada
    }

    @Test
    void reverseCardInTwoPlayerGameReturnsTurnToSelf() {
        UnoGame game = UnoGame.withPlayers("Alice", "Bob");
        ReverseCard reverse = new ReverseCard("Red");
        game.setHand("Alice", Arrays.asList(reverse));
        game.forceTopCard(new NumberCard("Red", 3)); 
        
        assertEquals("Alice", game.currentPlayerName());
        game.playCard("Alice", reverse); // Llamada unificada
        assertEquals("Alice", game.currentPlayerName());
    }

    @Test
    void playingWildCardWithoutChoosingColorThrowsException() {
        UnoGame game = UnoGame.withPlayers("Alice", "Bob");
        WildCard wild = new WildCard();
        game.setHand("Alice", Arrays.asList(wild));
        game.forceTopCard(new NumberCard("Red", 1)); // Para que sea compatible en matches()

        // No se llama a wild.chooseColor("someColor");

        // La excepción debería ocurrir dentro de WildCard.play() o Player.playCard() si la validación está allí.
        // Según el plan, la validación está en WildCard.play().
        assertThrows(IllegalStateException.class, () -> game.playCard("Alice", wild));
    }

    @Test
    void playingNonMatchingCardResultsInPenalty() {
        UnoGame game = UnoGame.withPlayers("Alice", "Bob");
        // Configurar la mano de Alice y la carta superior
        NumberCard topCard = new NumberCard("Red", 5);
        NumberCard nonMatchingCardInHand = new NumberCard("Blue", 3); // No coincide ni color ni número
        NumberCard otherCardInHand = new NumberCard("Green", 7);
        
        game.forceTopCard(topCard);
        game.setHand("Alice", Arrays.asList(nonMatchingCardInHand, otherCardInHand));
        
        int aliceHandSizeBefore = game.handOf("Alice").size(); // Debería ser 2
        String aliceName = "Alice";
        String bobName = "Bob";

        assertEquals(aliceName, game.currentPlayerName());

        // Alice intenta jugar la carta no compatible
        game.playCard(aliceName, nonMatchingCardInHand);

        // Verificar penalización:
        // 1. Alice tiene 2 cartas más (las 2 originales + 2 de penalización)
        assertEquals(aliceHandSizeBefore + 2, game.handOf(aliceName).size());
        // 2. La carta que intentó jugar sigue en su mano
        assertTrue(game.handOf(aliceName).contains(nonMatchingCardInHand));
        // 3. La topCard no cambió
        assertEquals(topCard, game.topCard());
        // 4. El turno pasó a Bob
        assertEquals(bobName, game.currentPlayerName());
    }
}
