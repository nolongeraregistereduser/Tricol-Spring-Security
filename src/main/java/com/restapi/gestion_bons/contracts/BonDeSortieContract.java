package com.restapi.gestion_bons.contracts;

import java.util.List;

import com.restapi.gestion_bons.dto.bondesortie.BonDeSortieCreateDTO;
import com.restapi.gestion_bons.dto.bondesortie.BonDeSortieResponseDTO;
import com.restapi.gestion_bons.dto.bondesortie.BonDeSortieUpdateDTO;
import com.restapi.gestion_bons.entitie.BonDeSortie;
import com.restapi.gestion_bons.entitie.BonDeSortieLigne;

public interface BonDeSortieContract {
    BonDeSortieResponseDTO save(BonDeSortieCreateDTO dto);

    BonDeSortieResponseDTO update(Long id, BonDeSortieUpdateDTO dto);

    BonDeSortieResponseDTO findById(Long id);

    List<BonDeSortieResponseDTO> findAll();

    void delete(Long id);

    BonDeSortieResponseDTO findByNumeroBon(String numeroBon);

    List<BonDeSortieResponseDTO> findByAtelierId(Long atelierId);

    BonDeSortieResponseDTO valider(Long id);

    BonDeSortieResponseDTO annuler(Long id);

    void traiterSortieFIFO(BonDeSortieLigne ligne, BonDeSortie bonDeSortie);

    }
