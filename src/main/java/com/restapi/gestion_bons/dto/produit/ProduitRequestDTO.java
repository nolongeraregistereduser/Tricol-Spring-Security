package com.restapi.gestion_bons.dto.produit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProduitRequestDTO {
    private Long id;

    @NotBlank(message = "La référence est obligatoire")
    @Size(max = 100, message = "La référence ne doit pas dépasser 100 caractères")
    private String reference;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 2, max = 500, message = "La description doit contenir entre 2 et 500 caractères")
    private String description;

    @NotBlank(message = "La catégorie est obligatoire")
    @Size(min = 2, max = 100, message = "La catégorie doit contenir entre 2 et 100 caractères")
    private String categorie;

    @NotBlank(message = "L'unité de mesure est obligatoire")
    private String uniteMesure;

    @NotBlank(message = "Le point de réapprovisionnement est obligatoire")
    private Integer reorderPoint;
}
