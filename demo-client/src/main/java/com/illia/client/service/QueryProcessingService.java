package com.illia.client.service;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.IMDbMovieHolderImpl;
import com.illia.client.model.IMDbMovieParser;
import com.illia.client.service.processor.InvalidAttributeException;
import com.illia.client.service.processor.ProcessorAssigner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    /**
     * params:
     * fileName | required
     * operation | required, possible values [delete, sort]
     * attribute | required
     * reparse | not required, default true
     * order | not required, default asc
     * amount | not required, returns everything by default
     */

    public ResponseEntity<Object> performOperation(Map<String, String> params) {
        var validateParamsMsg = validateParams(params);

        if (!validateParamsMsg.equals("ok")) {
            return ResponseEntity.badRequest().body(validateParamsMsg);
        }

        var fileName = params.get("fileName");
        var shouldParse = params.get("shouldParse");
        var operation = params.get("operation");


        List<IMDbMovieEntity> records = null;
        if ((shouldParse == null
                || shouldParse.equals("false"))
                && holder.holdsFile(fileName)) {
            records = holder.getEntities();
            shouldParse = "false";
        } else {
            shouldParse = "true";
        }

        if (shouldParse.equals("true")) {
            var path = fileHandlingService.resolveFilePath(fileName);
            if (path != null) {
                records = parser.parseFile(path.toFile());
            } else {
                return ResponseEntity.badRequest().body("No such file!");
            }
        }

        var processor = processorAssigner.assignProcessor(operation);
        if (processor != null) {
            try {
                var result = processor.proceed(records, params);
                return ResponseEntity.ok().body(result);
            } catch (InvalidAttributeException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid/Unsupported operation!");
        }
    }


    private String validateParams(Map<String, String> params) {
        StringBuilder responseBuilder = new StringBuilder();
        var fileName = params.get("fileName");
        var attribute = params.get("attribute");
        var operation = params.get("operation");
        if (fileName == null || fileName.isEmpty()) {
             responseBuilder.append(" File name is not specified!");
        }
        if (attribute == null || attribute.isEmpty()) {
            responseBuilder.append(" Attribute is not specified!");
        }
        if (operation == null || operation.isEmpty()) {
            responseBuilder.append(" Operation is not specified!");
        }
        return responseBuilder.length() == 0 ? "ok" : responseBuilder.toString();
    }
}
