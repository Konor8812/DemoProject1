package com.illia.client.model;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IMDbMovieHolderImpl implements IMDbMovieHolder {

    private String fileName;
    private List<IMDbMovieEntity> entities;

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public List<IMDbMovieEntity> getEntities() {
        return entities;
    }

    @Override
    public List<IMDbMovieEntity> saveEntities(String fileName, List<IMDbMovieEntity> report) {
        this.fileName = fileName;
        this.entities = report;
        return this.entities;
    }

    @Override
    public boolean isEmpty() {
        return entities.isEmpty();
    }

    @Override
    public boolean holdsFile(String fileName) {

        return getFileName() != null && getFileName().equals(fileName);
    }

}
