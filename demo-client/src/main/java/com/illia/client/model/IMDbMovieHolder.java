package com.illia.client.model;


import java.util.List;

public interface IMDbMovieHolder {

    String getFileName();
    List<IMDbMovieEntity> getEntities();
    List<IMDbMovieEntity> saveEntities(String fileName, List<IMDbMovieEntity> report);
    boolean isEmpty();
    boolean holdsFile(String fileName);
}