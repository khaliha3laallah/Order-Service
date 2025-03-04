package com.projects.orderservice.service.impl;

import com.projects.orderservice.domain.entities.Commande;
import com.projects.orderservice.domain.entities.Livraison;
import com.projects.orderservice.domain.enums.StatutLivraison;
import com.projects.orderservice.dto.LivraisonDTO;
import com.projects.orderservice.exception.BusinessException;
import com.projects.orderservice.exception.ResourceNotFoundException;
import com.projects.orderservice.mapper.LivraisonMapper;
import com.projects.orderservice.repository.CommandeRepository;
import com.projects.orderservice.repository.LivraisonRepository;
import com.projects.orderservice.service.interfaces.LivraisonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class LivraisonServiceImpl implements LivraisonService {

    private final LivraisonRepository livraisonRepository;
    private final CommandeRepository commandeRepository;
    private final LivraisonMapper livraisonMapper;

    @Autowired
    public LivraisonServiceImpl(
            LivraisonRepository livraisonRepository,
            CommandeRepository commandeRepository,
            LivraisonMapper livraisonMapper) {
        this.livraisonRepository = livraisonRepository;
        this.commandeRepository = commandeRepository;
        this.livraisonMapper = livraisonMapper;
    }

    @Override
    @Transactional
    public LivraisonDTO createLivraison(Long commandeId) {
        log.info("Création d'une nouvelle livraison pour la commande {}", commandeId);

        // Vérifier si la commande existe
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", "id", commandeId));

        // Vérifier si une livraison existe déjà pour cette commande
        if (livraisonRepository.findByCommandeId(commandeId).isPresent()) {
            throw new BusinessException("Une livraison existe déjà pour la commande " + commandeId);
        }

        // Créer une nouvelle livraison
        Livraison livraison = new Livraison();
        livraison.setCommande(commande);
        livraison.setStatut(StatutLivraison.PREPAREE);
        livraison.setDateCreation(LocalDateTime.now());

        // Sauvegarder la livraison
        livraison = livraisonRepository.save(livraison);

        return livraisonMapper.toDTO(livraison);
    }

    @Override
    public LivraisonDTO getLivraisonById(Long id) {
        log.info("Récupération de la livraison avec ID {}", id);
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livraison", "id", id));

        return livraisonMapper.toDTO(livraison);
    }

    @Override
    public LivraisonDTO getLivraisonByCommandeId(Long commandeId) {
        log.info("Récupération de la livraison pour la commande {}", commandeId);
        Livraison livraison = livraisonRepository.findByCommandeId(commandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Livraison", "commandeId", commandeId));

        return livraisonMapper.toDTO(livraison);
    }

    @Override
    @Transactional
    public LivraisonDTO updateLivraisonStatus(Long id, StatutLivraison nouveauStatut) {
        log.info("Mise à jour du statut de la livraison {} vers {}", id, nouveauStatut);
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livraison", "id", id));

        // Vérifier les règles métier pour le changement de statut
        validateStatusChange(livraison.getStatut(), nouveauStatut);

        // Mettre à jour le statut
        livraison.updateStatut(nouveauStatut);

        // Sauvegarder la livraison
        livraison = livraisonRepository.save(livraison);

        return livraisonMapper.toDTO(livraison);
    }

    @Override
    @Transactional
    public LivraisonDTO addNumeroSuivi(Long id, String numeroSuivi) {
        log.info("Ajout du numéro de suivi {} à la livraison {}", numeroSuivi, id);
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livraison", "id", id));

        // Vérifier si la livraison est au statut EXPEDIEE
        if (livraison.getStatut() != StatutLivraison.EXPEDIEE) {
            throw new BusinessException("Impossible d'ajouter un numéro de suivi à une livraison qui n'est pas au statut EXPEDIEE");
        }

        // Ajouter le numéro de suivi
        livraison.setNumeroSuivi(numeroSuivi);
        livraison.setDateModification(LocalDateTime.now());

        // Sauvegarder la livraison
        livraison = livraisonRepository.save(livraison);

        return livraisonMapper.toDTO(livraison);
    }

    // Méthodes utilitaires privées

    private void validateStatusChange(StatutLivraison currentStatus, StatutLivraison newStatus) {
        // Implémenter les règles métier pour les transitions de statut
        // Par exemple: impossible de passer de LIVREE à un autre statut
        if (currentStatus == StatutLivraison.LIVREE) {
            throw new BusinessException("Impossible de modifier le statut d'une livraison terminée.");
        }

        // Impossible de revenir à un statut antérieur
        if (currentStatus.ordinal() > newStatus.ordinal()) {
            throw new BusinessException("Impossible de revenir à un statut antérieur.");
        }
    }
}