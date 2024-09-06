package com.ebit.vectorization.controller;

import com.ebit.vectorization.service.DocumentReaderService;
import com.ebit.vectorization.service.WordEmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class WordEmbeddingController {

    @Autowired
    private WordEmbeddingService wordEmbeddingService;
    @Autowired
    private DocumentReaderService documentReaderService;

    @PostMapping("/train")
    public String trainModel(@RequestParam("file") MultipartFile file) {
        String sentences = documentReaderService.readWordDocument(file);
//        wordEmbeddingService.train(sentences);
        return "Model trained successfully!";
    }

    @GetMapping("/vector/{word}")
    public double[] getWordVector(@PathVariable String word) {
        return wordEmbeddingService.getWordVector(word);
    }

    @PostMapping("/document-vector")
    public double[] getDocumentVector(@RequestBody String text) {
        return wordEmbeddingService.getAverageDocumentVector(text);
    }
}
