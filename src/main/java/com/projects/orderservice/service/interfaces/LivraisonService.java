package com.projects.orderservice.service.interfaces;

import com.projects.orderservice.domain.enums.StatutLivraison;
import com.projects.orderservice.dto.LivraisonDTO;

public interface LivraisonService {

    /**
     * Crée une nouvelle livraison pour une commande
     * @param commandeId ID de la commande
     * @return La livraison créée
     */
    LivraisonDTO createLivraison(Long commandeId);

    /**
     * Récupère une livraison par son ID
     * @param id ID de la livraison
     * @return Les détails de la livraison
     */
    LivraisonDTO getLivraisonById(Long id);

    /**
     * Récupère une livraison par l'ID de la commande associée
     * @param commandeId ID de la commande
     * @return Les détails de la livraison
     */
    LivraisonDTO getLivraisonByCommandeId(Long commandeId);

    /**
     * Met à jour le statut d'une livraison
     * @param id ID de la livraison
     * @param nouveauStatut Nouveau statut de la livraison
     * @return La livraison mise à jour
     */
    LivraisonDTO updateLivraisonStatus(Long id, StatutLivraison nouveauStatut);

    /**
     * Ajoute un numéro de suivi à une livraison
     * @param id ID de la livraison
     * @param numeroSuivi Numéro de suivi
     * @return La livraison mise à jour
     */
    LivraisonDTO addNumeroSuivi(Long id, String numeroSuivi);
}
