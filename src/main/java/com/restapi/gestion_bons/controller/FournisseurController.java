package com.restapi.gestion_bons.controller;

import com.restapi.gestion_bons.dto.fournisseur.FournisseurCreateDTO;
import com.restapi.gestion_bons.dto.fournisseur.FournisseurResponseDTO;
import com.restapi.gestion_bons.dto.fournisseur.FournisseurUpdateDTO;
import com.restapi.gestion_bons.service.fournisseur.FournisseurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/fournisseurs")
public class FournisseurController {
    private final FournisseurService fournisseurService;

    public FournisseurController(FournisseurService fournisseurService) {
        this.fournisseurService = fournisseurService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('FOURNISSEUR_VIEW')")
    public ResponseEntity<List<FournisseurResponseDTO>> listAll() {
        List<FournisseurResponseDTO> fournisseurs = fournisseurService.findAll();
        return ResponseEntity.ok(fournisseurs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FOURNISSEUR_VIEW')")
    public ResponseEntity<FournisseurResponseDTO> getFournisseurById(@PathVariable Long id) {
        return ResponseEntity.ok(fournisseurService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FOURNISSEUR_CREATE')")
    public ResponseEntity<FournisseurResponseDTO> createFournisseur(
            @Valid @RequestBody FournisseurCreateDTO createDTO) {
        FournisseurResponseDTO created = fournisseurService.save(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FOURNISSEUR_UPDATE')")
    public ResponseEntity<FournisseurResponseDTO> updateFournisseur(
            @PathVariable Long id,
            @Valid @RequestBody FournisseurUpdateDTO updateDTO) {
        FournisseurResponseDTO updated = fournisseurService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FOURNISSEUR_DELETE')")
    public ResponseEntity<Void> deleteFournisseur(@PathVariable Long id) {
        fournisseurService.delete(id);
        return ResponseEntity.noContent().build();
    }
}