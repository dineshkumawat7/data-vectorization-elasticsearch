package com.ebit.wordfile.service;

import com.ebit.wordfile.entity.WordFile;
import com.ebit.wordfile.repository.WordFileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WordFileService {
    @Autowired
    private WordFileRepo wordFileRepo;

    public boolean saveFileContent(String fileName, String content){
        WordFile wordFile = new WordFile();
        wordFile.setFileName(fileName);
        wordFile.setContent(content);
        WordFile savedContent = wordFileRepo.save(wordFile);
        if(savedContent != null){
            return true;
        }
        return false;
    }
}
