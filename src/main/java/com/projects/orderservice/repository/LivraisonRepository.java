package com.projects.orderservice.repository;

import com.projects.orderservice.domain.entities.Livraison;
import com.projects.orderservice.domain.enums.StatutLivraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivraisonRepository extends JpaRepository<Livraison, Long> {

    Optional<Livraison> findByCommandeId(Long commandeId);

    Optional<Livraison> findByNumeroSuivi(String numeroSuivi);

    List<Livraison> findByStatut(StatutLivraison statut);
}