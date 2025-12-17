package com.restapi.gestion_bons.dto.mouvementstock;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MouvementStockRequestDTO extends BaseMouvementStockDTO {

    @NotNull(message = "L'identifiant du produit est obligatoire")
    private Long produitId;

    @NotNull(message = "L'identifiant du lot est obligatoire")
    private Long lotId;

    @NotNull(message = "L'identifiant du bon de sortie est obligatoire")
    private Long bonDeSortieId;
}
