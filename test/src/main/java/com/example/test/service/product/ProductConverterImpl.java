package com.example.test.service.product;

import com.example.test.model.dto.product.ProductDto;
import com.example.test.model.entity.ProductEntity;
import org.springframework.stereotype.Service;

@Service
public class ProductConverterImpl implements ProductConverter {

    @Override
    public ProductDto convertToProductDto(ProductEntity productEntity) {
        ProductDto productDto = new ProductDto();
        productDto.setCode(productEntity.getCode());
        productDto.setName(productEntity.getName());
        productDto.setPrice(productEntity.getPrice());
        return productDto;
    }

}
