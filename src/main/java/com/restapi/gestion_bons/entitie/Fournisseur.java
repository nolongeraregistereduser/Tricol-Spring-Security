package com.restapi.gestion_bons.entitie;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fournisseur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Fournisseur {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "raison_sociale", nullable = false)
    private String raisonSociale;

    @NotBlank
    @Size(max = 500)
    @Column(name = "adresse_complete", nullable = false)
    private String addressComplete;

    @Size(max = 255)
    @Column(name = "personne_contact")
    private String personneContact;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
    private String telephone;

    @Size(max = 100)
    private String ville;

    @Size(max = 50)
    private String ice;

    @OneToMany(mappedBy = "fournisseur", fetch = FetchType.LAZY)
    @Builder.Default
    private List<CommandeFournisseur> commandes = new ArrayList<>();
}