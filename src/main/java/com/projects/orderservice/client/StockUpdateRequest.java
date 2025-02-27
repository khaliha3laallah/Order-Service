package com.projects.orderservice.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateRequest {
    private Long produitId;
    private Integer quantite;
    private StockUpdateType type;

    public enum StockUpdateType {
        DECREASE, // Diminuer le stock (achat)
        INCREASE  // Augmenter le stock (annulation, retour)
    }
}