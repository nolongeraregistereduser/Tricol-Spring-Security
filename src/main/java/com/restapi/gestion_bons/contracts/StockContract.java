package com.restapi.gestion_bons.contracts;

import com.restapi.gestion_bons.dto.mouvementstock.MouvementStockResponseDTO;
import com.restapi.gestion_bons.dto.stock.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface StockContract {
    List<StockGlobalDTO> getStockGlobal();

    StockProduitDetailDTO getStockByProduitId(Long produitId);

    List<MouvementStockResponseDTO> getMouvements();

    List<MouvementStockResponseDTO> getMouvementsByProduitId(Long produitId);

    List<StockAlertDTO> getAlertes();

    StockValorisationDTO getValorisation();

    List<MouvementStockResponseDTO> search(
            Long id,
            String typeMouvement,
            LocalDateTime dateMouvement,
            LocalDateTime startDateMouvement,
            LocalDateTime endDateMouvement,
            Integer quantite,
            Integer minQuantite,
            Integer maxQuantite,
            Double prixUnitaireLot,
            Long produitId,
            Long lotId,
            Long bonDeSortieId
    );

    List<MouvementStockResponseDTO> getMouvementsWithFilters(
            String typeMouvement,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long produitId,
            String produitReference
    );


    // ---------------- pagination ----------------------

    Page<MouvementStockResponseDTO> getMouvementsWithFiltersPaged(
            String typeMouvement,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long produitId,
            String produitReference,
            Pageable pageable
    );

    Page<MouvementStockResponseDTO> searchPaged(
            Long id,
            String typeMouvement,
            LocalDateTime dateMouvement,
            LocalDateTime startDateMouvement,
            LocalDateTime endDateMouvement,
            Integer quantite,
            Integer minQuantite,
            Integer maxQuantite,
            Double prixUnitaireLot,
            Long produitId,
            Long lotId,
            Long bonDeSortieId,
            Pageable pageable
    );

}
