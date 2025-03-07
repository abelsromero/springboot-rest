package com.example.client;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LinesWriter {

    private final String filePath;
    private final List<String> lines = new ArrayList<>();

    public LinesWriter(String file) {
        this.filePath = file;
    }

    public void addLine(String line) {
        lines.add(line);
    }

    public void writeAll() {
        writeLines();
    }

    private void writeLines() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}