package com.restapi.gestion_bons.service.lot;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.restapi.gestion_bons.contracts.LotContract;
import com.restapi.gestion_bons.dao.LotDAO;
import com.restapi.gestion_bons.dto.lot.BaseLotDTO;
import com.restapi.gestion_bons.dto.lot.ResponseLotDTO;
import com.restapi.gestion_bons.entitie.CommandeFournisseur;
import com.restapi.gestion_bons.entitie.Lot;
import com.restapi.gestion_bons.mapper.LotMapper;

@Service
@Primary
@RequiredArgsConstructor
public class LotService implements LotContract {
    private final LotDAO lotDAO;
    private final LotMapper lotMapper;

    @Override
    public ResponseLotDTO save(BaseLotDTO dto, CommandeFournisseur cf) {
        if (dto == null) {
            throw new IllegalArgumentException("BaseStockLotDTO must not be null");
        }
        if (dto.getProduitId() == null) {
            throw new IllegalArgumentException("produitId must be provided when creating a stock lot");
        }

        Lot lot = lotMapper.toEntity(dto);
        Lot saved = lotDAO.save(lot);

        ResponseLotDTO response = lotMapper.toResponseDto(saved);
        // populate commande ref if present
        if (cf != null) {
            response.setCommandeFournisseur(cf);
        }
        return response;
    }

    @Override
    public ResponseLotDTO update(Long id, BaseLotDTO dto, CommandeFournisseur cf) {
        if (id == null)
            throw new IllegalArgumentException("id must not be null");

        lotDAO.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Lot not found with id: " + id));

        Lot toUpdate = lotMapper.toEntity(dto);
        toUpdate.setId(id);
        Lot updated = lotDAO.save(toUpdate);

        ResponseLotDTO response = lotMapper.toResponseDto(updated);
        if (cf != null) {
            response.setCommandeFournisseur(cf);
        }
        return response;
    }

    @Override
    public ResponseLotDTO findById(Long id) {
        Lot found = lotDAO.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Lot not found with id: " + id));
        ResponseLotDTO response = lotMapper.toResponseDto(found);
        return response;
    }

    @Override
    public List<ResponseLotDTO> findAll() {
        List<Lot> all = lotDAO.findAll();
        return all.stream().map(lotMapper::toResponseDto).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!lotDAO.existsById(id)) {
            throw new NoSuchElementException("Lot not found with id: " + id);
        }
        lotDAO.deleteById(id);
    }
}
