package com.restapi.gestion_bons.contracts;

import java.util.List;

import com.restapi.gestion_bons.dto.atelier.AtelierCreateDTO;
import com.restapi.gestion_bons.dto.atelier.AtelierResponseDTO;
import com.restapi.gestion_bons.dto.atelier.AtelierUpdateDTO;

public interface AtelierContract {
    AtelierResponseDTO save(AtelierCreateDTO dto);

    AtelierResponseDTO update(Long id, AtelierUpdateDTO dto);

    AtelierResponseDTO findById(Long id);

    List<AtelierResponseDTO> findAll();

    void delete(Long id);

    AtelierResponseDTO findByNom(String nom);
}
