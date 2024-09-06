package com.ebit.vectorization.service;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class TFIDFEmbeddingService {

    private final Analyzer analyzer;
    private final Directory directory;
    private final ClassicSimilarity similarity;

    public TFIDFEmbeddingService() {
        this.analyzer = new StandardAnalyzer();
        this.directory = new RAMDirectory();
        this.similarity = new ClassicSimilarity();
    }

    public void indexDocument(String content) throws IOException {
        try (IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer))) {
            Document doc = new Document();
            doc.add(new TextField("content", content, Field.Store.YES));
            writer.addDocument(doc);
        }
    }

    public float[] calculateTfIdf(String documentText, List<String> terms) throws IOException {
        indexDocument(documentText);

        try (DirectoryReader reader = DirectoryReader.open(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            searcher.setSimilarity(similarity);

            Document doc = searcher.doc(0); // Assume single document
            float[] tfIdfVector = new float[terms.size()];

            for (int i = 0; i < terms.size(); i++) {
                Term term = new Term("content", terms.get(i));
                long termFreq = reader.totalTermFreq(term);
                long docFreq = reader.docFreq(term);

                if (termFreq > 0) {
                    tfIdfVector[i] = similarity.tf(termFreq) * similarity.idf(docFreq, reader.maxDoc());
                } else {
                    tfIdfVector[i] = 0.0f;
                }
            }
            return tfIdfVector;
        }
    }
}
