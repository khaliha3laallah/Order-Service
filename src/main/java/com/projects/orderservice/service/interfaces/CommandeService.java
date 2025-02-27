package com.projects.orderservice.service.interfaces;

import com.projects.orderservice.dto.CommandeDTO;
import com.projects.orderservice.dto.CreateCommandeRequest;

import java.util.List;

public interface CommandeService {

    /**
     * Crée une nouvelle commande
     * @param userId ID de l'utilisateur qui passe la commande
     * @param request Détails de la commande à créer
     * @return La commande créée
     */
    CommandeDTO createCommande(Long userId, CreateCommandeRequest request);

    /**
     * Récupère une commande par son ID
     * @param id ID de la commande
     * @return Les détails de la commande
     */
    CommandeDTO getCommandeById(Long id);

    /**
     * Récupère une commande par sa référence
     * @param reference Référence de la commande
     * @return Les détails de la commande
     */
    CommandeDTO getCommandeByReference(String reference);

    /**
     * Récupère toutes les commandes d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Liste des commandes de l'utilisateur
     */
    List<CommandeDTO> getCommandesByUserId(Long userId);

    /**
     * Annule une commande
     * @param id ID de la commande à annuler
     * @return La commande mise à jour
     */
    CommandeDTO cancelCommande(Long id);

    /**
     * Met à jour le statut d'une commande
     * @param id ID de la commande
     * @param nouveauStatut Nouveau statut de la commande
     * @return La commande mise à jour
     */
    CommandeDTO updateCommandeStatus(Long id, String nouveauStatut);

    /**
     * Traite les commandes créées mais sans paiement depuis plus de 24h
     * @return Nombre de commandes annulées
     */
    int processUnpaidOrders();
}