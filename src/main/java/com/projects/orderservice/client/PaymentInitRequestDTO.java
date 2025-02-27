package com.projects.orderservice.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitRequestDTO {
    private String commandeReference;
    private BigDecimal montant;
    private String devise;
    private String description;
    private String returnUrl;
}