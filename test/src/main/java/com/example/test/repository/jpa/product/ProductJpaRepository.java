package com.example.test.repository.jpa.product;

import com.example.test.model.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {

    @Query("SELECT COUNT(p) > 0 FROM ProductEntity p WHERE p.code = :code")
    boolean existsByCode(String code);

    @Query("SELECT p FROM ProductEntity p WHERE p.createdAt BETWEEN :from AND :to")
    Set<ProductEntity> findAllByCreatedAtBetween(Instant from, Instant to);

}
