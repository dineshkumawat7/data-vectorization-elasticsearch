package com.ebit.wordfile.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class WordFileReaderService {
    public String readWordFileText(String filePath) throws IOException {
        try(FileInputStream fis = new FileInputStream(filePath)){
            XWPFDocument document = new XWPFDocument(fis);
            StringBuilder text = new StringBuilder();
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                text.append(paragraph.getText()).append("\n");
            }
            return text.toString();
        }
    }
}
