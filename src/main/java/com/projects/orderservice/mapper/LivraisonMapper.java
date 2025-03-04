package com.projects.orderservice.mapper;

import com.projects.orderservice.domain.entities.Livraison;
import com.projects.orderservice.dto.LivraisonDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LivraisonMapper {

    // Conversion de Livraison vers LivraisonDTO
    LivraisonDTO toDTO(Livraison livraison);

    // Conversion de LivraisonDTO vers Livraison, ignorer l'id
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commande", ignore = true)
    Livraison toEntity(LivraisonDTO dto);

    // Conversion d'une liste de Livraison vers une liste de LivraisonDTO
    List<LivraisonDTO> toDTOList(List<Livraison> livraisons);
}