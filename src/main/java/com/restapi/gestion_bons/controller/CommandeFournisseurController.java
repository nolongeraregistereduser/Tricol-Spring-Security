package com.restapi.gestion_bons.controller;

import com.restapi.gestion_bons.dto.commandefournisseur.CommandeFournisseurCreateDTO;
import com.restapi.gestion_bons.dto.commandefournisseur.CommandeFournisseurResponseDTO;
import com.restapi.gestion_bons.dto.commandefournisseur.CommandeFournisseurUpdateDTO;
import com.restapi.gestion_bons.mapper.CommandeFournisseurMapper;
import com.restapi.gestion_bons.service.commandeFournisseur.CommandeFournisseurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/commandes")
public class CommandeFournisseurController {

    private final CommandeFournisseurService commandeFournisseurService;

    public CommandeFournisseurController(CommandeFournisseurService commandeFournisseurService) {
        this.commandeFournisseurService = commandeFournisseurService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('COMMANDE_VIEW')")
    public ResponseEntity<List<CommandeFournisseurResponseDTO>> listAll(){
        List<CommandeFournisseurResponseDTO> commandes = commandeFournisseurService.findAll();
        return ResponseEntity.ok(commandes);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COMMANDE_VIEW')")
    public ResponseEntity<CommandeFournisseurResponseDTO> getCommandeFournisseurById(@PathVariable Long id){
        CommandeFournisseurResponseDTO commandeFournisseur = commandeFournisseurService.findById(id);
        return ResponseEntity.ok(commandeFournisseur);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('COMMANDE_CREATE')")
    public ResponseEntity<CommandeFournisseurResponseDTO> createCommandeFournisseur(
            @RequestBody @Valid
            CommandeFournisseurCreateDTO createDTO){
        CommandeFournisseurResponseDTO created = commandeFournisseurService.save(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('COMMANDE_UPDATE')")
    public ResponseEntity<CommandeFournisseurResponseDTO> updateCommandeFournisseur(
            @PathVariable Long id,
            @Valid @RequestBody CommandeFournisseurUpdateDTO fournisseurUpdateDTO
            ){
        CommandeFournisseurResponseDTO updated = commandeFournisseurService.update(id, fournisseurUpdateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCommandeFournisseur(
            @PathVariable Long id
    ){
        commandeFournisseurService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fournisseur/{id}")
    @PreAuthorize("hasAuthority('COMMANDE_VIEW')")
    public ResponseEntity<List<CommandeFournisseurResponseDTO>> getCommandeByFournisseurId(
            @PathVariable Long id
    ){
        List<CommandeFournisseurResponseDTO> commandes = commandeFournisseurService.findByFournisseurId(id);
        return ResponseEntity.ok(commandes);
    }

    @PutMapping("/{id}/reception")
    @PreAuthorize("hasAuthority('COMMANDE_RECEIVE')")
    public ResponseEntity<CommandeFournisseurResponseDTO> receptionnerCommande(
            @PathVariable Long id
    ){
        CommandeFournisseurResponseDTO commandeFournisseur = commandeFournisseurService.receptionnerCommande(id);
        return ResponseEntity.ok(commandeFournisseur);
    }

    @PutMapping("/{id}/valide")
    @PreAuthorize("hasAuthority('COMMANDE_VALIDATE')")
    public ResponseEntity<CommandeFournisseurResponseDTO> valideCommande(
            @PathVariable Long id
    ){
        CommandeFournisseurResponseDTO commandeFournisseur = commandeFournisseurService.valideCommande(id);
        return ResponseEntity.ok(commandeFournisseur);
    }
}