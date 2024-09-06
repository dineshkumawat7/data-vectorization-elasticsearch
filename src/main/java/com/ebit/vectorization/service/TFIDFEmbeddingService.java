package com.ebit.vectorization.service;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.RAMDirectory;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TFIDFEmbeddingService {

    private Word2Vec word2Vec;
    private RAMDirectory index;
    private TFIDFSimilarity similarity = new ClassicSimilarity();
    private Analyzer analyzer = new StandardAnalyzer();

    public void trainWord2VecModel(List<String> sentences) {
        CollectionSentenceIterator sentenceIterator = new CollectionSentenceIterator(sentences);
        DefaultTokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();

        word2Vec = new Word2Vec.Builder()
                .iterate(sentenceIterator)
                .tokenizerFactory(tokenizerFactory)
//                .vectorSize(100)
                .minWordFrequency(1)
                .seed(42)
                .build();

        word2Vec.fit();
    }

    public void indexDocuments(List<String> documents) throws IOException {
        index = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(index, config);

        for (int i = 0; i < documents.size(); i++) {
            Document doc = new Document();
            doc.add(new StringField("id", String.valueOf(i), Field.Store.YES));
            doc.add(new TextField("content", documents.get(i), Field.Store.YES));
            writer.addDocument(doc);
        }

        writer.close();
    }

    public double computeTfIdf(String term, String document) throws IOException {
        IndexReader reader = DirectoryReader.open(index);
        int docCount = reader.numDocs();
        int termDocFreq = reader.docFreq(new org.apache.lucene.index.Term("content", term));
        int termFreq = 0;

        for (int i = 0; i < reader.maxDoc(); i++) {
            Document doc = reader.document(i);
            if (doc.get("content").equals(document)) {
                termFreq = reader.docFreq(new org.apache.lucene.index.Term("content", term));
                break;
            }
        }

        double idf = similarity.idf(termDocFreq, docCount);
        double tf = similarity.tf(termFreq);

        return tf * idf;
    }

    public double[] getTfIdfWeightedWordEmbedding(String document) throws IOException {
        if (word2Vec == null) {
            throw new IllegalStateException("Word2Vec model has not been trained yet!");
        }

        String[] words = document.toLowerCase().split("\\s+");
        List<double[]> vectors = new ArrayList<>();
        double totalWeight = 0.0;

        for (String word : words) {
            double tfIdf = computeTfIdf(word, document);
            double[] vector = word2Vec.getWordVector(word);

            if (vector != null) {
                for (int i = 0; i < vector.length; i++) {
                    vector[i] *= tfIdf;
                }
                vectors.add(vector);
                totalWeight += tfIdf;
            }
        }

        double[] weightedAverageVector = vectors.stream()
                .reduce(new double[word2Vec.getLayerSize()], (a, b) -> {
                    for (int i = 0; i < a.length; i++) {
                        a[i] += b[i];
                    }
                    return a;
                });

        for (int i = 0; i < weightedAverageVector.length; i++) {
            weightedAverageVector[i] /= totalWeight;
        }

        return weightedAverageVector;
    }
}
