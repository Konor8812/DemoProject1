package com.illia.client.service.query;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.holder.IMDbMovieHolderImpl;
import com.illia.client.model.parser.IMDbMovieParser;
import com.illia.client.model.request.entity.QueryEntity;
import com.illia.client.service.file.FileHandlingException;
import com.illia.client.service.file.FileHandlingService;
import com.illia.client.service.query.processor.ProcessorAssigner;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<IMDbMovieEntity> performOperation(QueryEntity queryEntity) throws QueryProcessingException {
        var fileName = queryEntity.getFileName();

        List<IMDbMovieEntity> records = null;
        if (queryEntity.shouldParse()) {
            try{
                records = requestParseFile(fileName);
            }catch (FileHandlingException ex){
                throw new QueryProcessingException(ex.getMessage());
            }
        } else {
            if ((records = holder.getEntities(fileName)) == null) {
                throw new QueryProcessingException("Local cache is empty!");
            }
        }
        return processorAssigner.assignProcessor(queryEntity)
                        .apply(records, queryEntity);
    }

    private List<IMDbMovieEntity> requestParseFile(String fileName) throws FileHandlingException {
        return parser.parseFile(fileHandlingService.resolveFilePath(fileName));
    }

}
