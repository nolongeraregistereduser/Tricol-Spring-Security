package com.restapi.gestion_bons.contracts;

import java.util.List;

import com.restapi.gestion_bons.dto.mouvementstock.MouvementStockRequestDTO;
import com.restapi.gestion_bons.dto.mouvementstock.MouvementStockResponseDTO;

public interface MouvementStockContract {
    MouvementStockResponseDTO save(MouvementStockRequestDTO dto);

    MouvementStockResponseDTO findById(Long id);

    List<MouvementStockResponseDTO> findAll();

    List<MouvementStockResponseDTO> findByProduitId(Long produitId);

    List<MouvementStockResponseDTO> findByLotId(Long lotId);

    List<MouvementStockResponseDTO> findByBonDeSortieId(Long bonDeSortieId);
}
