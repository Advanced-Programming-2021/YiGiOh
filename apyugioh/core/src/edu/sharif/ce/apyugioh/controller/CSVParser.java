package edu.sharif.ce.apyugioh.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CSVParser {

    private List<String> headings;
    private List<HashMap<String, String>> contentsAsMap;
    private List<String[]> contents;

    public CSVParser(String fileName) {
        headings = new ArrayList<>();
        contentsAsMap = new ArrayList<>();
        FileHandle filePath = Gdx.files.internal(fileName);
        try {
            if (filePath.exists()) {
                CSVReader csvReader = new CSVReaderBuilder(filePath.reader()).build();
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
            } else {
                System.out.println("CSV not found!");
                System.out.println(filePath.toString());
            }
        } catch (Exception e) {
            Utils.printError("can't read CSV");
            e.printStackTrace();
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
