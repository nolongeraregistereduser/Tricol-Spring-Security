package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.dto.Request.ProductRequestDTO;
import com.tricol.springboottricolapi.dto.Response.ProductResponseDTO;
import com.tricol.springboottricolapi.dto.Response.ProductStockDTO;
import com.tricol.springboottricolapi.entity.Product;
import com.tricol.springboottricolapi.exception.DuplicateRessourceException;
import com.tricol.springboottricolapi.exception.ResourceNotFoundException;
import com.tricol.springboottricolapi.mapper.ProductMapper;
import com.tricol.springboottricolapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequestDTO requestDTO;
    private ProductResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .reference("PROD001")
                .name("Test Product")
                .description("Test Description")
                .unitPrice(BigDecimal.valueOf(100))
                .category("Electronics")
                .currentStock(BigDecimal.valueOf(50))
                .reorderPoint(BigDecimal.valueOf(10))
                .unitOfMeasure("PIECE")
                .build();

        requestDTO = new ProductRequestDTO();
        requestDTO.setReference("PROD001");
        requestDTO.setName("Test Product");
        requestDTO.setUnitPrice(BigDecimal.valueOf(100));
        requestDTO.setCategory("Electronics");

        responseDTO = new ProductResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setReference("PROD001");
        responseDTO.setName("Test Product");
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));
        when(productMapper.toResponseDTO(any(Product.class))).thenReturn(responseDTO);

        List<ProductResponseDTO> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository).findAll();
    }

    @Test
    void getProductById_WhenExists_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponseDTO(product)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals("PROD001", result.getReference());
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_WhenNotExists_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void createProduct_WhenValid_ShouldCreateProduct() {
        when(productRepository.existsByReference("PROD001")).thenReturn(false);
        when(productMapper.toEntity(requestDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponseDTO(product)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.createProduct(requestDTO);

        assertNotNull(result);
        verify(productRepository).save(product);
    }

    @Test
    void createProduct_WhenDuplicateReference_ShouldThrowException() {
        when(productRepository.existsByReference("PROD001")).thenReturn(true);

        assertThrows(DuplicateRessourceException.class, () -> productService.createProduct(requestDTO));
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_WhenValid_ShouldUpdateProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponseDTO(product)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.updateProduct(1L, requestDTO);

        assertNotNull(result);
        verify(productMapper).updateEntity(requestDTO, product);
        verify(productRepository).save(product);
    }

    @Test
    void deleteProduct_WhenExists_ShouldDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_WhenNotExists_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    void getProductStock_ShouldReturnStockInfo() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductStockDTO result = productService.getProductStock(1L);

        assertNotNull(result);
        assertEquals(1L, result.getProductId());
        assertEquals("PROD001", result.getReference());
        verify(productRepository).findById(1L);
    }
}
