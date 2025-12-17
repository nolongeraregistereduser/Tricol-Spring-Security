package com.restapi.gestion_bons.entitie;

import com.restapi.gestion_bons.entitie.enums.CommandeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "commande_fournisseur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeFournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_commande", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateCommande;

    @Column(name = "montant_total", nullable = false)
    private Double montantTotal;

    @Column(name = "statut", nullable = false)
    @Enumerated(EnumType.STRING)
    private CommandeStatus statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fournisseur_id", nullable = false)
    private Fournisseur fournisseur;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LigneCommande> lignesCommande = new ArrayList<>();

    @OneToMany(mappedBy = "commandeFournisseur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Lot> lots = new ArrayList<>();

}
