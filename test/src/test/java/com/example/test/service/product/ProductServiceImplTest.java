package com.example.test.service.product;

import com.example.test.exception.EntityValidationException;
import com.example.test.model.dto.product.DistributorProductDto;
import com.example.test.model.dto.product.ProductDto;
import com.example.test.model.entity.ProductEntity;
import com.example.test.repository.jpa.product.ProductJpaRepository;
import com.example.test.repository.rest.product.DistributorProductRestRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    private MockedStatic<Instant> instant;

    @Mock
    private ProductJpaRepository productJpaRepository;

    @Mock
    private DistributorProductRestRepository distributorProductRestRepository;

    @Spy
    private ProductConverter productConverter = new ProductConverterImpl();

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @AfterEach
    void teardown() {
        if (instant != null) {
            instant.close();
            instant = null;
        }
    }

    @Test
    void create_shouldThrowException_whenProductWithCodeAlreadyExists() {
        ProductDto productDto = new ProductDto();
        productDto.setCode("code");

        doReturn(true).when(productJpaRepository).existsByCode(productDto.getCode());

        Jwt performer = mock(Jwt.class);

        Executable executable = () -> productServiceImpl.create(productDto, performer);

        assertThrows(EntityValidationException.class, executable);
    }

    @Test
    void create_shouldSaveProductWithoutDescription_whenDistributorProductIsNull() {
        Instant timestamp = Instant.now();
        instant = Mockito.mockStatic(Instant.class);
        instant.when(Instant::now).thenReturn(timestamp);

        ProductDto productDto = new ProductDto();
        productDto.setCode("code");
        productDto.setName("name");
        productDto.setPrice(BigDecimal.valueOf(100));

        doReturn(false).when(productJpaRepository).existsByCode(productDto.getCode());

        doReturn(null).when(distributorProductRestRepository).findByCode(productDto.getCode());

        Jwt performer = mock(Jwt.class);
        doReturn("username").when(performer).getSubject();

        productServiceImpl.create(productDto, performer);

        ProductEntity expectedProductEntity = new ProductEntity();
        expectedProductEntity.setCode(productDto.getCode());
        expectedProductEntity.setName(productDto.getName());
        expectedProductEntity.setPrice(productDto.getPrice());
        expectedProductEntity.setCreatedAt(timestamp);
        expectedProductEntity.setCreatedBy("username");

        verify(productJpaRepository, times(1)).save(expectedProductEntity);
    }

    @Test
    void create_shouldSaveProductWithDescription_whenDistributorProductIsNotNull() {
        Instant timestamp = Instant.now();
        instant = Mockito.mockStatic(Instant.class);
        instant.when(Instant::now).thenReturn(timestamp);

        ProductDto productDto = new ProductDto();
        productDto.setCode("code");
        productDto.setName("name");
        productDto.setPrice(BigDecimal.valueOf(100));

        doReturn(false).when(productJpaRepository).existsByCode(productDto.getCode());

        DistributorProductDto distributorProductDto = new DistributorProductDto();
        distributorProductDto.setDescription("description");
        doReturn(distributorProductDto).when(distributorProductRestRepository).findByCode(productDto.getCode());

        Jwt performer = mock(Jwt.class);
        doReturn("username").when(performer).getSubject();

        productServiceImpl.create(productDto, performer);

        ProductEntity expectedProductEntity = new ProductEntity();
        expectedProductEntity.setCode(productDto.getCode());
        expectedProductEntity.setName(productDto.getName());
        expectedProductEntity.setPrice(productDto.getPrice());
        expectedProductEntity.setDescription(distributorProductDto.getDescription());
        expectedProductEntity.setCreatedAt(timestamp);
        expectedProductEntity.setCreatedBy("username");

        verify(productJpaRepository, times(1)).save(expectedProductEntity);
    }

    @Test
    void findAllByCreatedAtBetween_shouldReturnEmptySet_whenRepositoryReturnsEmptySet() {
        Instant to = Instant.now();
        Instant from = to.minus(Duration.ofDays(1));

        doReturn(Set.of()).when(productJpaRepository).findAllByCreatedAtBetween(from, to);

        Set<ProductDto> productDtos = productServiceImpl.findAllByCreatedAtBetween(from, to);

        assertEquals(Set.of(), productDtos);
    }

    @Test
    void findAllByCreatedAtBetween_shouldReturnProducts() {
        Instant to = Instant.now();
        Instant from = to.minus(Duration.ofDays(1));

        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(UUID.randomUUID());
        productEntity.setCode("code");
        productEntity.setName("name");

        doReturn(Set.of(productEntity)).when(productJpaRepository).findAllByCreatedAtBetween(from, to);

        Set<ProductDto> actualProductDtos = productServiceImpl.findAllByCreatedAtBetween(from, to);

        ProductDto expectedProductDto = productConverter.convertToProductDto(productEntity);

        assertEquals(Set.of(expectedProductDto), actualProductDtos);
    }

}
