package com.ebit.wordfile.controller;

import com.ebit.wordfile.service.WordFileReaderService;
import com.ebit.wordfile.service.WordFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class WordFileController {
    @Autowired
    private WordFileService wordFileService;

    @PostMapping("/upload")
    public ResponseEntity<String> saveWordFileContent(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("file is required");
        }
        String fileName = file.getOriginalFilename();
        boolean isSave = wordFileService.saveFileContent(file);
        if (isSave) {
            return ResponseEntity.status(HttpStatus.OK).body("file is successfully saved in elasticsearch");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("internal server error");
    }
}
