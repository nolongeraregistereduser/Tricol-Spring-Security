package com.restapi.gestion_bons.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.restapi.gestion_bons.dto.bondesortie.BonDeSortieLigneDTO;
import com.restapi.gestion_bons.entitie.BonDeSortieLigne;

import java.util.List;

@Mapper(componentModel = "spring", uses = { ProduitMapper.class })
public interface BonDeSortieLigneMapper {

    @Mapping(target = "produit.id", source = "produit.id")
    @Mapping(target = "produit.nom", source = "produit.nom")
    @Mapping(target = "produit.reference", source = "produit.reference")
    @Mapping(target = "produitId", ignore = true)
    BonDeSortieLigneDTO toDto(BonDeSortieLigne ligne);

    @Mapping(target = "produit.id", source = "produitId")
    @Mapping(target = "bonDeSortie", ignore = true)
    @Mapping(target = "id", ignore = true)
    BonDeSortieLigne toEntity(BonDeSortieLigneDTO dto);

    List<BonDeSortieLigneDTO> toDtoList(List<BonDeSortieLigne> lignes);

}
