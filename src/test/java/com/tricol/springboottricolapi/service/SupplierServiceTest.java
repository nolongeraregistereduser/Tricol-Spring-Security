package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.dto.SupplierRequestDTO;
import com.tricol.springboottricolapi.dto.SupplierResponseDTO;
import com.tricol.springboottricolapi.entity.Supplier;
import com.tricol.springboottricolapi.exception.ResourceNotFoundException;
import com.tricol.springboottricolapi.mapper.SupplierMapper;
import com.tricol.springboottricolapi.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier supplier;
    private SupplierRequestDTO requestDTO;
    private SupplierResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        supplier = Supplier.builder()
                .id(1L)
                .raisonSociale("Test Supplier")
                .email("supplier@test.com")
                .phone("123456789")
                .build();

        requestDTO = new SupplierRequestDTO();
        requestDTO.setRaisonSociale("Test Supplier");
        requestDTO.setEmail("supplier@test.com");

        responseDTO = new SupplierResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setRaisonSociale("Test Supplier");
    }

    @Test
    void getAllSuppliers_ShouldReturnList() {
        when(supplierRepository.findAll()).thenReturn(Arrays.asList(supplier));
        when(supplierMapper.toResponseDTO(any())).thenReturn(responseDTO);

        List<SupplierResponseDTO> result = supplierService.getAllSuppliers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(supplierRepository).findAll();
    }

    @Test
    void getSupplierById_WhenExists_ShouldReturnSupplier() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierMapper.toResponseDTO(supplier)).thenReturn(responseDTO);

        SupplierResponseDTO result = supplierService.getSupplierById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(supplierRepository).findById(1L);
    }

    @Test
    void getSupplierById_WhenNotExists_ShouldThrowException() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> supplierService.getSupplierById(1L));
    }

    @Test
    void createSupplier_ShouldCreateAndReturnSupplier() {
        when(supplierMapper.toEntity(requestDTO)).thenReturn(supplier);
        when(supplierRepository.save(supplier)).thenReturn(supplier);
        when(supplierMapper.toResponseDTO(supplier)).thenReturn(responseDTO);

        SupplierResponseDTO result = supplierService.createSupplier(requestDTO);

        assertNotNull(result);
        verify(supplierRepository).save(supplier);
    }

    @Test
    void updateSupplier_WhenExists_ShouldUpdate() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepository.save(supplier)).thenReturn(supplier);
        when(supplierMapper.toResponseDTO(supplier)).thenReturn(responseDTO);

        SupplierResponseDTO result = supplierService.updateSupplier(1L, requestDTO);

        assertNotNull(result);
        verify(supplierMapper).updateEntity(requestDTO, supplier);
        verify(supplierRepository).save(supplier);
    }

    @Test
    void deleteSupplier_WhenExists_ShouldDelete() {
        when(supplierRepository.existsById(1L)).thenReturn(true);

        supplierService.deleteSupplier(1L);

        verify(supplierRepository).deleteById(1L);
    }

    @Test
    void deleteSupplier_WhenNotExists_ShouldThrowException() {
        when(supplierRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> supplierService.deleteSupplier(1L));
    }
}
