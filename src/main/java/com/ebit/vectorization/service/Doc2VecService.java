package com.ebit.vectorization.service;

import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import com.ebit.vectorization.entity.DocumentEntity;
import com.ebit.vectorization.repository.DocumentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors.Builder;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.util.ModelSerializer;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class Doc2VecService {
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private RestClient restClient;
    private ParagraphVectors paragraphVectors;
    private ObjectMapper objectMapper = new ObjectMapper();


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

    public double[] getVectorByWord(String word) throws IOException {
        Request request = new Request("GET", "/vector_data/_doc/" + word);
        Response response = restClient.performRequest(request);
        Map<String, Object> responseMap =
                new ObjectMapper().readValue(response.getEntity().getContent(), Map.class);

        Map<String, Object> source = (Map<String, Object>) responseMap.get("_source");
        ArrayList<Double> vectorList = (ArrayList<Double>) source.get("vector");
        return vectorList.stream().mapToDouble(Double::doubleValue).toArray();
    }

    public List<String> findSimilarWords(String word, int topN) throws IOException {
        double[] queryVector = getVectorByWord(word);
        String queryVectorJson = objectMapper.writeValueAsString(queryVector);

        String script = "cosineSimilarity(params.vector_data, 'vector') + 1.0";

        String jsonQuery = "{\n" +
                "  \"size\": " + topN + ",\n" +
                "  \"query\": {\n" +
                "    \"script_score\": {\n" +
                "      \"query\": { \"match_all\": {} },\n" +
                "      \"script\": {\n" +
                "        \"source\": \"" + script + "\",\n" +
                "        \"params\": { \"vector_data\": " + queryVectorJson + " }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        Request request = new Request("POST", "/vector_data/_search");
        request.setJsonEntity(jsonQuery);

        Response response = restClient.performRequest(request);

        Map<String, Object> responseMap =
                objectMapper.readValue(response.getEntity().getContent(), Map.class);
        List<Map<String, Object>> hits = (List<Map<String, Object>>)
                ((Map<String, Object>) responseMap.get("hits")).get("hits");

        List<String> similarWords = new ArrayList<>();
        for (Map<String, Object> hit : hits) {
            String similarWord = (String) hit.get("_id");
            similarWords.add(similarWord);
        }

        return similarWords;
    }

}
