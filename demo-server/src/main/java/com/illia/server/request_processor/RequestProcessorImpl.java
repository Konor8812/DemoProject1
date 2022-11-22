package com.illia.server.request_processor;

import com.illia.server.file_holder.FileHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class RequestProcessorImpl implements RequestProcessor {

    @Autowired
    FileHolder fileHolder;

    @Override
    public File proceedDownloadFile(String fileName) {
        return fileHolder.getFile(fileName);
    }

    @Override
    public File proceedSaveFile(String fileName, File file) {
        return fileHolder.saveFile(fileName, file);
    }

    @Override
    public int getFilesAmount(){
        return fileHolder.getFilesAmount();
    }

}
