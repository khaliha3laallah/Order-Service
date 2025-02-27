package com.projects.orderservice.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitResponseDTO {
    private String commandeReference;
    private String paymentId;
    private String redirectUrl;
    private String status;
}