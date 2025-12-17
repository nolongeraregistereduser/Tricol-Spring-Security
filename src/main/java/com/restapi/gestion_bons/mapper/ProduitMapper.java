package com.restapi.gestion_bons.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.restapi.gestion_bons.dto.produit.ProduitResponseDTO;
import com.restapi.gestion_bons.dto.produit.ProduitRequestDTO;
import com.restapi.gestion_bons.entitie.Produit;

@Mapper(componentModel = "spring")
public interface ProduitMapper {

    ProduitResponseDTO toResponseDto(Produit produit);

    ProduitResponseDTO toResponseDto(ProduitRequestDTO produit);

    ProduitRequestDTO toProduitRequestDTO(Produit produit);

    @Mapping(target = "reorderPoint", ignore = true)
    ProduitRequestDTO toProduitRequestDTO(ProduitResponseDTO produit);

    Produit toEntity(ProduitRequestDTO dto);

    @Mapping(target = "reorderPoint", ignore = true)
    Produit toEntity(ProduitResponseDTO dto);

    List<ProduitResponseDTO> toResponseDtoList(List<Produit> produits);

    List<ProduitRequestDTO> toProduitRequestDtoList(List<Produit> produits);

    List<Produit> toEntityList(List<ProduitResponseDTO> dtos);
}
