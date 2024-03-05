package com.example.test.service.product;

import com.example.test.exception.EntityValidationException;
import com.example.test.model.dto.product.DistributorProductDto;
import com.example.test.model.dto.product.ProductDto;
import com.example.test.model.entity.ProductEntity;
import com.example.test.repository.jpa.product.ProductJpaRepository;
import com.example.test.repository.rest.product.DistributorProductRestRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductJpaRepository productJpaRepository;
    private final DistributorProductRestRepository distributorProductRestRepository;
    private final ProductConverter productConverter;

    public ProductServiceImpl(
            ProductJpaRepository productJpaRepository,
            DistributorProductRestRepository distributorProductRestRepository,
            ProductConverter productConverter) {

        this.productJpaRepository = productJpaRepository;
        this.distributorProductRestRepository = distributorProductRestRepository;
        this.productConverter = productConverter;
    }

    @Override
    public void create(ProductDto productDto, Jwt performer) {
        if (productJpaRepository.existsByCode(productDto.getCode())) {
            throw new EntityValidationException("Product with this code already exists");
        }

        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(null);
        productEntity.setCode(productDto.getCode());
        productEntity.setName(productDto.getName());
        productEntity.setPrice(productDto.getPrice());
        productEntity.setCreatedBy(performer.getSubject());

        DistributorProductDto distributorProductDto = distributorProductRestRepository.findByCode(productDto.getCode());
        if (distributorProductDto != null) {
            productEntity.setDescription(distributorProductDto.getDescription());
        }

        productEntity.setCreatedAt(Instant.now());
        productJpaRepository.save(productEntity);
    }

    @Override
    public Set<ProductDto> findAllByCreatedAtBetween(Instant from, Instant to) {
        Set<ProductEntity> products = productJpaRepository.findAllByCreatedAtBetween(from, to);
        return products.stream().map(productConverter::convertToProductDto).collect(Collectors.toSet());
    }

}
