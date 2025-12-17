package com.restapi.gestion_bons.dao;

import com.restapi.gestion_bons.entitie.BonDeSortieLigne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BonDeSortieLigneDAO extends JpaRepository<BonDeSortieLigne, Long> {
}
