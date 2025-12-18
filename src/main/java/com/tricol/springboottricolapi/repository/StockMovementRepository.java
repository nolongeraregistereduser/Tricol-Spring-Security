package com.tricol.springboottricolapi.repository;

import com.tricol.springboottricolapi.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long>,
                                                  JpaSpecificationExecutor<StockMovement> {

    
    List<StockMovement> findByProductIdOrderByMovementDateDesc(Long productId);

    
    List<StockMovement> findAllByOrderByMovementDateDesc();

}

