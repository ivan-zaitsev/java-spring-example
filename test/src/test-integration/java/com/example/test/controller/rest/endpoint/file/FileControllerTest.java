package com.example.test.controller.rest.endpoint.file;

import com.example.test.controller.rest.endpoint.AuthenticationConfigurer;
import com.example.test.controller.rest.endpoint.ControllerTestBase;
import com.example.test.model.dto.rest.ErrorCode;
import com.example.test.model.dto.rest.ErrorResponseDto;
import com.example.test.service.file.FileService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileControllerTest extends ControllerTestBase {

    @Autowired
    private AuthenticationConfigurer authorizationTestConfigurer;

    @Autowired
    private FileService fileService;

    @AfterEach
    void setup() {
        authorizationTestConfigurer.setAuthentication(null);
    }

    @Test
    void uploadFile_shouldReturnBadRequest_whenAuthenticationIsNotValid() {
        String url = "/api/v1/files/upload";

        ResponseEntity<ErrorResponseDto> response =
                buildRestTemplate().exchange(url, HttpMethod.POST, null, ErrorResponseDto.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void uploadFile_shouldReturnBadRequest_whenRequiredUrlParameterIsNotValid() throws Exception {
        authorizationTestConfigurer.setAuthentication(buildMainAuthentication());

        String url = "/api/v1/files/upload";

        ResponseEntity<ErrorResponseDto> response =
                buildRestTemplate().exchange(url, HttpMethod.POST, null, ErrorResponseDto.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ErrorCode.REQUEST_PARAMETER_NOT_VALID, response.getBody().getErrorCode());
    }

    @Test
    void uploadFile() {
        authorizationTestConfigurer.setAuthentication(buildMainAuthentication());

        String fileName = "file";
        byte[] fileContent = { 1, 2, 3 };

        String url = UriComponentsBuilder.fromPath("/api/v1/files/upload")
                .queryParam("fileName", fileName)
                .build()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        HttpEntity<byte[]> entity = new HttpEntity<byte[]>(fileContent, headers);

        ResponseEntity<Void> response =
                buildRestTemplate().exchange(url, HttpMethod.POST, entity, Void.class);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        fileService.downloadFile(outputStream, fileName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(fileContent, outputStream.toByteArray());
    }

    @Test
    void downloadFile_shouldReturnBadRequest_whenAuthenticationIsNotValid() {
        String url = "/api/v1/files/download";

        ResponseEntity<ErrorResponseDto> response =
                buildRestTemplate().exchange(url, HttpMethod.GET, null, ErrorResponseDto.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void downloadFile_shouldReturnBadRequest_whenRequiredUrlParameterIsNotValid() throws Exception {
        authorizationTestConfigurer.setAuthentication(buildMainAuthentication());

        String url = "/api/v1/files/download";

        ResponseEntity<ErrorResponseDto> response =
                buildRestTemplate().exchange(url, HttpMethod.GET, null, ErrorResponseDto.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ErrorCode.REQUEST_PARAMETER_NOT_VALID, response.getBody().getErrorCode());
    }

    @Test
    void downloadFile() {
        authorizationTestConfigurer.setAuthentication(buildMainAuthentication());

        String fileName = "file";
        byte[] fileContent = { 1, 2, 3 };

        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent);
        fileService.uploadFile(inputStream, fileName);

        String url = UriComponentsBuilder.fromPath("/api/v1/files/download")
                .queryParam("fileName", fileName)
                .build()
                .toUriString();

        ResponseEntity<byte[]> response =
                buildRestTemplate().exchange(url, HttpMethod.GET, null, byte[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(response.getBody(), fileContent);
    }

}
