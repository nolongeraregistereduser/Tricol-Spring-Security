package com.restapi.gestion_bons.mapper;

import com.restapi.gestion_bons.dto.atelier.AtelierCreateDTO;
import com.restapi.gestion_bons.dto.atelier.AtelierResponseDTO;
import com.restapi.gestion_bons.dto.atelier.AtelierUpdateDTO;
import com.restapi.gestion_bons.entitie.Atelier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AtelierMapper {

    AtelierResponseDTO toResponseDto(Atelier atelier);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bonDeSorties", ignore = true)
    Atelier toEntity(AtelierCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bonDeSorties", ignore = true)
    Atelier toEntity(AtelierUpdateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bonDeSorties", ignore = true)
    Atelier toEntity(AtelierResponseDTO dto);

    List<AtelierResponseDTO> toResponseDtoList(List<Atelier> ateliers);

}
