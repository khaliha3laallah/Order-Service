package com.projects.orderservice.repository;

import com.projects.orderservice.domain.entities.Commande;
import com.projects.orderservice.domain.enums.StatutCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    Optional<Commande> findByReference(String reference);

    List<Commande> findByStatut(StatutCommande statut);

    @Query("SELECT c FROM Commande c WHERE c.statut = com.projects.orderservice.entity.Commande.StatutCommande.CREEE AND c.date < :dateLimit")
    List<Commande> findCommandesEnAttenteDepuisPlus24h(LocalDateTime dateLimit);
}