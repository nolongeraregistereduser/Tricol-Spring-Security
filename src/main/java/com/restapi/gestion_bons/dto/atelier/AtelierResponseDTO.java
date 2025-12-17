package com.restapi.gestion_bons.dto.atelier;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtelierResponseDTO {

    private Long id;
    private String nom;
    
}
