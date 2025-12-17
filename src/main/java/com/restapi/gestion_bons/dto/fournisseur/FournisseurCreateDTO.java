package com.restapi.gestion_bons.dto.fournisseur;


import lombok.*;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FournisseurCreateDTO {

    private Long id;
    private String raisonSociale;
    private String addressComplete;
    private String personneContact;
    private String email;
    private String telephone;
    private String ville;
    private String ice;
}
