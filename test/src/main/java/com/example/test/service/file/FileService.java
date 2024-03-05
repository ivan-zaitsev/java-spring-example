package com.example.test.service.file;

import java.io.InputStream;
import java.io.OutputStream;

public interface FileService {

    void uploadFile(InputStream inputStream, String fileName);

    void downloadFile(OutputStream outputStream, String fileName);

}
