package com.example.test.service.product;

import com.example.test.model.dto.product.ProductDto;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Set;

public interface ProductService {

    void create(ProductDto productDto, Jwt performer);

    Set<ProductDto> findAllByCreatedAtBetween(Instant from, Instant to);

}
