package com.restapi.gestion_bons.dto.commandefournisseur;

import com.restapi.gestion_bons.entitie.enums.CommandeStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommandeFournisseurUpdateDTO {

    private Long id;
    private Date dateCommande;
    private Double montantTotal;
    private CommandeStatus statut;
    private Long fournisseurId;

}
