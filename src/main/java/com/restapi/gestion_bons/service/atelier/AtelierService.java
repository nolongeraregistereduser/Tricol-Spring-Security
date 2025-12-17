package com.restapi.gestion_bons.service.atelier;

import com.restapi.gestion_bons.contracts.AtelierContract;
import com.restapi.gestion_bons.dao.AtelierDAO;
import com.restapi.gestion_bons.dto.atelier.AtelierCreateDTO;
import com.restapi.gestion_bons.dto.atelier.AtelierResponseDTO;
import com.restapi.gestion_bons.dto.atelier.AtelierUpdateDTO;
import com.restapi.gestion_bons.entitie.Atelier;
import com.restapi.gestion_bons.mapper.AtelierMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AtelierService implements AtelierContract {

    private final AtelierDAO atelierDAO;
    private final AtelierMapper atelierMapper;

    @Override
    public AtelierResponseDTO save(AtelierCreateDTO dto) {
        Atelier atelier = atelierMapper.toEntity(dto);
        Atelier saved = atelierDAO.save(atelier);
        return atelierMapper.toResponseDto(saved);
    }

    @Override
    public AtelierResponseDTO update(Long id, AtelierUpdateDTO dto) {
        if (!atelierDAO.existsById(id)) {
            throw new EntityNotFoundException("Atelier non trouvé avec l'id: " + id);
        }

        Atelier toUpdate = atelierMapper.toEntity(dto);
        toUpdate.setId(id);
        Atelier updated = atelierDAO.save(toUpdate);
        return atelierMapper.toResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public AtelierResponseDTO findById(Long id) {
        return atelierDAO.findById(id)
                .map(atelierMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Atelier non trouvé avec l'id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AtelierResponseDTO> findAll() {
        return atelierDAO.findAll().stream()
                .map(atelierMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!atelierDAO.existsById(id)) {
            throw new EntityNotFoundException("Atelier non trouvé avec l'id: " + id);
        }
        atelierDAO.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public AtelierResponseDTO findByNom(String nom) {
        return atelierDAO.findByNom(nom)
                .map(atelierMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Atelier non trouvé avec le nom: " + nom));
    }
}
