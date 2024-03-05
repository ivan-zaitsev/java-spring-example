package com.example.test.repository.rest.product;

import com.example.test.config.product.ProductDistributorProperties;
import com.example.test.model.dto.product.DistributorProductDto;
import com.example.test.repository.rest.RestRepositoryTestBase;
import com.example.test.service.json.JsonConverter;
import io.github.resilience4j.springboot3.retry.autoconfigure.RetryAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.MediaType.APPLICATION_JSON;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

@MockServerTest
@Import({ ProductDistributorProperties.class, DistributorProductRestRepositoryImpl.class })
@ImportAutoConfiguration({ AopAutoConfiguration.class, RetryAutoConfiguration.class })
class DistributorProductRestRepositoryImplTest extends RestRepositoryTestBase {

    private MockServerClient mockServerClient;

    @Autowired
    private JsonConverter jsonConverter;

    @SpyBean
    private ProductDistributorProperties productDistributorProperties;

    @SpyBean
    private RestTemplate restTemplate;

    @Autowired
    private DistributorProductRestRepositoryImpl distributorProductRestRepositoryImpl;

    @Test
    @SuppressWarnings("unchecked")
    void findByCode_shouldNotRetry_whenClientErrorThrown() {
        doThrow(HttpClientErrorException.class).when(restTemplate).exchange(
                any(String.class), eq(GET), eq(null), any(ParameterizedTypeReference.class));

        try {
            String code = "code";

            distributorProductRestRepositoryImpl.findByCode(code);
            fail();
        } catch (HttpClientErrorException e) { }

        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(GET), eq(null), any(ParameterizedTypeReference.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByCode_shouldRetry_whenServerErrorThrown() {
        doThrow(RestClientException.class).when(restTemplate).exchange(
                any(String.class), eq(GET), eq(null), any(ParameterizedTypeReference.class));

        try {
            String code = "code";

            distributorProductRestRepositoryImpl.findByCode(code);
            fail();
        } catch (RestClientException e) { }

        verify(restTemplate, times(3))
                .exchange(any(String.class), eq(GET), eq(null), any(ParameterizedTypeReference.class));
    }

    @Test
    void findByCode_shouldReturnDistributorProduct() {
        DistributorProductDto expectedDistributorProductDto = new DistributorProductDto();
        expectedDistributorProductDto.setCode("code");
        expectedDistributorProductDto.setName("name");
        expectedDistributorProductDto.setDescription("description");

        HttpRequest mockRequest = request(URI.create(productDistributorProperties.getProductUrl()).getPath());
        HttpResponse mockResponse = response(jsonConverter.convertToString(expectedDistributorProductDto))
                .withContentType(APPLICATION_JSON).withStatusCode(OK.value());
        mockServerClient.when(mockRequest).respond(mockResponse);

        String code = "code";
        DistributorProductDto actualDistributorProductDto = distributorProductRestRepositoryImpl.findByCode(code);

        assertEquals(expectedDistributorProductDto, actualDistributorProductDto);

        mockServerClient.verify(mockRequest.withQueryStringParameter(Parameter.param("code", code)));
    }

}
