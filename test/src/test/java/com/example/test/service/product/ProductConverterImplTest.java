package com.example.test.service.product;

import com.example.test.model.dto.product.ProductDto;
import com.example.test.model.entity.ProductEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ProductConverterImplTest {

    @InjectMocks
    private ProductConverterImpl productConverterImpl;

    @Test
    void convertToProductDto() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setCode("code");
        productEntity.setName("name");
        productEntity.setPrice(BigDecimal.valueOf(100));

        ProductDto actualProductDto = productConverterImpl.convertToProductDto(productEntity);

        ProductDto expectedProductDto = new ProductDto();
        expectedProductDto.setCode(productEntity.getCode());
        expectedProductDto.setName(productEntity.getName());
        expectedProductDto.setPrice(productEntity.getPrice());

        assertEquals(expectedProductDto, actualProductDto);
    }

}
