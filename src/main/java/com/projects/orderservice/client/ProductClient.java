package com.projects.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "Stock-Service")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ResponseEntity<ProductDTO> getProductById(@PathVariable Long id);

    @GetMapping("/api/products/{id}/stock")
    ResponseEntity<StockDTO> getProductStock(@PathVariable Long id);

    @PostMapping("/api/products/stock/update")
    ResponseEntity<Void> updateProductStock(@RequestBody List<StockUpdateRequest> stockUpdates);
}