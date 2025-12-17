package com.restapi.gestion_bons.service.fournisseur;

import com.restapi.gestion_bons.dao.FournisseurDAO;
import com.restapi.gestion_bons.dto.fournisseur.FournisseurCreateDTO;
import com.restapi.gestion_bons.dto.fournisseur.FournisseurResponseDTO;
import com.restapi.gestion_bons.dto.fournisseur.FournisseurUpdateDTO;
import com.restapi.gestion_bons.entitie.Fournisseur;
import com.restapi.gestion_bons.mapper.FournisseurMapper;
import com.restapi.gestion_bons.contracts.FournisseurContract;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FournisseurService implements FournisseurContract {
    private final FournisseurDAO fournisseurDAO;
    private final FournisseurMapper fournisseurMapper;

    public FournisseurService(FournisseurDAO fournisseurDAO, FournisseurMapper fournisseurMapper) {
        this.fournisseurDAO = fournisseurDAO;
        this.fournisseurMapper = fournisseurMapper;
    }

    public boolean existes(Long id){
        return fournisseurDAO.existsById(id);
    }

    public List<FournisseurResponseDTO> findAll() {
        return fournisseurDAO.findAll().stream()
                .map(fournisseurMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public FournisseurResponseDTO findById(Long id) {
        return fournisseurDAO.findById(id)
                .map(fournisseurMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Fournisseur not found with id: " + id));
    }

    public FournisseurResponseDTO save(FournisseurCreateDTO createDTO) {
        Fournisseur fournisseur = fournisseurMapper.toEntity(createDTO);
        Fournisseur saved = fournisseurDAO.save(fournisseur);
        return fournisseurMapper.toResponseDto(saved);
    }

    public FournisseurResponseDTO update(Long id, FournisseurUpdateDTO updateDTO) {
        if (!fournisseurDAO.existsById(id)) {
            throw new EntityNotFoundException("Fournisseur not found with id: " + id);
        }

        Fournisseur toUpdate = fournisseurMapper.toEntity(updateDTO);
        toUpdate.setId(id);
        Fournisseur updated = fournisseurDAO.save(toUpdate);
        return fournisseurMapper.toResponseDto(updated);
    }

    public void delete(Long id) {
        if (!fournisseurDAO.existsById(id)) {
            throw new EntityNotFoundException("Fournisseur not found with id: " + id);
        }
        fournisseurDAO.deleteById(id);
    }
}