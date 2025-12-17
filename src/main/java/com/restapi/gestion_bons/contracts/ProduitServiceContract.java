package com.restapi.gestion_bons.contracts;

import java.util.List;
import java.util.Optional;

import com.restapi.gestion_bons.dto.produit.ProduitResponseDTO;
import com.restapi.gestion_bons.dto.produit.ProduitRequestDTO;
import org.springframework.data.domain.*;

public interface ProduitServiceContract {

    Page<ProduitResponseDTO> findAllWithPagination(Pageable pageable);

    List<ProduitResponseDTO> findAll();

    Optional<ProduitResponseDTO> findById(Long id);

    ProduitResponseDTO save(ProduitRequestDTO dto);

    ProduitResponseDTO update(Long id, ProduitRequestDTO dto);

    void delete(Long id);

    Optional<ProduitResponseDTO> findByNom(String name);

    Optional<ProduitResponseDTO> findByReference(String reference);

    List<ProduitResponseDTO> findByCategorie(String category);

    List<ProduitResponseDTO> initDB();
}
