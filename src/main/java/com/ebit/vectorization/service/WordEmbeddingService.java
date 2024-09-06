package com.ebit.vectorization.service;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WordEmbeddingService {
    private Word2Vec word2Vec;

    public void train(List<String> sentences){
        CollectionSentenceIterator collectionSentenceIterator = new CollectionSentenceIterator(sentences);
        DefaultTokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();

        word2Vec = new Word2Vec.Builder()
                .iterate(collectionSentenceIterator)
                .tokenizerFactory(tokenizerFactory)
//                .vectorSize(100)
                .minWordFrequency(1)
                .seed(42)
                .build();
        word2Vec.fit();
    }

    public double[] getWordVector(String word) {
        if (word2Vec == null) {
            throw new IllegalStateException("Model has not been trained yet!");
        }
        return word2Vec.getWordVector(word);
    }

    public double[] getAverageDocumentVector(String text) {
        if (word2Vec == null) {
            throw new IllegalStateException("Model has not been trained yet!");
        }
        String[] words = text.toLowerCase().split("\\s+");
        List<double[]> vectors = new ArrayList<>();
        for (String word : words) {
            double[] vector = word2Vec.getWordVector(word);
            if (vector != null) {
                vectors.add(vector);
            }
        }
        return vectors.stream()
                .reduce(new double[word2Vec.getLayerSize()], (a, b) -> {
                    for (int i = 0; i < a.length; i++) {
                        a[i] += b[i];
                    }
                    return a;
                });
    }
}
