package com.ebit.vectorization.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NGramService {
    public Map<String, Integer> createNGrams(int n ,String text){
        String[] words = text.toLowerCase().split("\\s+");
        Map<String, Integer> nGram = new HashMap<>();
        for(int i = 0; i <= words.length - n; i++){
            StringBuilder stringBuilder = new StringBuilder();
            for(int j = 0; j < n; j++){
                stringBuilder.append(words[i + j]);
                if(j < n - 1){
                    stringBuilder.append(" ");
                }
            }
            String nGramString = stringBuilder.toString().replaceAll("[^a-zA-Z ]", "");
            nGram.put(nGramString, nGram.getOrDefault(nGramString, 0) + 1);
        }
        return nGram;
    }
}
