package com.restapi.gestion_bons.dto.fournisseur;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FournisseurUpdateDTO {

    private Long id;
    private String raisonSociale;
    private String addressComplete;
    private String personneContact;
    private String email;
    private String telephone;
    private String ville;
    private String ice;
}
