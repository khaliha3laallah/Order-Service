package com.projects.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandeItemDTO {
    private Long id;
    private Long produitId;
    private Integer quantite;
    private BigDecimal prix;
    private BigDecimal montantTotal;
}
