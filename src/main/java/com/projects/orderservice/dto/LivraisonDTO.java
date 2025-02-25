package com.projects.orderservice.dto;
import com.projects.orderservice.domain.enums.StatutLivraison;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LivraisonDTO {
    private Long id;
    private String numeroSuivi;
    private StatutLivraison statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
}
