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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TFIDFWordEmbeddingController {
    @Autowired
    private DocumentReaderService documentReaderService;
    @Autowired
    private TFIDFEmbeddingService tfidfEmbeddingService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadAndProcessFile(@RequestParam("file") MultipartFile[] files){
        try {
            List<String> documents = new ArrayList<>();
            for (MultipartFile file : files) {
                File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
                file.transferTo(tempFile);
                String content = documentReaderService.readWordDocument(file);
                documents.add(content);
            }
            tfidfEmbeddingService.indexDocuments(documents);
            return ResponseEntity.status(HttpStatus.OK).body("Documents processed and indexed successfully");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
