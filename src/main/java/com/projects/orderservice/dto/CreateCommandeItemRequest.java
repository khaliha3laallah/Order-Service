package com.projects.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommandeItemRequest {

    @NotNull(message = "L'ID du produit est requis")
    private Long produitId;

    @NotNull(message = "La quantité est requise")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantite;

}
