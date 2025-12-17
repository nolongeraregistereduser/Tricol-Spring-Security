package com.restapi.gestion_bons.dto.bondesortie;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonDeSortieLigneDTO {
    private Long id;
    
    @NotNull(message = "L'identifiant du produit est obligatoire")
    private Long produitId;
    
    @NotNull(message = "La quantité demandée est obligatoire")
    @Positive(message = "La quantité demandée doit être positive")
    private Integer quantiteDemandee;
    
    private ProduitRef produit;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProduitRef {
        private Long id;
        private String reference;
        private String nom;
    }
}
