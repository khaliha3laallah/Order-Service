package com.projects.orderservice.controller;

import com.projects.orderservice.dto.CommandeDTO;
import com.projects.orderservice.dto.CreateCommandeRequest;
import com.projects.orderservice.service.interfaces.CommandeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
@Slf4j
public class CommandeController {

    private final CommandeService commandeService;

    @Autowired
    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    @PostMapping
    public ResponseEntity<CommandeDTO> createCommande(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateCommandeRequest request) {
        log.info("Création d'une nouvelle commande pour l'utilisateur {}", userId);
        CommandeDTO commandeDTO = commandeService.createCommande(userId, request);
        return new ResponseEntity<>(commandeDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeDTO> getCommandeById(@PathVariable Long id) {
        log.info("Récupération de la commande avec ID {}", id);
        CommandeDTO commandeDTO = commandeService.getCommandeById(id);
        return ResponseEntity.ok(commandeDTO);
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<CommandeDTO> getCommandeByReference(@PathVariable String reference) {
        log.info("Récupération de la commande avec référence {}", reference);
        CommandeDTO commandeDTO = commandeService.getCommandeByReference(reference);
        return ResponseEntity.ok(commandeDTO);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommandeDTO>> getCommandesByUserId(@PathVariable Long userId) {
        log.info("Récupération des commandes pour l'utilisateur {}", userId);
        List<CommandeDTO> commandes = commandeService.getCommandesByUserId(userId);
        return ResponseEntity.ok(commandes);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<CommandeDTO> cancelCommande(@PathVariable Long id) {
        log.info("Annulation de la commande avec ID {}", id);
        CommandeDTO commandeDTO = commandeService.cancelCommande(id);
        return ResponseEntity.ok(commandeDTO);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CommandeDTO> updateCommandeStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        log.info("Mise à jour du statut de la commande {} vers {}", id, status);
        CommandeDTO commandeDTO = commandeService.updateCommandeStatus(id, status);
        return ResponseEntity.ok(commandeDTO);
    }

    @PostMapping("/process-unpaid")
    public ResponseEntity<Integer> processUnpaidOrders() {
        log.info("Traitement des commandes non payées depuis plus de 24h");
        int count = commandeService.processUnpaidOrders();
        return ResponseEntity.ok(count);
    }
}