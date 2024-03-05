package com.example.test.controller.rest.endpoint.file;

import com.example.test.service.file.FileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/v1/files/upload")
    public void uploadFile(
            HttpServletRequest request,
            @RequestParam String fileName) throws IOException {

        fileService.uploadFile(request.getInputStream(), fileName);
    }

    @GetMapping("/v1/files/download")
    public void downloadFile(
            HttpServletResponse response,
            @RequestParam String fileName) throws IOException {

        fileService.downloadFile(response.getOutputStream(), fileName);
    }

}
