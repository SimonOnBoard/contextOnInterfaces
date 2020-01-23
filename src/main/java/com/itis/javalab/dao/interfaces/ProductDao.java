package com.itis.javalab.dao.interfaces;

import com.itis.javalab.dto.entity.ProductDTO;
import com.itis.javalab.models.Product;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductDao extends CrudDao<Product>{
    Optional<Product> findByName(String name);
    LocalDateTime savePaymentAct(Long userId, Long productId, Integer count);
    List<Product> findProductsOnPage(Long limit, Long offset);
    List<ProductDTO> findAllPaymentsById(Long id);
}
