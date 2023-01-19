package com.illia.client.service;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.IMDbMovieHolderImpl;
import com.illia.client.model.IMDbMovieParser;
import com.illia.client.model.request.QueryRequestEntity;
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

    public ResponseEntity<Object> performOperation(QueryRequestEntity requestEntity) {

        var validationResult = requestEntity.getErrorMsg();
        if (validationResult != null) {
            return ResponseEntity.badRequest().body(validationResult);
        }

        var fileName = requestEntity.getFileName();
        var shouldParse = requestEntity.shouldParse();

        List<IMDbMovieEntity> records = null;

        if (!shouldParse) {
            records = holder.getEntities(fileName);
        }

        if (records == null) {
            records = requestParseFile(fileName);
            if (records == null) {
                return ResponseEntity.badRequest().body("No data to proceed in both local cache and requested file!");
            }
        }

        var biFunction = processorAssigner.assignProcessor(requestEntity);

        var result = biFunction.apply(records, requestEntity);
        return ResponseEntity.ok().

                body(result);

    }

    private List<IMDbMovieEntity> requestParseFile(String fileName) {
        var path = fileHandlingService.resolveFilePath(fileName);
        if (path != null) {
            return parser.parseFile(path.toFile());
        }
        return null;
    }


}
