package com.ebit.vectorization.repository;

import com.ebit.vectorization.entity.DocumentEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends ElasticsearchRepository<DocumentEntity, String> {

}
