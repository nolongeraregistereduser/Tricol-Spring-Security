package com.restapi.gestion_bons.controller;

import com.restapi.gestion_bons.contracts.AtelierContract;
import com.restapi.gestion_bons.dto.atelier.AtelierCreateDTO;
import com.restapi.gestion_bons.dto.atelier.AtelierResponseDTO;
import com.restapi.gestion_bons.dto.atelier.AtelierUpdateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/ateliers")
@RequiredArgsConstructor
public class AtelierController {

    private final AtelierContract atelierService;

    @GetMapping
    public ResponseEntity<List<AtelierResponseDTO>> listAll() {
        return ResponseEntity.ok(atelierService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtelierResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(atelierService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AtelierResponseDTO> create(@RequestBody @Valid AtelierCreateDTO dto) {
        AtelierResponseDTO created = atelierService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AtelierResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid AtelierUpdateDTO dto) {
        return ResponseEntity.ok(atelierService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        atelierService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nom/{nom}")
    public ResponseEntity<AtelierResponseDTO> getByNom(@PathVariable String nom) {
        return ResponseEntity.ok(atelierService.findByNom(nom));
    }
}
