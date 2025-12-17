package com.restapi.gestion_bons.dto.atelier;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtelierUpdateDTO {

    @NotBlank(message = "Le nom de l'atelier est obligatoire")
    private String nom;
    
}
