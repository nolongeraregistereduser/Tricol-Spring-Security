package com.restapi.gestion_bons.dto.stock;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockValorisationDTO {
    private BigDecimal valorisationTotale;
    private Integer nombreProduitsDistincts;
    private Integer quantiteTotaleArticles;
    private Integer nombreLotsActifs;
    private String methodeVlorisation; // FIFO
}
