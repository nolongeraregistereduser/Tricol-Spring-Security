package com.restapi.gestion_bons.dao;

import com.restapi.gestion_bons.entitie.LigneCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneCommandeDAO extends JpaRepository<LigneCommande, Long> {

    List<LigneCommande> findLigneCommandeByProduit_Id(Long commandeId);

    List<LigneCommande> findLigneCommandeByCommande_Fournisseur_Id(Long commandeFournisseurId);
}
