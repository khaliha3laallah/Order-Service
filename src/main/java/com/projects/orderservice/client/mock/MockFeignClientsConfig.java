package com.projects.orderservice.client.mock;

import com.projects.orderservice.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Configuration pour mocker les clients Feign pendant les tests
 * Activé uniquement lorsque la propriété feign.mock.enabled=true est définie
 */
@Configuration
@ImportAutoConfiguration({FeignAutoConfiguration.class})
@Slf4j
public class MockFeignClientsConfig {

    @Bean
    @Qualifier("mockProductClient")
    @Primary
    public ProductClient productClient() {
        return new ProductClient() {
            @Override
            public ResponseEntity<ProductDTO> getProductById(Long id) {
                // Simuler un produit de test
                ProductDTO product = new ProductDTO();
                product.setId(id);
                product.setNom("Produit Test " + id);
                product.setDescription("Description du produit " + id);
                product.setPrix(new BigDecimal("49.99"));
                product.setCategorieId(1L);
                return ResponseEntity.ok(product);
            }

            @Override
            public ResponseEntity<StockDTO> getProductStock(Long id) {
                System.out.println("MOCK: Calling getProductStock for product ID: " + id);
                // Simuler un stock disponible
                StockDTO stock = new StockDTO();
                stock.setProduitId(id);
                stock.setQuantiteDisponible(100);
                stock.setDisponible(true);
                return ResponseEntity.ok(stock);
            }

            @Override
            public ResponseEntity<Void> updateProductStock(List<StockUpdateRequest> stockUpdates) {
                // Ne rien faire, juste simuler une réponse OK
                return ResponseEntity.ok().build();
            }
        };
    }

    @Bean
    @Qualifier("mockPaymentClient")
    @Primary
    public PaymentClient paymentClient() {
        return new PaymentClient() {
            @Override
            public ResponseEntity<PaymentStatusDTO> getPaymentStatus(String commandeReference) {
                // Simuler un statut de paiement
                PaymentStatusDTO paymentStatus = new PaymentStatusDTO();
                paymentStatus.setCommandeReference(commandeReference);
                paymentStatus.setStatusPaiement("PAID"); // Toujours considérer comme payé pour les tests
                paymentStatus.setMontant(new BigDecimal("199.99"));
                paymentStatus.setMoyenPaiement("CARTE");
                paymentStatus.setDatePaiement(LocalDateTime.now());
                return ResponseEntity.ok(paymentStatus);
            }

            @Override
            public ResponseEntity<PaymentInitResponseDTO> initializePayment(PaymentInitRequestDTO request) {
                // Simuler une initialisation de paiement réussie
                PaymentInitResponseDTO response = new PaymentInitResponseDTO();
                response.setCommandeReference(request.getCommandeReference());
                response.setPaymentId("PAYMENT-" + request.getCommandeReference());
                response.setRedirectUrl("http://localhost:8080/payment/" + request.getCommandeReference());
                response.setStatus("INITIALIZED");
                return ResponseEntity.ok(response);
            }
        };
    }
}