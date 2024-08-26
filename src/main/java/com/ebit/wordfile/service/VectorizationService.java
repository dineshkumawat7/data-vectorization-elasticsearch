package com.ebit.wordfile.service;

import com.ebit.wordfile.utils.MySentenceIterator;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
public class VectorizationService {

    public Word2Vec createWord2VecModel(InputStream filePath) throws FileNotFoundException {
        SentenceIterator iter = new BasicLineIterator(filePath);
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .iterations(1)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();
        vec.fit();
        return vec;
    }

    public INDArray textToVector(Word2Vec word2VecModel, String text) {
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        List<String> tokens = t.create(text).getTokens();
        INDArray vector = word2VecModel.getWordVectorMatrixNormalized(tokens.get(0));
        for (int i = 1; i < tokens.size(); i++) {
            vector.addi(word2VecModel.getWordVectorMatrixNormalized(tokens.get(i)));
        }
        vector.divi(tokens.size());
        return vector;
    }
}
