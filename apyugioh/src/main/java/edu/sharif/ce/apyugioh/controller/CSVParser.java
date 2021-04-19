package edu.sharif.ce.apyugioh.controller;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CSVParser {

    private String fileName;
    private Path filePath;
    private List<String> headings;
    private List<HashMap<String, String>> contentsAsMap;
    private List<String[]> contents;

    public CSVParser(String fileName) {
        this.fileName = fileName;
        headings = new ArrayList<>();
        contentsAsMap = new ArrayList<>();
        filePath = Path.of("assets/" + fileName);
        try {
            if (Files.exists(filePath)) {
                CSVReader csvReader = new CSVReaderBuilder(new FileReader("assets/" + fileName)).build();
                contents = csvReader.readAll();
                if (contents.size() > 0) headings.addAll(Arrays.asList(contents.get(0)));
                headings = headings.stream().map(e -> e.trim().toLowerCase()).collect(Collectors.toList());
                for (int i = 1; i < contents.size(); i++) {
                    HashMap<String, String> data = new HashMap<>();
                    for (int j = 0; j < headings.size(); j++) {
                        data.put(headings.get(j), contents.get(i)[j].trim());
                    }
                    contentsAsMap.add(data);
                }
            }
        } catch (Exception e) {
            Utils.printError("can't read CSV");
            System.exit(0);
        }
    }

    public List<String> getHeadings() {
        return headings;
    }

    public List<HashMap<String, String>> getContentsAsMap() {
        return contentsAsMap;
    }

    public List<String[]> getContents() {
        return contents;
    }
}
