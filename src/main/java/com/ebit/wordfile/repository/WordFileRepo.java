package com.ebit.wordfile.repository;


import com.ebit.wordfile.entity.WordFile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordFileRepo extends ElasticsearchRepository<WordFile, String> {

}
