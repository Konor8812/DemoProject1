package com.illia.client.service;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.IMDbMovieHolderImpl;
import com.illia.client.model.IMDbMovieParser;
import com.illia.client.model.request.creator.RequestParams;
import com.illia.client.model.request.entity.QueryEntity;
import com.illia.client.service.processor.ProcessorAssigner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryProcessingService {

    @Autowired
    private FileHandlingService fileHandlingService;
    @Autowired
    private ProcessorAssigner processorAssigner;
    @Autowired
    private IMDbMovieParser parser;
    @Autowired
    private IMDbMovieHolderImpl holder;

    public ResponseEntity<Object> performOperation(QueryEntity queryEntity) {
        var fileName = queryEntity.getFileName();

        List<IMDbMovieEntity> records = null;
        if (queryEntity.shouldParse()) {
            if((records = requestParseFile(fileName)) == null){
                return ResponseEntity.badRequest().body("No such file!");
            }
        }else {
            if ((records = holder.getEntities(fileName)) == null) {
                return ResponseEntity.badRequest().body("No data in local cache!");
            }
        }

        return ResponseEntity.ok()
                .body(
                        processorAssigner.assignProcessor(queryEntity)
                                .apply(records, queryEntity)
                );
    }

    private List<IMDbMovieEntity> requestParseFile(String fileName) {
        var path = fileHandlingService.resolveFilePath(fileName);
        if (path != null) {
            return parser.parseFile(path.toFile());
        }
        return null;
    }


}
