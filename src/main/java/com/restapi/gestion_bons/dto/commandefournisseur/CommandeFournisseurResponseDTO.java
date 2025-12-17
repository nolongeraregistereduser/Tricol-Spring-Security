package com.restapi.gestion_bons.dto.commandefournisseur;

import com.restapi.gestion_bons.entitie.enums.CommandeStatus;

import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandeFournisseurResponseDTO {

    private Long id;
    private Date dateCommande;
    private Double montantTotal;
    private CommandeStatus statut;
    private Long fournisseurId;

}
