package com.restapi.gestion_bons.dao;

import com.restapi.gestion_bons.entitie.BonDeSortie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BonDeSortieDAO extends JpaRepository<BonDeSortie, Long> {
    Optional<BonDeSortie> findByNumeroBon(String numeroBon);

    List<BonDeSortie> findByAtelierId(Long atelierId);
}
