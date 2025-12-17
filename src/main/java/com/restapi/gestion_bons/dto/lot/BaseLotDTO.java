package com.restapi.gestion_bons.dto.lot;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.restapi.gestion_bons.entitie.enums.LotStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseLotDTO {
    // non-relational properties only
    private Long id;
    private String numeroLot;
    private Long produitId;
    private LocalDateTime dateEntree;
    private Integer quantiteInitiale;
    private Integer quantiteRestante;
    private BigDecimal prixAchatUnitaire;
    private LotStatus statut;
}
