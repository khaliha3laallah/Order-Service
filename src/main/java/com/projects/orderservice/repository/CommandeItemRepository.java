package com.projects.orderservice.repository;

import com.projects.orderservice.domain.entities.CommandeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandeItemRepository extends JpaRepository<CommandeItem, Long> {

    List<CommandeItem> findByCommandeId(Long commandeId);

    List<CommandeItem> findByProduitId(Long produitId);
}