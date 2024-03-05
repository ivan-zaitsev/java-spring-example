package com.example.test.controller.rest.endpoint.product;

import com.example.test.controller.rest.endpoint.ControllerMvcTestBase;
import com.example.test.model.dto.product.ProductDto;
import com.example.test.model.dto.rest.ErrorCode;
import com.example.test.service.product.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerMvcTest extends ControllerMvcTestBase {

    @MockBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createProduct_shouldReturnUnauthorized_whenAuthenticationIsNotValid() throws Exception {
        String url = "/api/v1/products";

        mockMvc.perform(post(url))
               .andExpect(status().isUnauthorized());

        verify(productService, times(0)).create(any(), any());
    }

    @Test
    void createProduct_shouldReturnBadRequest_whenBodyParametersAreNotValid() throws Exception {
        String url = "/api/v1/products";

        String body = """
              {
                "code" : "",
                "name" : "",
                "price" : -1
              }
              """;

        mockMvc.perform(post(url)
                    .with(jwt())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                )
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errorCode", is(ErrorCode.REQUEST_BODY_NOT_VALID.name())))
               .andExpect(jsonPath("$.errors[0].target", is("name")))
               .andExpect(jsonPath("$.errors[0].message", is("must not be empty")))
               .andExpect(jsonPath("$.errors[1].target", is("code")))
               .andExpect(jsonPath("$.errors[1].message", is("must not be empty")))
               .andExpect(jsonPath("$.errors[2].target", is("price")))
               .andExpect(jsonPath("$.errors[2].message", is("must be greater than or equal to 0")));

        verify(productService, times(0)).create(any(), any());
    }

    @Test
    void createProduct() throws Exception {
        String url = "/api/v1/products";

        String body = """
              {
                "code" : "0x0001",
                "name" : "Product",
                "price" : 10
              }
              """;

        mockMvc.perform(post(url)
                    .with(jwt())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                )
               .andExpect(status().isOk());

        verify(productService, times(1)).create(any(), any());
    }

    @Test
    void fetchProducts_shouldReturnUnauthorized_whenAuthenticationIsNotValid() throws Exception {
        String url = "/api/v1/products";

        mockMvc.perform(get(url))
               .andExpect(status().isUnauthorized());

        verify(productService, times(0)).create(any(), any());
    }

    @Test
    void fetchProducts_shouldReturnBadRequest_whenRequiredUrlParameterIsNotValid() throws Exception {
        String url = "/api/v1/products";

        mockMvc.perform(get(url)
                    .with(jwt())
                )
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errorCode", is(ErrorCode.REQUEST_PARAMETER_NOT_VALID.name())));

        verify(productService, times(0)).findAllByCreatedAtBetween(any(), any());
    }

    @Test
    void fetchProducts() throws Exception {
        String url = "/api/v1/products";
        String from = "2023-06-01T00:00:00.000Z";
        String to = "2023-06-30T23:59:59.999Z";

        Set<ProductDto> products = Set.of(
                ProductDto.builder().code("0x0001").name("Product").price(BigDecimal.valueOf(10)).build());
        doReturn(products).when(productService).findAllByCreatedAtBetween(Instant.parse(from), Instant.parse(to));

        mockMvc.perform(get(url)
                    .with(jwt())
                    .param("from", from)
                    .param("to", to)
                )
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(1)))
               .andExpect(jsonPath("$[0].code", is("0x0001")))
               .andExpect(jsonPath("$[0].name", is("Product")))
               .andExpect(jsonPath("$[0].price", is(10)));
    }

}
