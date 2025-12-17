package com.restapi.gestion_bons.contracts;

import java.util.List;

import com.restapi.gestion_bons.dto.lot.BaseLotDTO;
import com.restapi.gestion_bons.dto.lot.ResponseLotDTO;
import com.restapi.gestion_bons.entitie.CommandeFournisseur;

public interface LotContract {
    ResponseLotDTO save(BaseLotDTO dto, CommandeFournisseur cf);

    ResponseLotDTO update(Long id, BaseLotDTO dto, CommandeFournisseur cf);

    ResponseLotDTO findById(Long id);

    List<ResponseLotDTO> findAll();

    void delete(Long id);
}
