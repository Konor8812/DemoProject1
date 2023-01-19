package com.illia.client.service.processor.unit;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.request.QueryRequestEntity;

import java.util.List;

public interface OperationProcessor {

    List<IMDbMovieEntity> proceed(List<IMDbMovieEntity> records, QueryRequestEntity requestEntity);

}
