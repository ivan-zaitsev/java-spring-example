package com.example.test.repository.rest.product;

import com.example.test.config.product.ProductDistributorProperties;
import com.example.test.model.dto.product.DistributorProductDto;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Repository
public class DistributorProductRestRepositoryImpl implements DistributorProductRestRepository {

    private final ProductDistributorProperties productDistributorProperties;

    private final RestTemplate restTemplate;

    public DistributorProductRestRepositoryImpl(
            ProductDistributorProperties productDistributorProperties,
            RestTemplate restTemplate) {

        this.productDistributorProperties = productDistributorProperties;
        this.restTemplate = restTemplate;
    }

    @Override
    @Retry(name = "api-exception")
    public DistributorProductDto findByCode(String code) {
        String url = UriComponentsBuilder.fromUriString(productDistributorProperties.getProductUrl())
                .queryParam("code", code)
                .build()
                .toUriString();

        ResponseEntity<DistributorProductDto> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<DistributorProductDto>() {});

        return response.getBody();
    }

}
