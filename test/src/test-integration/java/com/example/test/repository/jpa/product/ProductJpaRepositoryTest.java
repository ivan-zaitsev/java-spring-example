package com.example.test.repository.jpa.product;

import com.example.test.model.entity.ProductEntity;
import com.example.test.repository.jpa.RepositoryTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ProductJpaRepositoryTest extends RepositoryTestBase {

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Test
    void existsByCode_shouldReturnFalse_whenProductNotExists() {
        String code = "code";

        boolean exists = productJpaRepository.existsByCode(code);

        assertFalse(exists);
    }

    @Test
    void existsByCode_shouldReturnTrue_whenProductExists() {
        String code = "code";

        ProductEntity productEntity = new ProductEntity();
        productEntity.setCode(code);
        productJpaRepository.save(productEntity);

        boolean exists = productJpaRepository.existsByCode(code);

        assertTrue(exists);
    }

    @Test
    void findAllByCreatedAtBetween_shouldReturnEmptySet_whenProductsNotExist() {
        Instant to = Instant.now();
        Instant from = to.minus(Duration.ofDays(1));

        Set<ProductEntity> productEntities = productJpaRepository.findAllByCreatedAtBetween(from, to);

        assertTrue(productEntities.isEmpty());
    }

    @Test
    void findAllByCreatedAtBetween_shouldReturnProducts_whenProductsExist() {
        Instant to = Instant.now();
        Instant from = to.minus(Duration.ofDays(1));

        ProductEntity productEntity_1 = new ProductEntity();
        productEntity_1.setCode("code_1");
        productEntity_1.setCreatedAt(from.minus(Duration.ofMinutes(1)));
        productJpaRepository.save(productEntity_1);

        ProductEntity productEntity_2 = new ProductEntity();
        productEntity_2.setCode("code_2");
        productEntity_2.setCreatedAt(from.plus(Duration.ofMinutes(1)));
        productJpaRepository.save(productEntity_2);

        Set<ProductEntity> productEntities = productJpaRepository.findAllByCreatedAtBetween(from, to);

        assertThat(productEntities).doesNotContain(productEntity_1).contains(productEntity_2);
    }

}
