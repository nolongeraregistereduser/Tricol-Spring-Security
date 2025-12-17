package com.restapi.gestion_bons.dto.stock;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAlertDTO {
    private Long produitId;
    private String produitReference;
    private String produitNom;
    private Integer stockActuel;
    private Integer seuilMinimum;
    private Integer quantiteAReapprovisionner;
}
