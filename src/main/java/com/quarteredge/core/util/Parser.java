package com.quarteredge.core.util;

import java.io.*;
import java.util.Iterator;

public class Parser {
    private static File file;

    // private Map<String, List<int[]>> sessionMap;

    public Parser(File file) {
        Parser.file = file;
        // this.sessionMap = new LinkedHashMap<>();
    }

    public void parse() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Iterator<String> iterator = reader.lines().iterator();
            while(iterator.hasNext()) {
                System.out.println(iterator.next());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
