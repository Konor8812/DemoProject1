package com.illia.client.service.processor.unit;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.request.creator.RequestParams;
import com.illia.client.model.request.entity.QueryEntity;

import java.util.List;

public interface OperationProcessor {

    List<IMDbMovieEntity> proceed(List<IMDbMovieEntity> records, QueryEntity queryEntity);

}
