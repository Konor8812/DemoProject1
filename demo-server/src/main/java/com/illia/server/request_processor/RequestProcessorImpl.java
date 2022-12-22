package com.illia.server.request_processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illia.server.file_holder.FileHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;


@Service
public class RequestProcessorImpl implements RequestProcessor {

    @Autowired
    FileHolder fileHolder;

    @Override
    public ResponseEntity<Object> proceedDownloadFile(String fileName) {
        try {
            var fileHolderResponse = fileHolder.getFile(fileName);
            if (fileHolderResponse != null) {
                return ResponseEntity.ok().body(fileHolderResponse);
            } else {
                return ResponseEntity.badRequest().body("No such file!".getBytes());
            }
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // I don't like this realization
    @Override
    public ResponseEntity<String> proceedSaveFile(String fileName, File file) {
        var fileHolderResponse = fileHolder.saveFile(fileName, file);

        if (fileHolderResponse.equals("Updated existing file on server " + fileName)
                || fileHolderResponse.equals("Saved file on server " + fileName)) {
            return ResponseEntity.ok().body(fileHolderResponse);
        }

        if (fileHolderResponse.equals("File is null, nothing to save!")) {
            return ResponseEntity.badRequest().body(fileHolderResponse);
        } else {
            return ResponseEntity.internalServerError().body(fileHolderResponse);
        }
    }

    @Override
    public ResponseEntity<Integer> getFilesAmount() {
        var fileHolderResponse = fileHolder.getFilesAmount();
        if (fileHolderResponse >= 0) {
            return ResponseEntity.ok().body(fileHolderResponse);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }


}
