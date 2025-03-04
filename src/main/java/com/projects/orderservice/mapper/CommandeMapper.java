package com.projects.orderservice.mapper;

import com.projects.orderservice.domain.entities.Commande;
import com.projects.orderservice.domain.entities.CommandeItem;
import com.projects.orderservice.domain.entities.Livraison;
import com.projects.orderservice.dto.CommandeDTO;
import com.projects.orderservice.dto.CommandeItemDTO;
import com.projects.orderservice.dto.LivraisonDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {})
public interface CommandeMapper {

    @Mapping(target = "items", source = "items")
    @Mapping(target = "livraison", source = "livraison")
    CommandeDTO toDTO(Commande commande);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commande", ignore = true)
    CommandeItem toEntity(CommandeItemDTO dto);

    @Mapping(target = "montantTotal", expression = "java(commandeItem.getMontantTotal())")
    CommandeItemDTO toDTO(CommandeItem commandeItem);

    LivraisonDTO toDTO(Livraison livraison);

    List<CommandeDTO> toDTOList(List<Commande> commandes);
}