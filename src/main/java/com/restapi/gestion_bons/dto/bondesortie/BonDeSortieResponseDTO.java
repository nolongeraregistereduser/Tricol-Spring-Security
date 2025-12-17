package com.restapi.gestion_bons.dto.bondesortie;

import com.restapi.gestion_bons.entitie.enums.BonDeSortieStatus;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonDeSortieResponseDTO {
    private Long id;
    private String numeroBon;
    private Date dateSortie;
    private String motifSortie;
    private BonDeSortieStatus statut;
    private AtelierRef atelier;
    
    @Builder.Default
    private List<BonDeSortieLigneDTO> bonDeSortieLignes = new ArrayList<>();
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AtelierRef {
        private Long id;
        private String nom;
    }
}
