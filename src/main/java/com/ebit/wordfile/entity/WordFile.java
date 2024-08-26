package com.ebit.wordfile.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "word_file")
@Data
public class WordFile {
    @Id
    private String id;
    private String fileName;
    private String content;
}
