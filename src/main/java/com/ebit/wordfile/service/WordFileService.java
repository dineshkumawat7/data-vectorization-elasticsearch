package com.ebit.wordfile.service;

import com.ebit.wordfile.entity.WordFile;
import com.ebit.wordfile.repository.WordFileRepo;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Service
public class WordFileService {
    @Autowired
    private WordFileRepo wordFileRepo;
    @Autowired
    private WordFileReaderService wordFileReaderService;
    @Autowired
    private VectorizationService vectorizationService;

    public boolean saveFileContent(MultipartFile file) {
        try {
            String content = wordFileReaderService.readWordFile(file);
            WordFile wordFile = new WordFile();
            wordFile.setFileName(file.getOriginalFilename());
            wordFile.setContent(content);
            Word2Vec word2Vec = vectorizationService.createWord2VecModel(file.getInputStream());
            INDArray indArray = vectorizationService.textToVector(word2Vec, content);
            double[] vector = indArray.toDoubleVector();
            wordFile.setContentVector(Arrays.stream(vector).mapToLong(i -> (long) i).toArray());
            WordFile savedContent = wordFileRepo.save(wordFile);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
