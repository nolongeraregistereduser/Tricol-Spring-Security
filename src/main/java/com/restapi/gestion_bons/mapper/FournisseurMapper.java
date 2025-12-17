package com.restapi.gestion_bons.mapper;

import com.restapi.gestion_bons.dto.fournisseur.FournisseurCreateDTO;
import com.restapi.gestion_bons.dto.fournisseur.FournisseurResponseDTO;
import com.restapi.gestion_bons.dto.fournisseur.FournisseurUpdateDTO;
import com.restapi.gestion_bons.entitie.Fournisseur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FournisseurMapper {

    FournisseurResponseDTO toResponseDto(Fournisseur fournisseur);

    FournisseurResponseDTO toResponseDto(FournisseurCreateDTO fournisseur);

    FournisseurResponseDTO toResponseDto(FournisseurUpdateDTO fournisseur);

    @Mapping(target = "commandes", ignore = true)
    Fournisseur toEntity(FournisseurCreateDTO dto);

    @Mapping(target = "commandes", ignore = true)
    Fournisseur toEntity(FournisseurUpdateDTO dto);

    @Mapping(target = "commandes", ignore = true)
    Fournisseur toEntity(FournisseurResponseDTO dto);

}
