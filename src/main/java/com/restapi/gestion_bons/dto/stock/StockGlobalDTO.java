package com.restapi.gestion_bons.dto.stock;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockGlobalDTO {
    private Long produitId;
    private String produitReference;
    private String produitNom;
    private Integer quantiteDisponible;
    private BigDecimal valorisation;
    private Integer nombreLots;
}
