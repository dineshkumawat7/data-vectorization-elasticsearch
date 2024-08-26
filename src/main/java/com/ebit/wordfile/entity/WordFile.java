package com.ebit.wordfile.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "word_file")
@Data
public class WordFile {
    @Id
    private String id;
    @Field(type = FieldType.Text)
    private String fileName;
    @Field(type = FieldType.Text)
    private String content;
    @Field(type = FieldType.Dense_Vector, dims = 100)
    private long[] contentVector;
}
