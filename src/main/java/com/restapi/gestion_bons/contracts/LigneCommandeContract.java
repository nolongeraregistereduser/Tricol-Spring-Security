package com.restapi.gestion_bons.contracts;

import com.restapi.gestion_bons.dto.lignecommande.LigneCommandeCreateDTO;
import com.restapi.gestion_bons.dto.lignecommande.LigneCommandeResponseDTO;
import com.restapi.gestion_bons.dto.lignecommande.LigneCommandeUpdateDTO;

import java.util.List;
import java.util.Optional;

public interface LigneCommandeContract {

    LigneCommandeResponseDTO save(LigneCommandeCreateDTO createDTO);

    LigneCommandeResponseDTO update(Long id, LigneCommandeUpdateDTO updateDTO);

    Optional<LigneCommandeResponseDTO> findById(Long id);

    List<LigneCommandeResponseDTO> findAll();

    void delete(Long id);

}
