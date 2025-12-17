package com.restapi.gestion_bons.dto.stock;

import com.restapi.gestion_bons.entitie.enums.LotStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockProduitDetailDTO {
    private Long produitId;
    private String produitReference;
    private String produitNom;
    private Integer quantiteTotale;
    private BigDecimal valorisationFIFO;
    private List<LotInfo> lots;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LotInfo {
        private Long lotId;
        private String numeroLot;
        private LocalDateTime dateEntree;
        private Integer quantiteInitiale;
        private Integer quantiteRestante;
        private BigDecimal prixAchatUnitaire;
        private LotStatus statut;
    }
}
