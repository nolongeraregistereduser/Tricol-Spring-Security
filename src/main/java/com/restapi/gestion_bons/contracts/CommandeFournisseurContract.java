package com.restapi.gestion_bons.contracts;

import java.util.List;

import com.restapi.gestion_bons.dto.commandefournisseur.CommandeFournisseurCreateDTO;
import com.restapi.gestion_bons.dto.commandefournisseur.CommandeFournisseurResponseDTO;
import com.restapi.gestion_bons.dto.commandefournisseur.CommandeFournisseurUpdateDTO;

public interface CommandeFournisseurContract {
    CommandeFournisseurResponseDTO save(CommandeFournisseurCreateDTO dto);

    CommandeFournisseurResponseDTO update(Long id, CommandeFournisseurUpdateDTO dto);

    CommandeFournisseurResponseDTO findById(Long id);

    List<CommandeFournisseurResponseDTO> findAll();

    void delete(Long id);

    List<CommandeFournisseurResponseDTO> findByFournisseurId(Long fournisseurId);
}
