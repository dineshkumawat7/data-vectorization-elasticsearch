package com.ebit.vectorization.controller;

import com.ebit.vectorization.service.BagOfWordsService;
import com.ebit.vectorization.service.Doc2VecService;
import com.ebit.vectorization.service.DocumentReaderService;
import com.ebit.vectorization.service.NGramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class Doc2VecController {
    @Autowired
    private Doc2VecService doc2VecService;
    @Autowired
    private DocumentReaderService documentReaderService;
    @Autowired
    private BagOfWordsService bagOfWordsService;
    @Autowired
    private NGramService nGramService;

    @PostMapping("/process")
    public ResponseEntity<String> processFile(@RequestParam(required = true, name = "file") MultipartFile file) {
        String content = documentReaderService.readWordDocument(file);
        doc2VecService.train(content);
        double[] vectorContent = doc2VecService.inferVector(content);
        doc2VecService.saveDocument(content, vectorContent);
        return ResponseEntity.status(HttpStatus.OK).body("Document processed and save successfully");
    }

    @PostMapping("/bagOfWords")
    public ResponseEntity<Map<String, Integer>> getBagOfWords(@RequestParam(required = true, name = "file") MultipartFile file){
        String content = documentReaderService.readWordDocument(file);
        Map<String, Integer> bagOfWords = bagOfWordsService.createBagOfWords(content);
         return ResponseEntity.status(HttpStatus.OK).body(bagOfWords);
    }

    @PostMapping("/n-grams")
    public ResponseEntity<Map<String, Integer>> getNGrams(@RequestParam("n") int n, @RequestParam("file") MultipartFile file){
        String content = documentReaderService.readWordDocument(file);
        Map<String, Integer> nGrams = nGramService.createNGrams(n, content);
        return ResponseEntity.status(HttpStatus.OK).body(nGrams);
    }
}
