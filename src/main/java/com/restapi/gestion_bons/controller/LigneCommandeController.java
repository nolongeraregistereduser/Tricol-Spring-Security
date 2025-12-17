package com.restapi.gestion_bons.controller;

import com.restapi.gestion_bons.dto.lignecommande.LigneCommandeCreateDTO;
import com.restapi.gestion_bons.dto.lignecommande.LigneCommandeResponseDTO;
import com.restapi.gestion_bons.dto.lignecommande.LigneCommandeUpdateDTO;
import com.restapi.gestion_bons.service.commandeFournisseur.CommandeFournisseurService;
import com.restapi.gestion_bons.service.ligneCommande.LigneCommandeService;
import com.restapi.gestion_bons.service.produit.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/ligne-commande")
public class LigneCommandeController {

    private final LigneCommandeService ligneCommandeService;

    @Autowired
    public LigneCommandeController(
            LigneCommandeService ligneCommandeService,
            ProduitService produitService,
            CommandeFournisseurService commandeFournisseurService
    ){

        this.ligneCommandeService = ligneCommandeService;
    }

    @GetMapping
    public ResponseEntity<List<LigneCommandeResponseDTO>> getAllLigneCommande(){
        return Optional.ofNullable(ligneCommandeService.findAll())
                .filter( obj -> !obj.isEmpty())
                .map(ResponseEntity::ok)
                .orElseGet(()-> ResponseEntity.noContent().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LigneCommandeResponseDTO> getLigneCommandeById(
           @PathVariable Long id){
        return ligneCommandeService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping
    public ResponseEntity<LigneCommandeResponseDTO> createLigneCommande(
            @RequestBody LigneCommandeCreateDTO createDTO
            ){
        LigneCommandeResponseDTO created = ligneCommandeService.save(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LigneCommandeResponseDTO> updateLigneCommande(
            @PathVariable Long id,
            @RequestBody LigneCommandeUpdateDTO updateDTO
            ){
        LigneCommandeResponseDTO updated = ligneCommandeService.update(id, updateDTO);
        return  ResponseEntity.status(HttpStatus.CREATED).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLigneCommande(
            @PathVariable Long id
    ){
        ligneCommandeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/produit/{id}")
    public ResponseEntity<List<LigneCommandeResponseDTO>> getLigneCommandeByProductId(
            @PathVariable Long id
    ){
        return Optional.ofNullable(ligneCommandeService.getLigneCommandeByProduitId(id))
                .filter( obj -> !obj.isEmpty())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/commande-fournisseur/{id}")
    public ResponseEntity<List<LigneCommandeResponseDTO>> getLigneCommandeByCommandeFournisseurId(
            @PathVariable Long id
    ){
        return Optional.ofNullable(ligneCommandeService.getLigneCommandeByFournisseurCommandeId(id))
                .filter( obj -> !obj.isEmpty())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

}
