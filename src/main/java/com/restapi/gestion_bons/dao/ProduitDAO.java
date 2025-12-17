package com.restapi.gestion_bons.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restapi.gestion_bons.entitie.Produit;

@Repository
public interface ProduitDAO extends JpaRepository<Produit, Long> {
    Produit findByReference(String reference);

    Produit findByNom(String name);

    List<Produit> findByCategorie(String category);
}
