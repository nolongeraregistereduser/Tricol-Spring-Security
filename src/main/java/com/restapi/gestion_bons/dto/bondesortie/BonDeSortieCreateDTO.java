package com.restapi.gestion_bons.dto.bondesortie;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BonDeSortieCreateDTO {

    private String numeroBon;

    @NotNull(message = "La date de sortie est obligatoire")
    private Date dateSortie;

    @NotNull(message = "Le motif de sortie est obligatoire")
    private String motifSortie;

    @NotNull(message = "L'identifiant de l'atelier est obligatoire")
    private Long atelierId;

    @NotEmpty(message = "Au moins une ligne est requise")
    @Builder.Default
    private List<BonDeSortieLigneDTO> lignes = new ArrayList<>();
}
