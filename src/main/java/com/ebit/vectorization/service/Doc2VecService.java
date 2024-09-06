package com.ebit.vectorization.service;

import com.ebit.vectorization.entity.DocumentEntity;
import com.ebit.vectorization.repository.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors.Builder;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.util.ModelSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

@Service
@Slf4j
public class Doc2VecService {
    @Autowired
    private DocumentRepository documentRepository;

    private ParagraphVectors paragraphVectors;

    public boolean saveDocument(String content, double[] doc2vector){
        try{
            DocumentEntity documentEntity = new DocumentEntity();
            documentEntity.setContent(content);
            documentEntity.setVector(doc2vector);
            documentRepository.save(documentEntity);
            return true;
        }catch (Exception e){
            log.info("document save process failed: {}", e.getMessage());
            return false;
        }
    }

    public void train(String content){
        CollectionSentenceIterator sentenceIterator = new CollectionSentenceIterator(Collections.singletonList(content));
        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        paragraphVectors = new ParagraphVectors.Builder()
                .iterate(sentenceIterator)
                .tokenizerFactory(tokenizerFactory)
                .build();

        paragraphVectors.fit();
    }

    public double[] inferVector(String content){
        return paragraphVectors.inferVector(content).toDoubleVector();
    }
}
