package com.restapi.gestion_bons.dao;

import com.restapi.gestion_bons.entitie.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FournisseurDAO extends JpaRepository<Fournisseur, Long> {

    Fournisseur findByEmail(String email);
}
