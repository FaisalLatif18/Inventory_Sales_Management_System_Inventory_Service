package main.java.com.pharmacy.inventory_service.dto;

import lombok.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryItemDto {
    @NotNull(message = "Product ID is required")
    private Long productId;
    @Min(value = 0, message = "Quantity must be zero or greater")
    private int quantity;
    private LocalDateTime lastUpdated;
}
