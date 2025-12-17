package com.restapi.gestion_bons.entitie;

import com.restapi.gestion_bons.entitie.enums.LotStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_lot", nullable = false, unique = true)
    private String numeroLot;

    @Column(name = "date_entree", nullable = false)
    private LocalDateTime dateEntree;

    /** Quantity initially received */
    @NotNull
    @Min(0)
    @Column(name = "quantite_initiale", nullable = false)
    private Integer quantiteInitiale;

    /** Quantity currently available in this lot */
    @NotNull
    @Min(0)
    @Column(name = "quantite_restante", nullable = false)
    private Integer quantiteRestante;

    /** Purchase unit price for valuation (FIFO base) */
    @NotNull
    @Min(0)
    @Column(name = "prix_achat_unitaire", nullable = false, precision = 12, scale = 2)
    private BigDecimal prixAchatUnitaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_fournisseur_id")
    private CommandeFournisseur commandeFournisseur;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private LotStatus statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mouvement_stock_id")
    private MouvementStock mouvementStock;

}
