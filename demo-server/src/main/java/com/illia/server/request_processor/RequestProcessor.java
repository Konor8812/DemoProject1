package com.illia.server.request_processor;


import java.io.File;

public interface RequestProcessor {

    File proceedDownloadFile(String fileName);
    File proceedSaveFile(String fileName, File file);
    int getFilesAmount();
}
