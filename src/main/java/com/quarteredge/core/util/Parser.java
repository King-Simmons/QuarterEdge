package com.quarteredge.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class Parser {
    private static File file;
    private Map<String, List<List<String>>> sessionMap;

    public Parser(File file) {
        Parser.file = file;
        this.sessionMap = new LinkedHashMap<>();
    }

    public void parse() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Iterator<String> iterator = reader.lines().iterator();
            List<List<String>> currSession = new ArrayList<>();

            while (iterator.hasNext()) {
                List<String> currLine = Arrays.asList(iterator.next().split("[, ]"));
                currSession.add(currLine.subList(1, currLine.size()));
                if (currLine.get(1).equals("16:55:00")) {
                    sessionMap.put(currLine.getFirst(), new ArrayList<>(currSession));
                    currSession.clear();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, List<List<String>>> getSessionMap() {
        return sessionMap;
    }
}
