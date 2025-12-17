package com.restapi.gestion_bons.dto.mouvementstock;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class MouvementStockResponseDTO extends RelationalMouvementStockDTO {
    // inherits all relational fields from RelationalMouvementStockDTO
}
