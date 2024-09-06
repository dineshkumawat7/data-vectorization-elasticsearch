package com.ebit.vectorization.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BagOfWordsService {
    public Map<String, Integer> createBagOfWords(String text){
        String[] words = text.toLowerCase().split("\\s+");
        Map<String, Integer> bagOfWords = new HashMap<>();
        for(String word : words){
            word = word.replaceAll("[^a-zA-Z]", "");
            if(word.isEmpty()){
                continue;
            }
            bagOfWords.put(word, bagOfWords.getOrDefault(word, 0) + 1);
        }
        return bagOfWords;
    }
}
