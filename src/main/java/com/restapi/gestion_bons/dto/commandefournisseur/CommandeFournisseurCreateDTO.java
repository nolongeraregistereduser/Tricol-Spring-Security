package com.restapi.gestion_bons.dto.commandefournisseur;


import com.restapi.gestion_bons.dto.lignecommande.LigneCommandeCreateDTO;
import com.restapi.gestion_bons.entitie.enums.CommandeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommandeFournisseurCreateDTO {

    private Long id;
    private Date dateCommande;
    private Double montantTotal;
    private CommandeStatus statut;
    private Long fournisseurId;


    @Builder.Default
    private List<LigneCommandeCreateDTO> lignesCommande = new ArrayList<>();
}
