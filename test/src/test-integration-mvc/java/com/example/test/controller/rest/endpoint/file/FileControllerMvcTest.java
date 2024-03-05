package com.example.test.controller.rest.endpoint.file;

import com.example.test.controller.rest.endpoint.ControllerMvcTestBase;
import com.example.test.model.dto.rest.ErrorCode;
import com.example.test.service.file.FileService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.io.OutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileController.class)
class FileControllerMvcTest extends ControllerMvcTestBase {

    @MockBean
    private FileService fileService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void uploadFile_shouldReturnUnauthorized_whenAuthenticationIsNotValid() throws Exception {
        String url = "/api/v1/files/upload";

        mockMvc.perform(post(url))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void uploadFile_shouldReturnBadRequest_whenRequiredUrlParameterIsNotValid() throws Exception {
        String url = "/api/v1/files/upload";

        mockMvc.perform(post(url)
                    .with(jwt())
                )
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errorCode", is(ErrorCode.REQUEST_PARAMETER_NOT_VALID.name())));
    }

    @Test
    void uploadFile() throws Exception {
        String url = "/api/v1/files/upload";
        String fileName = "file";
        byte[] fileContent = { 1, 2, 3 };

        mockMvc.perform(post(url)
                    .with(jwt())
                    .param("fileName", fileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .content(fileContent)
                )
               .andExpect(status().isOk());

        ArgumentCaptor<InputStream> inputStreamCaptor = ArgumentCaptor.forClass(InputStream.class);
        verify(fileService, times(1)).uploadFile(inputStreamCaptor.capture(), eq("file"));

        assertArrayEquals(fileContent, inputStreamCaptor.getValue().readAllBytes());
    }

    @Test
    void downloadFile_shouldReturnUnauthorized_whenAuthenticationIsNotValid() throws Exception {
        String url = "/api/v1/files/download";

        mockMvc.perform(get(url))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void downloadFile_shouldReturnBadRequest_whenRequiredUrlParameterIsNotValid() throws Exception {
        String url = "/api/v1/files/download";

        mockMvc.perform(get(url)
                    .with(jwt())
                )
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errorCode", is(ErrorCode.REQUEST_PARAMETER_NOT_VALID.name())));
    }

    @Test
    void downloadFile() throws Exception {
        String url = "/api/v1/files/download";
        String fileName = "file";
        byte[] fileContent = { 1, 2, 3 };

        ArgumentCaptor<OutputStream> outputStreamCaptor = ArgumentCaptor.forClass(OutputStream.class);
        doAnswer(invocation -> {
            OutputStream outputStream = invocation.getArgument(0);
            outputStream.write(fileContent);
            return null;
        }).when(fileService).downloadFile(outputStreamCaptor.capture(), eq(fileName));

        mockMvc.perform(get(url)
                    .with(jwt())
                    .param("fileName", "file")
                )
               .andExpect(status().isOk())
               .andExpect(content().bytes(fileContent));
    }

}
