package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.dto.Request.ProductRequestDTO;
import com.tricol.springboottricolapi.dto.Response.ProductResponseDTO;
import com.tricol.springboottricolapi.dto.Response.ProductStockDTO;

import java.util.List;

public interface IProductService {
    List<ProductResponseDTO> getAllProducts();
    ProductResponseDTO getProductById(Long id);
    ProductResponseDTO createProduct(ProductRequestDTO requestDTO);
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO);
    void deleteProduct(Long id);
    ProductStockDTO getProductStock(Long id);
}
