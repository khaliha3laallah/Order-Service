package com.projects.orderservice.domain.entities;

import com.projects.orderservice.domain.enums.StatutLivraison;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "livraison")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Livraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", unique = true, nullable = false)
    private Commande commande;

    @Column(name = "numero_suivi", length = 50)
    private String numeroSuivi;

    @Column(name = "statut", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StatutLivraison statut;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;


    // Méthode utilitaire pour mettre à jour le statut et la date de modification
    public void updateStatut(StatutLivraison nouveauStatut) {
        this.statut = nouveauStatut;
        this.dateModification = LocalDateTime.now();
    }
}
