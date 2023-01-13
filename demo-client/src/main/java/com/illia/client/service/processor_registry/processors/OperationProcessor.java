package com.illia.client.service.processor_registry.processors;

import com.illia.client.model.IMDbMovieEntity;

import java.util.List;
import java.util.Map;

public interface OperationProcessor {

    public List<IMDbMovieEntity> proceed(List<IMDbMovieEntity> records, Map<String, String> params) throws NoSuchFieldException;

}
