package com.illia;

import com.illia.server.file_holder.FileHolder;
import com.illia.server.file_holder.FileHolderImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {FileHolderImpl.class})
public class FileHolderTest {

    @Autowired
    FileHolder fileHolder;

    @Test
    public void testFileHolder() throws URISyntaxException {
        String fileName = "provided.csv";

        URI uri = ClassLoader.getSystemResource(fileName).toURI();
        var providedFile = new File(uri);

        fileHolder.saveFile(fileName, providedFile);
        var savedFile = fileHolder.getFile(fileName);

        assertEquals(providedFile, savedFile);
    }

}
