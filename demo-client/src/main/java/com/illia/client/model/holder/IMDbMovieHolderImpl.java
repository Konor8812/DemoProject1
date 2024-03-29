package com.illia.client.model.holder;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.service.query.QueryProcessingException;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class IMDbMovieHolderImpl implements IMDbMovieHolder {

  private String fileName;
  private List<IMDbMovieEntity> entities;

  @Override
  public String getFileName() {
    return fileName;
  }

  @Override
  public List<IMDbMovieEntity> getEntities(String fileName) throws QueryProcessingException {
    if (holdsFile(fileName) && !entities.isEmpty()) {
      return entities;
    }
    throw new QueryProcessingException("Local cache is empty!");
  }

  @Override
  public List<IMDbMovieEntity> saveEntities(String fileName, List<IMDbMovieEntity> report) {
    this.fileName = fileName;
    this.entities = report;
    return this.entities;
  }


  @Override
  public boolean holdsFile(String fileName) {
    if (fileName == null) {
      return false;
    }
    return fileName.equals(getFileName());
  }

  @Override
  public void applyChanges(List<IMDbMovieEntity> entities) {
    this.entities = entities;
  }

}
