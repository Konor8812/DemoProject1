package com.illia.server.request_processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illia.server.file_holder.FileHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;


@Service
public class RequestProcessorImpl implements RequestProcessor {

    @Autowired
    FileHolder fileHolder;

    @Autowired
    ObjectMapper mapper;

    @Override
    public ResponseEntity<Object> proceedDownloadFile(String fileName) {
        try {
            var fileHolderResponse = fileHolder.getFile(fileName);
            if (fileHolderResponse != null) {
                return ResponseEntity.ok().body(fileHolderResponse);
            } else {
                return ResponseEntity.badRequest().body(mapper.writeValueAsString("No such file!"));
            }
        }catch (Exception ex){
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<String> proceedSaveFile(String fileName, MultipartFile file) {
        var fileHolderResponse = fileHolder.saveFile(fileName, file);
        return ResponseEntity.ok().body(fileHolderResponse);
    }

    @Override
    public ResponseEntity<String> proceedSaveFile(String fileName, File file) {
        var fileHolderResponse = fileHolder.saveFile(fileName, file);
        return ResponseEntity.ok().body(fileHolderResponse);
    }

    @Override
    public ResponseEntity<Integer> getFilesAmount(){
        var fileHolderResponse = fileHolder.getFilesAmount();
        if(fileHolderResponse >= 0){
            return ResponseEntity.ok().body(fileHolderResponse);
        } else{
            return ResponseEntity.internalServerError().build();
        }
    }




}
