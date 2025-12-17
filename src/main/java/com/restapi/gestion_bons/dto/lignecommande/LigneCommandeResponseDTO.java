package com.restapi.gestion_bons.dto.lignecommande;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LigneCommandeResponseDTO {

    private Long id;
    private Long commandeId;
    private Long produitId;
    private Integer quantiteCommandee;
    private Double prixAchatUnitaire;

}
