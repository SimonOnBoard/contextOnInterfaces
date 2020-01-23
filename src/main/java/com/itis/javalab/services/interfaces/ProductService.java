package com.itis.javalab.services.interfaces;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.itis.javalab.context.annotations.Component;
import com.itis.javalab.dto.entity.ProductDTO;
import com.itis.javalab.dto.system.PaginationDto;
import com.itis.javalab.dto.system.ServiceDto;

@Component
public interface ProductService {
    public ServiceDto getProductListViaPagination(PaginationDto paginationDto);
    public ServiceDto removeProduct(ProductDTO product);
    public ServiceDto addNewProduct(ProductDTO product);
    public ServiceDto registerPayment(ProductDTO product, DecodedJWT jwt);
}
