package com.restapi.gestion_bons.controller;

import com.restapi.gestion_bons.dto.produit.ProduitResponseDTO;
import com.restapi.gestion_bons.contracts.ProduitServiceContract;
import com.restapi.gestion_bons.dto.produit.ProduitRequestDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/v1/produits")
@RequiredArgsConstructor
public class ProduitController {
    private final ProduitServiceContract produitService;

    @GetMapping
    public List<ProduitResponseDTO> listAll() {
        return produitService.findAll();
    }

    @GetMapping("/paginated")  // ✅ Add pagination endpoint
    public ResponseEntity<Page<ProduitResponseDTO>> listAllPaginated(Pageable pageable) {
        Page<ProduitResponseDTO> produits = produitService.findAllWithPagination(pageable);
        return ResponseEntity.ok(produits);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProduitResponseDTO> getById(@PathVariable Long id) {
        return produitService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProduitResponseDTO> create(@RequestBody @Valid ProduitRequestDTO dto) {
        ProduitResponseDTO saved = produitService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProduitResponseDTO> update(@PathVariable Long id, @RequestBody @Valid ProduitRequestDTO dto) {
        try {
            ProduitResponseDTO updated = produitService.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (produitService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        produitService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ByName/{name}")
    public ResponseEntity<ProduitResponseDTO> getByName(@PathVariable String name) {
        return produitService.findByNom(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ByReference/{reference}")
    public ResponseEntity<ProduitResponseDTO> getByReference(@PathVariable String reference) {
        return produitService.findByReference(reference)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ByCategory/{category}")
    public ResponseEntity<List<ProduitResponseDTO>> getByCategory(@PathVariable String category) {
        List<ProduitResponseDTO> produits = produitService.findByCategorie(category);
        if (produits.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(produits);
    }

    @PostMapping("/initdb")
    public ResponseEntity<List<ProduitResponseDTO>> initDb(){
        return ResponseEntity.ok(produitService.initDB());
    }
}