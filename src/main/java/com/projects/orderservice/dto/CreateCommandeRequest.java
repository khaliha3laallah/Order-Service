package com.projects.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommandeRequest {

    @NotEmpty(message = "Au moins un item est requis")
    private List<@Valid CreateCommandeItemRequest> items;

    @NotNull(message = "L'adresse de livraison est requise")
    private Long adresseLivraisonId;

}
