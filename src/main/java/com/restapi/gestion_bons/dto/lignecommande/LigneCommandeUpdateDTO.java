package com.restapi.gestion_bons.dto.lignecommande;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LigneCommandeUpdateDTO {

//    private Long id; we dont update id !
    private Long commandeId;
    private Long produitId;
    private Integer quantiteCommandee;
    private Double prixAchatUnitaire;
}
