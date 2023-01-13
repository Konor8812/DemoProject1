package com.illia.client.service;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.IMDbMovieHolderImpl;
import com.illia.client.model.IMDbMovieParser;
import com.illia.client.service.processor_registry.ProcessorAssigner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QueryProcessingService {

    @Autowired
    FileHandlingService fileHandlingService;

    @Autowired
    ProcessorAssigner processorAssigner;
    @Autowired
    IMDbMovieParser parser;
    @Autowired
    IMDbMovieHolderImpl holder;

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

        if (shouldParse == null || shouldParse.equals("false")) {
            if (holder.holdsFile(fileName)) {
                records = holder.getEntities();
                shouldParse = "false";
            } else {
                shouldParse = "true";
            }
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
            } catch (NoSuchFieldException e) {
                return ResponseEntity.badRequest().body("Invalid attribute!");
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid/Unsupported operation!");
        }
    }

    private String validateParams(Map<String, String> params) {
        var fileName = params.get("fileName");
        var attribute = params.get("attribute");
        var operation = params.get("operation");
        if (fileName == null || fileName.isEmpty()) {
            return "File name is not specified!";
        } else if (attribute == null || attribute.isEmpty()) {
            return "Attribute is not specified!";
        } else if (operation == null || operation.isEmpty()) {
            return "Operation is not specified!";
        }
        return "ok";
    }
}
