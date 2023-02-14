package com.illia.client.model.holder;


import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.service.query.QueryProcessingException;
import java.util.List;

public interface IMDbMovieHolder {

  String getFileName();

  List<IMDbMovieEntity> getEntities(String fileName) throws QueryProcessingException;

  List<IMDbMovieEntity> saveEntities(String fileName, List<IMDbMovieEntity> report);

  boolean holdsFile(String fileName);

  void applyChanges(List<IMDbMovieEntity> entities);
}
