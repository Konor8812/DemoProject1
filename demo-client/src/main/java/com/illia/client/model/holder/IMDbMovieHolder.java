package com.illia.client.model.holder;


import com.illia.client.model.IMDbMovieEntity;

import java.util.List;

public interface IMDbMovieHolder {

    String getFileName();
    List<IMDbMovieEntity> getEntities(String fileName);
    List<IMDbMovieEntity> saveEntities(String fileName, List<IMDbMovieEntity> report);
    boolean isEmpty();
    boolean holdsFile(String fileName);

    void applyChanges(List<IMDbMovieEntity> entities);
}
