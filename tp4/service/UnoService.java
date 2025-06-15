package org.udesa.unoback.service;

import org.springframework.stereotype.Service;
import org.udesa.unoback.model.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UnoService {
    private Map<UUID, Match> matches = new ConcurrentHashMap<>();
    
    public UUID createMatch(List<String> players) {
        UUID matchId = UUID.randomUUID();
        List<Card> deck = createDeck();
        Match match = Match.fullMatch(deck, players);
        matches.put(matchId, match);
        return matchId;
    }
    
    public void playCard(UUID matchId, String player, JsonCard jsonCard) {
        Match match = getMatch(matchId);
        Card card = jsonCard.asCard();
        match.play(player, card);
    }
    
    public void drawCard(UUID matchId, String player) {
        Match match = getMatch(matchId);
        match.drawCard(player);
    }
    
    public JsonCard getActiveCard(UUID matchId) {
        Match match = getMatch(matchId);
        return match.activeCard().asJson();
    }
    
    public List<JsonCard> getPlayerHand(UUID matchId) {
        Match match = getMatch(matchId);
        return match.playerHand().stream()
                .map(card -> ((Card) card).asJson())
                .toList();
    }
    
    private Match getMatch(UUID matchId) {
        Match match = matches.get(matchId);
        if (match == null) {
            throw new RuntimeException("Match not found: " + matchId);
        }
        return match;
    }
    
    private List<Card> createDeck() {
        List<Card> deck = new ArrayList<>();
        String[] colors = {"Red", "Blue", "Green", "Yellow"};
        
        // Add Number Cards (0-9)
        for (String color : colors) {
            // One 0 card per color
            deck.add(new NumberCard(color, 0));
            
            // Two of each number 1-9 per color
            for (int number = 1; number <= 9; number++) {
                deck.add(new NumberCard(color, number));
                deck.add(new NumberCard(color, number));
            }
        }
        
        // Add Action Cards (Skip, Reverse, Draw2)
        for (String color : colors) {
            // Two of each action card per color
            deck.add(new SkipCard(color));
            deck.add(new SkipCard(color));
            
            deck.add(new ReverseCard(color));
            deck.add(new ReverseCard(color));
            
            deck.add(new Draw2Card(color));
            deck.add(new Draw2Card(color));
        }
        
        // Add Wild Cards (4 total)
        for (int i = 0; i < 4; i++) {
            deck.add(new WildCard());
        }
        
        // Shuffle the deck
        Collections.shuffle(deck);
        
        return deck;
    }
}