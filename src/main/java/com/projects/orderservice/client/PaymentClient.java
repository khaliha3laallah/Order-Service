package com.projects.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "Payment-Service")
public interface PaymentClient {

    @GetMapping("/api/payments/commande/{commandeReference}")
    ResponseEntity<PaymentStatusDTO> getPaymentStatus(@PathVariable String commandeReference);

    @PostMapping("/api/payments/init")
    ResponseEntity<PaymentInitResponseDTO> initializePayment(@RequestBody PaymentInitRequestDTO request);
}