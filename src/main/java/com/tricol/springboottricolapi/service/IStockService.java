package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.dto.ProductStockDetailDTO;
import com.tricol.springboottricolapi.dto.Response.ProductStockDTO;
import com.tricol.springboottricolapi.dto.Response.StockMovementResponseDTO;
import com.tricol.springboottricolapi.dto.StockAlertDTO;
import com.tricol.springboottricolapi.dto.StockValuationDTO;
import com.tricol.springboottricolapi.entity.SupplierOrder;
import com.tricol.springboottricolapi.entity.enums.MovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface IStockService {
    void createStockEntryFromOrder(SupplierOrder order);
    void processStockExit(Long productId, BigDecimal quantityNeeded, String reference, String notes);
    ProductStockDetailDTO getProductStockDetail(Long productId);
    List<StockMovementResponseDTO> getAllMovements();
    List<StockMovementResponseDTO> getMovementsByProduct(Long productId);
    List<StockAlertDTO> getStockAlerts();
    StockValuationDTO getStockValuation();
    List<ProductStockDTO> getGlobalStockOverview();
    Page<StockMovementResponseDTO> searchMovements(LocalDateTime dateDebut, LocalDateTime dateFin, Long produitId, String reference, MovementType type, String numeroLot, Pageable pageable);
}
