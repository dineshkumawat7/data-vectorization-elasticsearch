package com.ebit.vectorization.controller;

import com.ebit.vectorization.service.DocumentReaderService;
import com.ebit.vectorization.service.TFIDFEmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
public class TFIDFWordEmbeddingController {
    @Autowired
    private DocumentReaderService documentReaderService;
    @Autowired
    private TFIDFEmbeddingService tfidfEmbeddingService;

    @PostMapping("/upload")
    public ResponseEntity<float[]> uploadAndProcessFile(@RequestParam("file") MultipartFile files){
        try{
            String content = documentReaderService.readWordDocument(files);
            List<String> terms = Arrays.asList(content.split("\\s+"));
            float[] vector = tfidfEmbeddingService.calculateTfIdf(content, terms);
            return ResponseEntity.status(HttpStatus.OK).body(vector);
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
}
