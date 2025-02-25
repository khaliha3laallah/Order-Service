package com.projects.orderservice.domain.entities;

import com.projects.orderservice.domain.enums.StatutCommande;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commande")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference", nullable = false, unique = true, length = 50)
    private String reference;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "statut", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StatutCommande statut;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommandeItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private Livraison livraison;

    // Méthode utilitaire pour ajouter un item
    public void addItem(CommandeItem item) {
        items.add(item);
        item.setCommande(this);
    }

    // Méthode utilitaire pour supprimer un item
    public void removeItem(CommandeItem item) {
        items.remove(item);
        item.setCommande(null);
    }


}
