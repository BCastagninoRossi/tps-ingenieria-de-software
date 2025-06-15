package org.udesa.unoback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.udesa.unoback.model.JsonCard;
import org.udesa.unoback.service.UnoService;

import java.util.List;
import java.util.UUID;

@RestController
public class UnoController {
    
    @Autowired
    private UnoService unoService;
    
    @PostMapping("newmatch")
    public ResponseEntity<UUID> newMatch(@RequestParam List<String> players) {
        UUID matchId = unoService.createMatch(players);
        return ResponseEntity.ok(matchId);
    }
    
    @PostMapping("play/{matchId}/{player}")
    public ResponseEntity<Void> play(@PathVariable UUID matchId, 
                                   @PathVariable String player, 
                                   @RequestBody JsonCard card) {
        unoService.playCard(matchId, player, card);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("draw/{matchId}/{player}")
    public ResponseEntity<Void> drawCard(@PathVariable UUID matchId, 
                                       @PathVariable String player) {
        unoService.drawCard(matchId, player);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("activecard/{matchId}")
    public ResponseEntity<JsonCard> activeCard(@PathVariable UUID matchId) {
        JsonCard activeCard = unoService.getActiveCard(matchId);
        return ResponseEntity.ok(activeCard);
    }
    
    @GetMapping("playerhand/{matchId}")
    public ResponseEntity<List<JsonCard>> playerHand(@PathVariable UUID matchId) {
        List<JsonCard> hand = unoService.getPlayerHand(matchId);
        return ResponseEntity.ok(hand);
    }
}