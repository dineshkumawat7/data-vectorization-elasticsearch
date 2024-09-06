package com.ebit.vectorization.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class DocumentReaderService {
    public String readWordDocument(MultipartFile file){
        if (file.getOriginalFilename().endsWith(".doc")) {
            try {
                HWPFDocument document = new HWPFDocument(file.getInputStream());
                WordExtractor extractor = new WordExtractor(document);
                return extractor.getText();
            } catch (Exception e) {
                log.error("Error for reading .doc file {}" ,e.getMessage());
            }
        } else if (file.getOriginalFilename().endsWith(".docx")) {
            try {
                XWPFDocument document = new XWPFDocument(file.getInputStream());
                StringBuilder text = new StringBuilder();
                List<XWPFParagraph> paragraphs = document.getParagraphs();

                for (XWPFParagraph paragraph : paragraphs) {
                    text.append(paragraph.getText()).append("\n");
                }
                return text.toString();
            } catch (Exception e) {
                log.error("Error for reading .docx file {}" ,e.getMessage());
            }
        } else if (file.getOriginalFilename().endsWith(".pdf")) {
            try{
                PDDocument document = PDDocument.load(file.getInputStream());
                PDFTextStripper pdfTextStripper = new PDFTextStripper();
                return pdfTextStripper.getText(document);
            } catch (IOException e) {
                log.error("Error for reading .pdf word file {}" ,e.getMessage());
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("Unsupported file type");
        }
        return "";
    }
}
