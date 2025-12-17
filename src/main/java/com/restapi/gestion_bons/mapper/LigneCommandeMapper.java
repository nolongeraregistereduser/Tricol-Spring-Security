package com.restapi.gestion_bons.mapper;

import com.restapi.gestion_bons.dto.lignecommande.LigneCommandeCreateDTO;
import com.restapi.gestion_bons.dto.lignecommande.LigneCommandeResponseDTO;
import com.restapi.gestion_bons.dto.lignecommande.LigneCommandeUpdateDTO;
import com.restapi.gestion_bons.entitie.LigneCommande;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = { ProduitMapper.class })
public interface LigneCommandeMapper {

    // Existing mappings
    @Mapping(target = "commandeId", source = "commande.id")
    @Mapping(target = "produitId", source = "produit.id")
    LigneCommandeResponseDTO toResponseDto(LigneCommande ligneCommande);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produit.id", ignore = true)
    @Mapping(target = "commande", ignore = true)
    LigneCommande toEntity(LigneCommandeCreateDTO createDTO);

    @Mapping(target = "commande", ignore = true)
    @Mapping(target = "produit", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(LigneCommandeUpdateDTO updateDTO, @MappingTarget LigneCommande ligneCommande);

    // List Mapping
    List<LigneCommandeResponseDTO> toResponseDtoList(List<LigneCommande> ligneCommandes);

    // Incoming mappings to add
    @Mapping(target = "produit.id", source = "produitId")
    @Mapping(target = "commande", ignore = true)
    @Mapping(target = "id", ignore = true)
    LigneCommande toEntity(LigneCommandeUpdateDTO dto);

    List<LigneCommande> toEntityList(List<LigneCommandeCreateDTO> dtos);

}
