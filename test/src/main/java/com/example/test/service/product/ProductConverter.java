package com.example.test.service.product;

import com.example.test.model.dto.product.ProductDto;
import com.example.test.model.entity.ProductEntity;

public interface ProductConverter {

    ProductDto convertToProductDto(ProductEntity productEntity);

}
