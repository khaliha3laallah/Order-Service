package com.projects.orderservice.dto;

import com.projects.orderservice.domain.enums.StatutCommande;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandeDTO {
    private Long id;
    private String reference;
    private LocalDateTime date;
    private StatutCommande statut;
    private List<CommandeItemDTO> items = new ArrayList<>();
    private LivraisonDTO livraison;
}