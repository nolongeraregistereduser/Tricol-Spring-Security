package com.restapi.gestion_bons.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.restapi.gestion_bons.dto.lot.BaseLotDTO;
import com.restapi.gestion_bons.dto.lot.ResponseLotDTO;
import com.restapi.gestion_bons.entitie.Lot;

@Mapper(componentModel = "spring", uses = { ProduitMapper.class })
public interface LotMapper {

    @Mapping(target = "commandeFournisseurId", ignore = true)
    @Mapping(target = "produitId", source = "produit.id")
    ResponseLotDTO toResponseDto(Lot lot);

    @Mapping(target = "produit.id", source = "produitId")
    @Mapping(target = "commandeFournisseur.id", source = "commandeFournisseurId")
    @Mapping(target = "mouvementStock", ignore = true)
    Lot toEntity(ResponseLotDTO dto);

    @Mapping(target = "produit.id", source = "produitId")
    @Mapping(target = "mouvementStock", ignore = true)
    @Mapping(target = "commandeFournisseur", ignore = true)
    Lot toEntity(BaseLotDTO bsl);

    List<ResponseLotDTO> toResponseDtoList(List<Lot> lots);

    List<Lot> toEntityList(List<ResponseLotDTO> dtos);
}
