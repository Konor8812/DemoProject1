package com.illia.client.service.processor.unit;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.service.processor.InvalidAttributeException;

import java.util.List;
import java.util.Map;

public interface OperationProcessor {

    public List<IMDbMovieEntity> proceed(List<IMDbMovieEntity> records, Map<String, String> params) throws InvalidAttributeException;

}
