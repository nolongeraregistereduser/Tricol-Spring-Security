package com.restapi.gestion_bons.dto.mouvementstock;

import com.restapi.gestion_bons.entitie.enums.TypeMouvement;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseMouvementStockDTO {
    private Long id;
    private TypeMouvement typeMouvement;
    private LocalDateTime dateMouvement;
    private Integer quantite;
    private Double prixUnitaireLot;
}
