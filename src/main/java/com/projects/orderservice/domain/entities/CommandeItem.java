package com.projects.orderservice.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "commande_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommandeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

    @Column(name = "produit_id", nullable = false)
    private Long produitId;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "prix", nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;

    // Méthode utilitaire pour calculer le montant total de l'item
    public BigDecimal getMontantTotal() {
        return prix.multiply(BigDecimal.valueOf(quantite));
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }
}