package com.restapi.gestion_bons.dto.lot;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ResponseLotDTO extends RelationalLotDTO {
    private Long produitId;
    private Long commandeFournisseurId;
}
