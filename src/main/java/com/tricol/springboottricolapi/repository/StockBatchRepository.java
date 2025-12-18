package com.tricol.springboottricolapi.repository;

import com.tricol.springboottricolapi.entity.StockBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockBatchRepository extends JpaRepository<StockBatch, Long> {


    @Query("SELECT sb FROM StockBatch sb WHERE sb.product.id = :productId " +
            "AND sb.remainingQuantity > 0 ORDER BY sb.entryDate ASC, sb.id ASC")
    List<StockBatch> findAvailableBatchesByProductIdOrderedByFifo(@Param("productId") Long productId);


    List<StockBatch> findByProductIdOrderByEntryDateDesc(Long productId);


    boolean existsByBatchNumber(String batchNumber);


    
}

