package com.restapi.gestion_bons.dao;

import com.restapi.gestion_bons.entitie.Lot;
import com.restapi.gestion_bons.entitie.enums.LotStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LotDAO extends JpaRepository<Lot, Long> {
    List<Lot> findByProduitIdAndStatut(Long produitId, LotStatus statut);

    List<Lot> findByProduitIdOrderByDateEntreeAsc(Long produitId);

    List<Lot> findByProduitIdAndStatutOrderByDateEntreeAsc(Long produitId, LotStatus statut);

    List<Lot> findByStatut(LotStatus statut);

}
