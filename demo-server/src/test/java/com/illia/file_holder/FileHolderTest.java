package com.illia.file_holder;

import com.illia.server.file_holder.FileHolder;
import com.illia.server.file_holder.FileHolderImpl;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

    @Disabled
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

    // ?
    @Test
    public void testSaveFile() throws URISyntaxException, IOException {
        String fileName = "provided.csv";

        var uri = ClassLoader.getSystemResource(fileName).toURI();
        var copy = Files.copy(Path.of(uri), Path.of(fileName));

        File providedFile = copy.toFile();

        var response = fileHolder.saveFile(fileName, providedFile);
        assertTrue(response.contains("Saved file"));

    }

    @Test
    public void testSaveMultipartFile() {
        String fileName = "provided.csv";
        String multipartFileName = "multipartFile";

        var mockedMultipartFile = new MockMultipartFile(multipartFileName, fileName, "text/csv", "Content".getBytes());

        fileHolder.saveFile(multipartFileName, mockedMultipartFile);

        var responseShouldBeNewFile = fileHolder.saveFile(fileName, mockedMultipartFile);
        var responseShouldBeUpdatedExisting = fileHolder.saveFile(fileName, mockedMultipartFile);

        assertTrue(responseShouldBeNewFile.contains("Saved file"));
        assertTrue(responseShouldBeUpdatedExisting.contains("Updated existing file"));
    }

    @Test
    public void testGetFile() throws URISyntaxException, IOException {
        String fileName = "provided.csv";
        String multipartFileName = "multipartFile";
        var expectedLinesAmount = createFiles(fileName, multipartFileName);

        var file1 = fileHolder.getFile(fileName);
        var file2 = fileHolder.getFile(multipartFileName);

        var lines1 = Files.readAllLines(file1.toPath());
        var lines2 = Files.readAllLines(file2.toPath());

        assertTrue(expectedLinesAmount == lines1.size() && expectedLinesAmount == lines2.size());
        assertTrue(lines1.containsAll(lines2));

    }

    private int createFiles(String fileName, String multipartFileName) throws URISyntaxException, IOException {
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

        return expectedLinesAmount;
    }



    @BeforeEach
    public void clearAndCreateDirectory() throws IOException {
        var directoryPath = Path.of(prefix);
        fileHolder.setPrefix(prefix);
        FileUtils.deleteDirectory(directoryPath.toFile());
        Files.createDirectory(directoryPath);
    }

    @AfterEach
    public void clearDirectory() throws IOException {
        var directoryPath = Path.of(prefix);
        FileUtils.deleteDirectory(directoryPath.toFile());
    }
}
