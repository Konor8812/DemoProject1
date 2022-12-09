package com.illia.file_holder;

import com.illia.server.file_holder.FileHolder;
import com.illia.server.file_holder.FileHolderImpl;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {FileHolderImpl.class})
public class FileHolderTest {

    @Autowired
    FileHolder fileHolder;

    private final String prefix = "test_file_holder/";

    @Test
    public void testFileHolder() throws URISyntaxException, IOException {
        String fileName = "provided.csv";
        String multipartFileName = "multipartFile";

        fileHolder.setPrefix(prefix);
        var uri = ClassLoader.getSystemResource(fileName).toURI();

        var copy = Files.copy(Path.of(uri), Path.of(fileName));
        var expectedLinesAmount = Files.readAllLines(copy).size();
        File providedFile = copy.toFile();
        MockMultipartFile mockedMultipartFile;

        try (var is = new FileInputStream(providedFile)){
            var providedFileBytes = is.readAllBytes();
            mockedMultipartFile = new MockMultipartFile(multipartFileName, fileName, "text/csv", providedFileBytes);
        }

        fileHolder.saveFile(fileName, providedFile);
        fileHolder.saveFile(multipartFileName, mockedMultipartFile);

        var file1 = fileHolder.getFile(fileName);
        var file2 = fileHolder.getFile(multipartFileName);


        var lines1 = Files.readAllLines(file1.toPath());
        var lines2 = Files.readAllLines(file2.toPath());

        assertTrue(expectedLinesAmount == lines1.size() && expectedLinesAmount == lines2.size());
        assertTrue(lines1.containsAll(lines2));

    }

    @BeforeEach
    public void clearAndCreateDirectory() throws IOException {
        var directoryPath = Path.of(prefix);
        FileUtils.deleteDirectory(directoryPath.toFile());
        Files.createDirectory(directoryPath);
    }

    @AfterEach
    public void clearDirectory() throws IOException {
        var directoryPath = Path.of(prefix);
        FileUtils.deleteDirectory(directoryPath.toFile());
    }
}
