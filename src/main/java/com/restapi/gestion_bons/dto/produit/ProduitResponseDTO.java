package com.restapi.gestion_bons.dto.produit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProduitResponseDTO {
    private String id;
    private String reference;
    private String nom;
    private String description;
    private String categorie;
    private String uniteMesure;
}
