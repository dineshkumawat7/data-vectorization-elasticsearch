package com.ebit.vectorization.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "vector_data")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentEntity {
    @Id
    private String id;
    private String content;
    private double[] vector;
}
