package com.restapi.gestion_bons.dto.lignecommande;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LigneCommandeCreateDTO {

//    private Long id; be cause w dont need it we ganarate the id by self
    private Long commandeId;
    private Long produitId;
    private Integer quantiteCommandee;
    private Double prixAchatUnitaire;

}
