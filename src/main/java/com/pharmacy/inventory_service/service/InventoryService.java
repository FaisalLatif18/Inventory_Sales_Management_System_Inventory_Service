package com.pharmacy.inventory_service.service;

import com.pharmacy.inventory_service.dto.InventoryItemDto;
import com.pharmacy.inventory_service.entity.InventoryItem;
import com.pharmacy.inventory_service.repository.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryItemRepository repository;

    public InventoryItem addOrUpdateInventory(InventoryItemDto dto) {
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

        return repository.save(item);
    }

    public InventoryItem getInventoryByProductId(Long productId) {
        return repository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
    }

    public void deleteInventory(Long productId) {
        InventoryItem item = repository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        repository.delete(item);
    }
}
