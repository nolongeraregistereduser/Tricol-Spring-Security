package com.restapi.gestion_bons.controller;

import com.restapi.gestion_bons.contracts.BonDeSortieContract;
import com.restapi.gestion_bons.dto.bondesortie.BonDeSortieCreateDTO;
import com.restapi.gestion_bons.dto.bondesortie.BonDeSortieResponseDTO;
import com.restapi.gestion_bons.dto.bondesortie.BonDeSortieUpdateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/bons-sortie")
@RequiredArgsConstructor
public class BonDeSortieController {

    private final BonDeSortieContract bonDeSortieService;

    @GetMapping
    @PreAuthorize("hasAuthority('BON_SORTIE_VIEW')")
    public ResponseEntity<List<BonDeSortieResponseDTO>> listAll() {
        return ResponseEntity.ok(bonDeSortieService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('BON_SORTIE_VIEW')")
    public ResponseEntity<BonDeSortieResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bonDeSortieService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('BON_SORTIE_CREATE')")
    public ResponseEntity<BonDeSortieResponseDTO> create(@RequestBody @Valid BonDeSortieCreateDTO dto) {
        BonDeSortieResponseDTO created = bonDeSortieService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BON_SORTIE_UPDATE')")
    public ResponseEntity<BonDeSortieResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid BonDeSortieUpdateDTO dto) {
        return ResponseEntity.ok(bonDeSortieService.update(id, dto));
    }

    @PutMapping("/{id}/valider")
    @PreAuthorize("hasAuthority('BON_SORTIE_VALIDATE')")
    public ResponseEntity<BonDeSortieResponseDTO> valider(@PathVariable Long id) {
        return ResponseEntity.ok(bonDeSortieService.valider(id));
    }

    @PutMapping("/{id}/annuler")
    @PreAuthorize("hasAuthority('BON_SORTIE_CANCEL')")
    public ResponseEntity<BonDeSortieResponseDTO> annuler(@PathVariable Long id) {
        return ResponseEntity.ok(bonDeSortieService.annuler(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bonDeSortieService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/numero/{numeroBon}")
    @PreAuthorize("hasAuthority('BON_SORTIE_VIEW')")
    public ResponseEntity<BonDeSortieResponseDTO> getByNumeroBon(@PathVariable String numeroBon) {
        return ResponseEntity.ok(bonDeSortieService.findByNumeroBon(numeroBon));
    }

    @GetMapping("/atelier/{atelierId}")
    @PreAuthorize("hasAuthority('BON_SORTIE_VIEW')")
    public ResponseEntity<List<BonDeSortieResponseDTO>> getByAtelierId(@PathVariable Long atelierId) {
        return ResponseEntity.ok(bonDeSortieService.findByAtelierId(atelierId));
    }
}