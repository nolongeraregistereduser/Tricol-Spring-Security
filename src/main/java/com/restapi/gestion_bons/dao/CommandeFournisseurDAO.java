package com.restapi.gestion_bons.dao;

import com.restapi.gestion_bons.entitie.CommandeFournisseur;
import com.restapi.gestion_bons.entitie.enums.CommandeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandeFournisseurDAO extends JpaRepository<CommandeFournisseur, Long> {

    List<CommandeFournisseur> findByStatut(CommandeStatus statut);

    List<CommandeFournisseur> findByFournisseurId(Long fournisseurId);
}
