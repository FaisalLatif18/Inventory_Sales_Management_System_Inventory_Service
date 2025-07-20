package com.pharmacy.inventory_service.controller;

import com.pharmacy.inventory_service.dto.InventoryItemDto;
import com.pharmacy.inventory_service.entity.InventoryItem;
import com.pharmacy.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<InventoryItem> addOrUpdateInventory(@Valid @RequestBody InventoryItemDto dto) {
        return inventoryService.addOrUpdateInventory(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    @GetMapping("/{productId}")
    public ResponseEntity<?> getInventoryByProductId(@PathVariable Long productId) {
        return inventoryService.getInventoryByProductId(productId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteInventory(@PathVariable Long productId) {
        return inventoryService.deleteInventory(productId);
    }
}
