package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.dto.SupplierRequestDTO;
import com.tricol.springboottricolapi.dto.SupplierResponseDTO;

import java.util.List;

public interface ISupplierService {
    List<SupplierResponseDTO> getAllSuppliers();
    SupplierResponseDTO getSupplierById(Long id);
    SupplierResponseDTO createSupplier(SupplierRequestDTO requestDTO);
    SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO requestDTO);
    void deleteSupplier(Long id);
}
