package com.example.test.service.file;

import com.example.test.config.file.StorageProperties;
import com.example.test.exception.EntityValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @Mock
    private StorageProperties storageProperties;

    @InjectMocks
    private FileServiceImpl fileServiceImpl;

    @Test
    void uploadFile_shouldThrowException_whenFileNameIsNotValid() {
        String fileName = "../../file";
        byte[] fileContent = { 1, 2, 3 };

        doReturn(System.getProperty("user.dir")).when(storageProperties).getFolderPath();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent);
        Executable executable = () -> fileServiceImpl.uploadFile(inputStream, fileName);

        assertThrows(EntityValidationException.class, executable);
    }

    @Test
    void uploadFileAndDownloadFile() {
        String fileName = "file";
        byte[] fileContent = { 1, 2, 3 };

        doReturn(System.getProperty("user.dir") + "/build").when(storageProperties).getFolderPath();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent);
        fileServiceImpl.uploadFile(inputStream, fileName);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        fileServiceImpl.downloadFile(outputStream, fileName);

        assertArrayEquals(fileContent, outputStream.toByteArray());
    }

}
