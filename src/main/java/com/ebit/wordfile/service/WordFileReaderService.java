package com.ebit.wordfile.service;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class WordFileReaderService {
    public String readWordFile(MultipartFile file) throws IOException {
        if (file.getOriginalFilename().endsWith(".doc")) {
            // Handling for .doc (OLE2) files
            try {
                HWPFDocument document = new HWPFDocument(file.getInputStream());
                WordExtractor extractor = new WordExtractor(document);
                return extractor.getText();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (file.getOriginalFilename().endsWith(".docx")) {
            // Handling for .docx (OOXML) files
            try {
                XWPFDocument document = new XWPFDocument(file.getInputStream());
                StringBuilder text = new StringBuilder();
                List<XWPFParagraph> paragraphs = document.getParagraphs();

                for (XWPFParagraph paragraph : paragraphs) {
                    text.append(paragraph.getText()).append("\n");
                }
                return text.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("Unsupported file type");
        }
        return "";
    }
}
