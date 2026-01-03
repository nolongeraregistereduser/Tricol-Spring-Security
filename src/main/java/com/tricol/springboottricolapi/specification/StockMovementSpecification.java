package com.tricol.springboottricolapi.specification;

import com.tricol.springboottricolapi.entity.StockMovement;
import com.tricol.springboottricolapi.entity.enums.MovementType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class StockMovementSpecification {

    public static Specification<StockMovement> hasDateBetween(LocalDateTime dateDebut, LocalDateTime dateFin) {
        return (root, query, criteriaBuilder) -> {
            if (dateDebut == null && dateFin == null) {
                return null;
            }

            if (dateDebut != null && dateFin != null) {
                return criteriaBuilder.between(root.get("movementDate"), dateDebut, dateFin);
            } else if (dateDebut != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("movementDate"), dateDebut);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("movementDate"), dateFin);
            }
        };
    }

    public static Specification<StockMovement> hasProductId(Long produitId) {
        return (root, query, criteriaBuilder) -> {
            if (produitId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("product").get("id"), produitId);
        };
    }

    public static Specification<StockMovement> hasProductReference(String reference) {
        return (root, query, criteriaBuilder) -> {
            if (reference == null || reference.trim().isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(root.get("product").get("reference"), reference);
        };
    }

    public static Specification<StockMovement> hasMovementType(MovementType type) {
        return (root, query, criteriaBuilder) -> {
            if (type == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("movementType"), type);
        };
    }

    public static Specification<StockMovement> hasBatchNumber(String numeroLot) {
        return (root, query, criteriaBuilder) -> {
            if (numeroLot == null || numeroLot.trim().isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(root.get("batch").get("batchNumber"), numeroLot);
        };
    }

    public static Specification<StockMovement> buildSpecification(
            LocalDateTime dateDebut,
            LocalDateTime dateFin,
            Long produitId,
            String reference,
            MovementType type,
            String numeroLot) {

        return Specification.allOf(
                hasDateBetween(dateDebut, dateFin),
                hasProductId(produitId),
                hasProductReference(reference),
                hasMovementType(type),
                hasBatchNumber(numeroLot)
        );
    }
}

