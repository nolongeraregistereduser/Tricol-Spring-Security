package com.restapi.gestion_bons.entitie;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "reference", unique = true)
    private String reference;

    @Column(name = "nom", unique = true)
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;

    @Column(name = "description")
    @Size(min = 2, max = 500, message = "La description doit contenir entre 2 et 500 caractères")
    private String description;

    @Column(name = "categorie")
    @Size(min = 2, max = 100, message = "La catégorie doit contenir entre 2 et 100 caractères")
    private String categorie;

    @Column(name = "unite_mesure")
    private String uniteMesure;

    @Column(name = "point_reapprovisionnement")
    private Integer reorderPoint;

    @OneToMany(mappedBy = "produit", fetch = FetchType.LAZY)
    private final List<LigneCommande> lignesCommande = new ArrayList<>();

    @OneToMany(mappedBy = "produit", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final List<Lot> lots = new ArrayList<>();

}