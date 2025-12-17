package com.restapi.gestion_bons.contracts;

import java.util.List;

import com.restapi.gestion_bons.dto.fournisseur.FournisseurCreateDTO;
import com.restapi.gestion_bons.dto.fournisseur.FournisseurResponseDTO;
import com.restapi.gestion_bons.dto.fournisseur.FournisseurUpdateDTO;

public interface FournisseurContract {
    FournisseurResponseDTO save(FournisseurCreateDTO dto);

    FournisseurResponseDTO update(Long id, FournisseurUpdateDTO dto);

    FournisseurResponseDTO findById(Long id);

    List<FournisseurResponseDTO> findAll();

    void delete(Long id);
}
