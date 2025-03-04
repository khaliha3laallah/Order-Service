package com.projects.orderservice.controller;

import com.projects.orderservice.domain.enums.StatutLivraison;
import com.projects.orderservice.dto.LivraisonDTO;
import com.projects.orderservice.service.interfaces.LivraisonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/livraisons")
@Slf4j
public class LivraisonController {

    private final LivraisonService livraisonService;

    @Autowired
    public LivraisonController(LivraisonService livraisonService) {
        this.livraisonService = livraisonService;
    }

    @PostMapping("/commande/{commandeId}")
    public ResponseEntity<LivraisonDTO> createLivraison(@PathVariable Long commandeId) {
        log.info("Création d'une nouvelle livraison pour la commande {}", commandeId);
        LivraisonDTO livraisonDTO = livraisonService.createLivraison(commandeId);
        return new ResponseEntity<>(livraisonDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivraisonDTO> getLivraisonById(@PathVariable Long id) {
        log.info("Récupération de la livraison avec ID {}", id);
        LivraisonDTO livraisonDTO = livraisonService.getLivraisonById(id);
        return ResponseEntity.ok(livraisonDTO);
    }

    @GetMapping("/commande/{commandeId}")
    public ResponseEntity<LivraisonDTO> getLivraisonByCommandeId(@PathVariable Long commandeId) {
        log.info("Récupération de la livraison pour la commande {}", commandeId);
        LivraisonDTO livraisonDTO = livraisonService.getLivraisonByCommandeId(commandeId);
        return ResponseEntity.ok(livraisonDTO);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<LivraisonDTO> updateLivraisonStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        log.info("Mise à jour du statut de la livraison {} vers {}", id, status);
        StatutLivraison statutLivraison;
        try {
            statutLivraison = StatutLivraison.valueOf(status);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        LivraisonDTO livraisonDTO = livraisonService.updateLivraisonStatus(id, statutLivraison);
        return ResponseEntity.ok(livraisonDTO);
    }

    @PutMapping("/{id}/numero-suivi")
    public ResponseEntity<LivraisonDTO> addNumeroSuivi(
            @PathVariable Long id,
            @RequestParam String numeroSuivi) {
        log.info("Ajout du numéro de suivi {} à la livraison {}", numeroSuivi, id);
        LivraisonDTO livraisonDTO = livraisonService.addNumeroSuivi(id, numeroSuivi);
        return ResponseEntity.ok(livraisonDTO);
    }
}