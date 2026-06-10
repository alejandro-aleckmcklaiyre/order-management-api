package com.example.product_api.service;

import com.example.product_api.dto.ProductRequestDTO;
import com.example.product_api.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {

    ProductResponseDTO createProduct(ProductRequestDTO request);

    List<ProductResponseDTO> getAllProducts();

    ProductResponseDTO getProductById(Long id);
}
