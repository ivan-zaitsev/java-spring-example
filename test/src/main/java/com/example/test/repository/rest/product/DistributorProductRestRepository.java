package com.example.test.repository.rest.product;

import com.example.test.model.dto.product.DistributorProductDto;

public interface DistributorProductRestRepository {

    DistributorProductDto findByCode(String code);

}
