package com.restapi.gestion_bons.dto.bondesortie;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonDeSortieUpdateDTO {
    private Date dateSortie;
    private String motifSortie;
    private Long atelierId;
}
