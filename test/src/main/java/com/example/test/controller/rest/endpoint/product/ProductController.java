package com.example.test.controller.rest.endpoint.product;

import com.example.test.model.dto.product.ProductDto;
import com.example.test.service.product.ProductService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/v1/products")
    public void createProduct(
            @RequestBody @Valid ProductDto productDto,
            @AuthenticationPrincipal Jwt performer) {

        productService.create(productDto, performer);
    }

    @GetMapping("/v1/products")
    public Set<ProductDto> fetchProducts(
            @RequestParam Instant from,
            @RequestParam Instant to) {

        return productService.findAllByCreatedAtBetween(from, to);
    }

}
