package com.projects.orderservice.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusDTO {
    private String commandeReference;
    private String statusPaiement;
    private BigDecimal montant;
    private String moyenPaiement;
    private LocalDateTime datePaiement;
}