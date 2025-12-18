package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.dto.ProductStockDetailDTO;
import com.tricol.springboottricolapi.dto.Response.StockBatchResponseDTO;
import com.tricol.springboottricolapi.dto.Response.StockMovementResponseDTO;
import com.tricol.springboottricolapi.dto.Response.ProductStockDTO;
import com.tricol.springboottricolapi.dto.StockAlertDTO;
import com.tricol.springboottricolapi.dto.StockValuationDTO;
import com.tricol.springboottricolapi.entity.*;
import com.tricol.springboottricolapi.entity.enums.MovementType;
import com.tricol.springboottricolapi.exception.InsufficientStockException;
import com.tricol.springboottricolapi.exception.InvalidFifoOperationException;
import com.tricol.springboottricolapi.exception.ResourceNotFoundException;
import com.tricol.springboottricolapi.repository.*;
import com.tricol.springboottricolapi.specification.StockMovementSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockService {

    private final StockBatchRepository stockBatchRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;


    public void createStockEntryFromOrder(SupplierOrder order) {

        for (SupplierOrderLine orderLine : order.getOrderLines()) {
            String batchNumber = generateBatchNumber(orderLine.getProduct());

            //stock batch
            StockBatch batch = StockBatch.builder()
                    .batchNumber(batchNumber)
                    .product(orderLine.getProduct())
                    .initialQuantity(orderLine.getQuantity())
                    .remainingQuantity(orderLine.getQuantity())
                    .unitPurchasePrice(orderLine.getUnitPurchasePrice())
                    .entryDate(LocalDateTime.now())
                    .supplierOrder(order)
                    .build();

            stockBatchRepository.save(batch);

            //(ENTREE)
            StockMovement movement = StockMovement.builder()
                    .product(orderLine.getProduct())
                    .batch(batch)
                    .movementType(MovementType.ENTREE)
                    .quantity(orderLine.getQuantity())
                    .movementDate(LocalDateTime.now())
                    .source("SUPPLIER_ORDER")
                    .sourceReference(order.getId())
                    .comments("Réception commande fournisseur: " + order.getOrderNumber())
                    .build();

            stockMovementRepository.save(movement);


            updateProductStock(orderLine.getProduct());
        }

    }


    public void processStockExit(Long productId, BigDecimal quantityNeeded, String reference, String notes) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));


        if (product.getCurrentStock().compareTo(quantityNeeded) < 0) {
            throw new InsufficientStockException(
                    product.getName(),
                    product.getCurrentStock().intValue(),
                    quantityNeeded.intValue()
            );
        }

        List<StockBatch> availableBatches = stockBatchRepository
                .findAvailableBatchesByProductIdOrderedByFifo(productId);

        if (availableBatches.isEmpty()) {
            throw new InvalidFifoOperationException("No available batches found for product: " + product.getName());
        }

        BigDecimal remainingQuantity = quantityNeeded;


        for (StockBatch batch : availableBatches) {
            if (remainingQuantity.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal quantityToConsume = remainingQuantity.min(batch.getRemainingQuantity());


            batch.setRemainingQuantity(batch.getRemainingQuantity().subtract(quantityToConsume));
            stockBatchRepository.save(batch);

            //(SORTIE)
            StockMovement movement = StockMovement.builder()
                    .product(product)
                    .batch(batch)
                    .movementType(MovementType.SORTIE)
                    .quantity(quantityToConsume)
                    .movementDate(LocalDateTime.now())
                    .source("STOCK_EXIT")
                    .comments(notes != null ? notes : "Sortie de stock - " + reference)
                    .build();

            stockMovementRepository.save(movement);

            remainingQuantity = remainingQuantity.subtract(quantityToConsume);

        }

        // Update product stock
        updateProductStock(product);

    }


    @Transactional(readOnly = true)
    public ProductStockDetailDTO getProductStockDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        List<StockBatch> batches = stockBatchRepository
                .findByProductIdOrderByEntryDateDesc(productId);

        List<StockBatchResponseDTO> batchDTOs = batches.stream()
                .map(this::mapToBatchDTO)
                .collect(Collectors.toList());

        BigDecimal totalValue = batches.stream()
                .map(batch -> batch.getUnitPurchasePrice()
                        .multiply(batch.getRemainingQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ProductStockDetailDTO.builder()
                .productId(product.getId())
                .productName(product.getName())
                .totalQuantity(product.getCurrentStock().doubleValue())
                .totalValue(totalValue)
                .batches(batchDTOs)
                .build();
    }


    @Transactional(readOnly = true)
    public List<StockMovementResponseDTO> getAllMovements() {
        return stockMovementRepository.findAllByOrderByMovementDateDesc()
                .stream()
                .map(this::mapToMovementDTO)
                .collect(Collectors.toList());
    }



    @Transactional(readOnly = true)
    public List<StockMovementResponseDTO> getMovementsByProduct(Long productId) {
        // Verify product exists
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        return stockMovementRepository.findByProductIdOrderByMovementDateDesc(productId)
                .stream()
                .map(this::mapToMovementDTO)
                .collect(Collectors.toList());
    }



    @Transactional(readOnly = true)
    public List<StockAlertDTO> getStockAlerts() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .filter(p -> p.getCurrentStock().compareTo(p.getReorderPoint()) < 0)
                .map(p -> StockAlertDTO.builder()
                        .productId(p.getId())
                        .productName(p.getName())
                        .currentStock(p.getCurrentStock().doubleValue())
                        .reorderPoint(p.getReorderPoint().doubleValue())
                        .deficit(p.getReorderPoint().subtract(p.getCurrentStock()).doubleValue())
                        .build())
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public StockValuationDTO getStockValuation() {
        List<StockBatch> allBatches = stockBatchRepository.findAll();

        BigDecimal totalValue = allBatches.stream()
                .filter(batch -> batch.getRemainingQuantity().compareTo(BigDecimal.ZERO) > 0)
                .map(batch -> batch.getUnitPurchasePrice()
                        .multiply(batch.getRemainingQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalProducts = productRepository.count();
        int totalBatches = (int) allBatches.stream()
                .filter(batch -> batch.getRemainingQuantity().compareTo(BigDecimal.ZERO) > 0)
                .count();

        return StockValuationDTO.builder()
                .totalStockValue(totalValue)
                .totalProducts((int) totalProducts)
                .totalBatches(totalBatches)
                .build();
    }



    private void updateProductStock(Product product) {
        List<StockBatch> batches = stockBatchRepository
                .findAvailableBatchesByProductIdOrderedByFifo(product.getId());

        BigDecimal totalStock = batches.stream()
                .map(StockBatch::getRemainingQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        product.setCurrentStock(totalStock);
        productRepository.save(product);
    }

    private String generateBatchNumber(Product product) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String productRef = product.getReference().substring(0, Math.min(4, product.getReference().length()));
        long count = stockBatchRepository.count() + 1;
        return String.format("LOT-%s-%s-%04d", productRef, date, count);
    }

    private StockBatchResponseDTO mapToBatchDTO(StockBatch batch) {
        return StockBatchResponseDTO.builder()
                .id(batch.getId())
                .batchNumber(batch.getBatchNumber())
                .productId(batch.getProduct().getId())
                .productName(batch.getProduct().getName())
                .quantityRemaining(batch.getRemainingQuantity().doubleValue())
                .unitPurchasePrice(batch.getUnitPurchasePrice())
                .entryDate(batch.getEntryDate().toLocalDate())
                .supplierOrderId(batch.getSupplierOrder() != null ? batch.getSupplierOrder().getId() : null)
                .supplierOrderNumber(batch.getSupplierOrder() != null ? batch.getSupplierOrder().getOrderNumber() : null)
                .build();
    }

    private StockMovementResponseDTO mapToMovementDTO(StockMovement movement) {
        return StockMovementResponseDTO.builder()
                .id(movement.getId())
                .productId(movement.getProduct().getId())
                .productName(movement.getProduct().getName())
                .movementType(movement.getMovementType())
                .quantity(movement.getQuantity().doubleValue())
                .unitPrice(movement.getBatch() != null ? movement.getBatch().getUnitPurchasePrice() : null)
                .batchNumber(movement.getBatch() != null ? movement.getBatch().getBatchNumber() : null)
                .reference(movement.getSource() + (movement.getSourceReference() != null ? "-" + movement.getSourceReference() : ""))
                .movementDate(movement.getMovementDate())
                .notes(movement.getComments())
                .build();
    }



    @Transactional(readOnly = true)
        public List<ProductStockDTO> getGlobalStockOverview() {
        return productRepository.findAll()
                .stream()
                .map(product -> ProductStockDTO.builder()
                        .productId(product.getId())
                        .reference(product.getReference())
                        .name(product.getName())
                        .currentStock(product.getCurrentStock())
                        .reorderPoint(product.getReorderPoint())
                        .unitOfMeasure(product.getUnitOfMeasure())
                        .isBelowReorderPoint(product.isBelowReorderPoint())
                        .build())

                .collect(Collectors.toList());
        }

    @Transactional(readOnly = true)
    public Page<StockMovementResponseDTO> searchMovements(
            LocalDateTime dateDebut,
            LocalDateTime dateFin,
            Long produitId,
            String reference,
            MovementType type,
            String numeroLot,
            Pageable pageable) {
        
        log.info("Searching stock movements with filters - dateDebut: {}, dateFin: {}, produitId: {}, reference: {}, type: {}, numeroLot: {}",
                dateDebut, dateFin, produitId, reference, type, numeroLot);
        
        Specification<StockMovement> spec = StockMovementSpecification.buildSpecification(
                dateDebut, dateFin, produitId, reference, type, numeroLot
        );
        
        Page<StockMovement> movements = stockMovementRepository.findAll(spec, pageable);
        
        return movements.map(this::mapToMovementDTO);
    }
}
