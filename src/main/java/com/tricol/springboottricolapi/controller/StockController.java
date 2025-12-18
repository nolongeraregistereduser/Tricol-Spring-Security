package com.tricol.springboottricolapi.controller;

import com.tricol.springboottricolapi.dto.ProductStockDetailDTO;
import com.tricol.springboottricolapi.dto.Response.ProductStockDTO;
import com.tricol.springboottricolapi.dto.Response.StockMovementResponseDTO;
import com.tricol.springboottricolapi.dto.StockAlertDTO;
import com.tricol.springboottricolapi.dto.StockValuationDTO;
import com.tricol.springboottricolapi.entity.enums.MovementType;
import com.tricol.springboottricolapi.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<ProductStockDTO>> getGlobalStockOverview() {
        List<ProductStockDTO> globalStock = stockService.getGlobalStockOverview();
        return ResponseEntity.ok(globalStock);
    }

    @GetMapping("/produit/{productId}")
    public ResponseEntity<ProductStockDetailDTO> getProductStockDetail(@PathVariable Long productId) {
        ProductStockDetailDTO stockDetail = stockService.getProductStockDetail(productId);
        return ResponseEntity.ok(stockDetail);
    }

    @GetMapping("/mouvements")
    public ResponseEntity<Page<StockMovementResponseDTO>> searchMovements(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) Long produitId,
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) MovementType type,
            @RequestParam(required = false) String numeroLot,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "movementDate,desc") String[] sort) {
        
        LocalDateTime dateDebutTime = dateDebut != null ? dateDebut.atStartOfDay() : null;
        LocalDateTime dateFinTime = dateFin != null ? dateFin.atTime(23, 59, 59) : null;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(parseSort(sort)));
        
        Page<StockMovementResponseDTO> movements = stockService.searchMovements(
                dateDebutTime, dateFinTime, produitId, reference, type, numeroLot, pageable
        );
        
        return ResponseEntity.ok(movements);
    }
    
    private List<Sort.Order> parseSort(String[] sortParams) {
        List<Sort.Order> orders = new ArrayList<>();
        for (String sortParam : sortParams) {
            String[] parts = sortParam.split(",");
            String property = parts[0];
            Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("asc") 
                    ? Sort.Direction.ASC 
                    : Sort.Direction.DESC;
            orders.add(new Sort.Order(direction, property));
        }
        return orders;
    }

    @GetMapping("/mouvements/produit/{productId}")
    public ResponseEntity<List<StockMovementResponseDTO>> getMovementsByProduct(@PathVariable Long productId) {
        List<StockMovementResponseDTO> movements = stockService.getMovementsByProduct(productId);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/alertes")
    public ResponseEntity<List<StockAlertDTO>> getStockAlerts() {
        List<StockAlertDTO> alerts = stockService.getStockAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/valorisation")
    public ResponseEntity<StockValuationDTO> getStockValuation() {
        StockValuationDTO valuation = stockService.getStockValuation();
        return ResponseEntity.ok(valuation);
    }
}

