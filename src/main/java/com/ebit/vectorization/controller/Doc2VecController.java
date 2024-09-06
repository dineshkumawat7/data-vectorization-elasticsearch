package com.ebit.vectorization.controller;

import com.ebit.vectorization.service.BagOfWordsService;
import com.ebit.vectorization.service.Doc2VecService;
import com.ebit.vectorization.service.DocumentReaderService;
import com.ebit.vectorization.service.NGramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
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

    @GetMapping("/getVector")
    public ResponseEntity<?> getVectorByWord(@RequestParam("word") String word){
        try{
            double[] vector = doc2VecService.getVectorByWord(word);
            return ResponseEntity.ok(vector);
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/similar")
    public ResponseEntity<?> getSimilarWords(@RequestParam("word") String word, @RequestParam(defaultValue = "10") int topN){
        try{
            List<String> words = doc2VecService.findSimilarWords(word, topN);
            return ResponseEntity.ok(words);
        }catch (Exception e){
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
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
