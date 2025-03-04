package com.projects.orderservice.service.impl;

import com.projects.orderservice.client.*;
import com.projects.orderservice.domain.entities.Commande;
import com.projects.orderservice.domain.entities.CommandeItem;
import com.projects.orderservice.domain.entities.Livraison;
import com.projects.orderservice.domain.enums.StatutCommande;
import com.projects.orderservice.domain.enums.StatutLivraison;
import com.projects.orderservice.dto.CommandeDTO;
import com.projects.orderservice.dto.CreateCommandeItemRequest;
import com.projects.orderservice.dto.CreateCommandeRequest;
import com.projects.orderservice.exception.BusinessException;
import com.projects.orderservice.exception.ResourceNotFoundException;
import com.projects.orderservice.mapper.CommandeMapper;
import com.projects.orderservice.repository.CommandeItemRepository;
import com.projects.orderservice.repository.CommandeRepository;
import com.projects.orderservice.repository.LivraisonRepository;
import com.projects.orderservice.service.interfaces.CommandeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository commandeRepository;
    private final CommandeItemRepository commandeItemRepository;
    private final LivraisonRepository livraisonRepository;
    private final CommandeMapper commandeMapper;
    private final ProductClient productClient;
    private final PaymentClient paymentClient;

    @Autowired
    public CommandeServiceImpl(
            CommandeRepository commandeRepository,
            CommandeItemRepository commandeItemRepository,
            LivraisonRepository livraisonRepository,
            CommandeMapper mapper,
            ProductClient productClient,
            PaymentClient paymentClient) {
        this.commandeRepository = commandeRepository;
        this.commandeItemRepository = commandeItemRepository;
        this.livraisonRepository = livraisonRepository;
        this.commandeMapper = mapper;
        this.productClient = productClient;
        this.paymentClient = paymentClient;
    }

    @Override
    @Transactional
    public CommandeDTO createCommande(Long userId, CreateCommandeRequest request) {
        log.info("Création d'une nouvelle commande pour l'utilisateur {}", userId);

        // Vérifier la disponibilité des produits en stock
        validateStock(request.getItems());

        // Créer une nouvelle commande
        Commande commande = new Commande();
        commande.setReference(generateUniqueReference());
        commande.setDate(LocalDateTime.now());
        commande.setStatut(StatutCommande.CREEE);

        // Sauvegarder la commande pour obtenir un ID
        commande = commandeRepository.save(commande);

        // Ajouter les items de la commande
        BigDecimal montantTotal = BigDecimal.ZERO;
        for (CreateCommandeItemRequest itemRequest : request.getItems()) {
            ResponseEntity<ProductDTO> productResponse = productClient.getProductById(itemRequest.getProduitId());
            ProductDTO product = productResponse.getBody();

            if (product == null) {
                throw new ResourceNotFoundException("Produit", "id", itemRequest.getProduitId());
            }

            CommandeItem item = new CommandeItem();
            item.setCommande(commande);
            item.setProduitId(itemRequest.getProduitId());
            item.setQuantite(itemRequest.getQuantite());
            item.setPrix(product.getPrix());

            commande.addItem(item);
            montantTotal = montantTotal.add(item.getMontantTotal());
        }

        // Mettre à jour le stock
        updateStock(request.getItems(), StockUpdateRequest.StockUpdateType.DECREASE);

        // Créer la livraison associée
        Livraison livraison = new Livraison();
        livraison.setCommande(commande);
        livraison.setStatut(StatutLivraison.PREPAREE);
        livraison.setDateCreation(LocalDateTime.now());

        commande.setLivraison(livraison);

        // Sauvegarder la commande complète
        commande = commandeRepository.save(commande);

        // Initialiser le paiement
        initPayment(commande.getReference(), montantTotal);

        return commandeMapper.toDTO(commande);
    }

    @Override
    public CommandeDTO getCommandeById(Long id) {
        log.info("Récupération de la commande avec ID {}", id);
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", "id", id));

        return commandeMapper.toDTO(commande);
    }

    @Override
    public CommandeDTO getCommandeByReference(String reference) {
        log.info("Récupération de la commande avec référence {}", reference);
        Commande commande = commandeRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", "reference", reference));

        return commandeMapper.toDTO(commande);
    }

    @Override
    public List<CommandeDTO> getCommandesByUserId(Long userId) {
        // Cette méthode nécessiterait d'avoir une relation entre Commande et User
        // ou d'ajouter un champ userId dans l'entité Commande
        // Pour l'instant, retournons une liste vide
        log.info("Récupération des commandes pour l'utilisateur {}", userId);
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public CommandeDTO cancelCommande(Long id) {
        log.info("Annulation de la commande avec ID {}", id);
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", "id", id));

        // Vérifier si la commande peut être annulée
        if (commande.getStatut() == StatutCommande.EXPEDIEE || commande.getStatut() == StatutCommande.LIVREE) {
            throw new BusinessException("Impossible d'annuler une commande déjà expédiée ou livrée.");
        }

        // Mettre à jour le statut
        commande.setStatut(StatutCommande.ANNULEE);

        // Réajuster le stock
        List<CreateCommandeItemRequest> items = commande.getItems().stream()
                .map(item -> {
                    CreateCommandeItemRequest request = new CreateCommandeItemRequest();
                    request.setProduitId(item.getProduitId());
                    request.setQuantite(item.getQuantite());
                    return request;
                })
                .collect(Collectors.toList());

        updateStock(items, StockUpdateRequest.StockUpdateType.INCREASE);

        // Sauvegarder la commande
        commande = commandeRepository.save(commande);

        return commandeMapper.toDTO(commande);
    }

    @Override
    @Transactional
    public CommandeDTO updateCommandeStatus(Long id, String nouveauStatut) {
        log.info("Mise à jour du statut de la commande {} vers {}", id, nouveauStatut);
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", "id", id));

        StatutCommande statut;
        try {
            statut = StatutCommande.valueOf(nouveauStatut);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Statut de commande invalide: " + nouveauStatut);
        }

        // Vérifier les règles métier pour le changement de statut
        validateStatusChange(commande.getStatut(), statut);

        // Mettre à jour le statut
        commande.setStatut(statut);

        // Si le statut devient EXPEDIEE, mettre à jour la livraison
        if (statut == StatutCommande.EXPEDIEE && commande.getLivraison() != null) {
            commande.getLivraison().setStatut(StatutLivraison.EXPEDIEE);
            commande.getLivraison().setDateModification(LocalDateTime.now());

            // Générer un numéro de suivi si non existant
            if (commande.getLivraison().getNumeroSuivi() == null) {
                commande.getLivraison().setNumeroSuivi("TRACK-" + UUID.randomUUID().toString().substring(0, 8));
            }
        }

        // Si le statut devient LIVREE, mettre à jour la livraison
        if (statut == StatutCommande.LIVREE && commande.getLivraison() != null) {
            commande.getLivraison().setStatut(StatutLivraison.LIVREE);
            commande.getLivraison().setDateModification(LocalDateTime.now());
        }

        // Sauvegarder la commande
        commande = commandeRepository.save(commande);

        return commandeMapper.toDTO(commande);
    }

    @Override
    @Transactional
    public int processUnpaidOrders() {
        log.info("Traitement des commandes non payées depuis plus de 24h");
        LocalDateTime dateLimit = LocalDateTime.now().minusHours(24);
        List<Commande> unpaidOrders = commandeRepository.findCommandesEnAttenteDepuisPlus24h(dateLimit);

        int count = 0;
        for (Commande commande : unpaidOrders) {
            // Vérifier le statut de paiement
            ResponseEntity<PaymentStatusDTO> paymentStatusResponse = paymentClient.getPaymentStatus(commande.getReference());
            PaymentStatusDTO paymentStatus = paymentStatusResponse.getBody();

            if (paymentStatus == null || !"PAID".equals(paymentStatus.getStatusPaiement())) {
                // Annuler la commande
                commande.setStatut(StatutCommande.ANNULEE);
                commandeRepository.save(commande);

                // Réajuster le stock
                List<CreateCommandeItemRequest> items = commande.getItems().stream()
                        .map(item -> {
                            CreateCommandeItemRequest request = new CreateCommandeItemRequest();
                            request.setProduitId(item.getProduitId());
                            request.setQuantite(item.getQuantite());
                            return request;
                        })
                        .collect(Collectors.toList());

                updateStock(items, StockUpdateRequest.StockUpdateType.INCREASE);
                count++;
            }
        }

        log.info("{} commandes ont été annulées pour cause de non-paiement", count);
        return count;
    }

    // Méthodes utilitaires privées

    private String generateUniqueReference() {
        // Générer une référence unique pour la commande
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(6);
        String uuid = UUID.randomUUID().toString().substring(0, 4);
        return "CMD-" + timestamp + "-" + uuid;
    }

    private void validateStock(List<CreateCommandeItemRequest> items) {
        for (CreateCommandeItemRequest item : items) {
            ResponseEntity<StockDTO> stockResponse = productClient.getProductStock(item.getProduitId());
            StockDTO stock = stockResponse.getBody();

            if (stock == null) {
                throw new ResourceNotFoundException("Stock", "produitId", item.getProduitId());
            }

            if (!stock.isDisponible() || stock.getQuantiteDisponible() < item.getQuantite()) {
                throw new BusinessException("Le produit " + item.getProduitId() + " n'est pas disponible en quantité suffisante. " +
                        "Quantité demandée: " + item.getQuantite() + ", Quantité disponible: " + stock.getQuantiteDisponible());
            }
        }
    }

    private void updateStock(List<CreateCommandeItemRequest> items, StockUpdateRequest.StockUpdateType type) {
        List<StockUpdateRequest> stockUpdates = items.stream()
                .map(item -> {
                    StockUpdateRequest request = new StockUpdateRequest();
                    request.setProduitId(item.getProduitId());
                    request.setQuantite(item.getQuantite());
                    request.setType(type);
                    return request;
                })
                .collect(Collectors.toList());

        productClient.updateProductStock(stockUpdates);
    }

    private void validateStatusChange(StatutCommande currentStatus, StatutCommande newStatus) {
        // Implémenter les règles métier pour les transitions de statut
        // Par exemple: impossible de passer de ANNULEE à un autre statut
        if (currentStatus == StatutCommande.ANNULEE) {
            throw new BusinessException("Impossible de modifier le statut d'une commande annulée.");
        }

        // Impossible de passer de LIVREE à un autre statut
        if (currentStatus == StatutCommande.LIVREE) {
            throw new BusinessException("Impossible de modifier le statut d'une commande livrée.");
        }

        // Impossible de revenir à un statut antérieur
        if (currentStatus.ordinal() > newStatus.ordinal()) {
            throw new BusinessException("Impossible de revenir à un statut antérieur.");
        }
    }

    private void initPayment(String reference, BigDecimal montant) {
        PaymentInitRequestDTO paymentRequest = new PaymentInitRequestDTO();
        paymentRequest.setCommandeReference(reference);
        paymentRequest.setMontant(montant);
        paymentRequest.setDevise("EUR");
        paymentRequest.setDescription("Paiement pour la commande " + reference);
        paymentRequest.setReturnUrl("/api/orders/" + reference + "/confirm");

        paymentClient.initializePayment(paymentRequest);
    }
}