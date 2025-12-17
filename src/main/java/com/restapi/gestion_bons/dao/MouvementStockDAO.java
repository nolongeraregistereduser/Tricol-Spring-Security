package com.restapi.gestion_bons.dao;

import com.restapi.gestion_bons.entitie.MouvementStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface MouvementStockDAO extends JpaRepository<MouvementStock, Long>, JpaSpecificationExecutor<MouvementStock> {
    List<MouvementStock> findAllByOrderByDateMouvementDesc();

    List<MouvementStock> findByProduitIdOrderByDateMouvementDesc(Long produitId);

    List<MouvementStock> findByLotIdOrderByDateMouvementDesc(Long lotId);

    List<MouvementStock> findByBonDeSortieIdOrderByDateMouvementDesc(Long bonDeSortieId);


    @Query("SELECT m FROM MouvementStock m WHERE m.typeMouvement = :type AND m.dateMouvement BETWEEN :start AND :ends")
    List<MouvementStock> getListDate(@Param("type") String type, @Param("start") LocalDateTime start, @Param("ends") LocalDateTime date);

//    Page<MouvementStock> findAll(Specification<MouvementStock> spec, Pageable pageable);
//    Page<MouvementStock> findAll(Pageable pageable);


}
