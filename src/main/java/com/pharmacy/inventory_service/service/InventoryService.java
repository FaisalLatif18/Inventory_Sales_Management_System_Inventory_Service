package com.pharmacy.inventory_service.service;

import com.pharmacy.inventory_service.dto.InventoryItemDto;
import com.pharmacy.inventory_service.dto.ProductDto;
import com.pharmacy.inventory_service.entity.InventoryItem;
import com.pharmacy.inventory_service.repository.InventoryItemRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryItemRepository repository;
    private final RestTemplate restTemplate;

    @Value("${product.service.url:http://localhost:8082/api/products/}")
    private String productServiceUrl;

    public ResponseEntity<InventoryItem> addOrUpdateInventory(InventoryItemDto dto) {
        if (!validateProduct(dto.getProductId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Optional<InventoryItem> existing = repository.findByProductId(dto.getProductId());

        InventoryItem item = existing.map(i -> {
            i.setQuantity(dto.getQuantity());
            i.setLastUpdated(LocalDateTime.now());
            return i;
        }).orElse(InventoryItem.builder()
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .lastUpdated(LocalDateTime.now())
                .build());

        InventoryItem savedItem = repository.save(item);
        return ResponseEntity.ok(savedItem);
    }

    public ResponseEntity<InventoryItem> getInventoryByProductId(Long productId) {
        return repository.findByProductId(productId)
                .map(item -> ResponseEntity.ok(item)) // ✅ Fixed
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    public ResponseEntity<?> deleteInventory(Long productId) {
        Optional<InventoryItem> itemOpt = repository.findByProductId(productId);
        if (itemOpt.isPresent()) {
            repository.delete(itemOpt.get());
            return ResponseEntity.ok("Inventory deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Inventory not found for Product ID: " + productId);
        }
    }

    @Retry(name = "productServiceRetry", fallbackMethod = "productValidationFallback")
    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "productValidationFallback")
    public boolean validateProduct(Long productId) {
        String url = productServiceUrl + productId;
        ResponseEntity<ProductDto> response = restTemplate.getForEntity(url, ProductDto.class);
        return response.getStatusCode() == HttpStatus.OK && response.getBody() != null;
    }

    // Fallback returns false and logs the reason
    public boolean productValidationFallback(Long productId, Throwable t) {
        System.err.println(
                "Fallback triggered for product validation (Product ID: " + productId + "). Reason: " + t.getMessage());
        return false;
    }
}
