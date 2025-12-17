package com.restapi.gestion_bons.util;

import com.restapi.gestion_bons.entitie.CommandeFournisseur;
import com.restapi.gestion_bons.entitie.LigneCommande;
import com.restapi.gestion_bons.entitie.Lot;
import com.restapi.gestion_bons.entitie.enums.LotStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class LotHelper {

    public List<Lot> createLotsFromLignesCommande(CommandeFournisseur commande) {
        List<Lot> lots = new ArrayList<>();

        for (LigneCommande ligne : commande.getLignesCommande()) {
            Lot lot = Lot.builder()
                    .numeroLot(generateLotNumber(ligne.getProduit(), commande))
                    .dateEntree(LocalDateTime.now())
                    .quantiteInitiale(ligne.getQuantiteCommandee())
                    .quantiteRestante(ligne.getQuantiteCommandee())
                    .prixAchatUnitaire(BigDecimal.valueOf(ligne.getPrixAchatUnitaire()))
                    .produit(ligne.getProduit())
                    .commandeFournisseur(commande)
                    .statut(LotStatus.DISPONIBLE)
                    .build();

            lots.add(lot);
        }

        return lots;
    }

    public String generateLotNumber(com.restapi.gestion_bons.entitie.Produit produit, CommandeFournisseur commande) {
        return String.format("LOT-%s-%s-%d",
                produit.getReference(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                commande.getId());
    }
}

