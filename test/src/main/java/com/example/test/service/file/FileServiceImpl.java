package com.example.test.service.file;

import com.example.test.config.file.StorageProperties;
import com.example.test.exception.EntityValidationException;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileServiceImpl implements FileService {

    private final StorageProperties storageProperties;

    public FileServiceImpl(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public void uploadFile(InputStream inputStream, String fileName) {
        Path filePath = Paths.get(storageProperties.getFolderPath(), fileName);
        validateFilePath(filePath);

        try (OutputStream outputStream = new FileOutputStream(filePath.toAbsolutePath().toString())) {
            inputStream.transferTo(outputStream);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to upload the file", e);
        }
    }

    @Override
    public void downloadFile(OutputStream outputStream, String fileName) {
        Path filePath = Paths.get(storageProperties.getFolderPath(), fileName);
        validateFilePath(filePath);

        try (InputStream inputStream = new FileInputStream(filePath.toAbsolutePath().toString())) {
            inputStream.transferTo(outputStream);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to download the file", e);
        }
    }

    private void validateFilePath(Path filePath) {
        if (!filePath.normalize().startsWith(storageProperties.getFolderPath())) {
            throw new EntityValidationException("File path in not correct");
        }
    }

}
