package com.restapi.gestion_bons.mapper;

import com.restapi.gestion_bons.dto.commandefournisseur.CommandeFournisseurCreateDTO;
import com.restapi.gestion_bons.dto.commandefournisseur.CommandeFournisseurResponseDTO;
import com.restapi.gestion_bons.dto.commandefournisseur.CommandeFournisseurUpdateDTO;
import com.restapi.gestion_bons.entitie.CommandeFournisseur;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { FournisseurMapper.class,
        LigneCommandeMapper.class })
public interface CommandeFournisseurMapper {

    @Mapping(target = "fournisseurId", source = "fournisseur.id")
    CommandeFournisseurResponseDTO toResponseDto(CommandeFournisseur commandeFournisseur);

    CommandeFournisseurResponseDTO toResponseDto(CommandeFournisseurCreateDTO fournisseurCreateDTO);

    CommandeFournisseurResponseDTO toResponseDto(CommandeFournisseurUpdateDTO fournisseurUpdateDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fournisseur.id", source = "fournisseurId")
    @Mapping(target = "lignesCommande", ignore = true)
    @Mapping(target = "lots", ignore = true)
    CommandeFournisseur toEntityCreate(CommandeFournisseurCreateDTO createDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fournisseur.id", source = "fournisseurId")
    @Mapping(target = "lignesCommande", ignore = true)
    @Mapping(target = "lots", ignore = true)
    CommandeFournisseur toEntityUpdate(CommandeFournisseurUpdateDTO updateDTO);

    @Mapping(target = "lignesCommande", ignore = true)
    @Mapping(target = "fournisseur", ignore = true)
    @Mapping(target = "lots", ignore = true)
    CommandeFournisseur toEntityResponse(CommandeFournisseurResponseDTO responseDTO);

    List<CommandeFournisseurResponseDTO> toResponseDtoList(List<CommandeFournisseur> commandes);

}
