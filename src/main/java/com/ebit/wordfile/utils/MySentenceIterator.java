package com.ebit.wordfile.utils;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MySentenceIterator implements SentenceIterator {

    private BufferedReader reader;
    private String currentLine;

    public MySentenceIterator(String filePath) throws IOException {
        reader = new BufferedReader(new FileReader(filePath));
    }

    @Override
    public String nextSentence() {
        try {
            currentLine = reader.readLine();
            return currentLine;
        } catch (IOException e) {
            throw new RuntimeException("Error reading line from file", e);
        }
    }

    @Override
    public boolean hasNext() {
        try {
            return reader.ready();
        } catch (IOException e) {
            throw new RuntimeException("Error checking if reader is ready", e);
        }
    }

    @Override
    public void reset() {
        try {
            reader.close();
            // Re-open the file for reading
            reader = new BufferedReader(new FileReader("path/to/your/file.txt"));
        } catch (IOException e) {
            throw new RuntimeException("Error resetting reader", e);
        }
    }

    @Override
    public void finish() {
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing reader", e);
        }
    }

    @Override
    public SentencePreProcessor getPreProcessor() {
        return null; // Implement if needed
    }

    @Override
    public void setPreProcessor(SentencePreProcessor preProcessor) {
        // Implement if needed
    }
}