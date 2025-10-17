package com.quarteredge;

import com.quarteredge.core.util.Parser;
import java.io.File;

/**
 * Main application class for QuarterEdge.
 * This class serves as the entry point for the QuarterEdge application.
 */
public class QuarterEdgeApplication {

    /**
     * Application entry point.
     * Prints "Hello World" to the standard output stream.
     */
    static void main() {
        System.out.println("Hello World");
        Parser parser = new Parser(new File("data/CL_5min_sample.csv"));
        parser.parse();
        parser.getSessionMap()
                .forEach(
                        (key, value) -> {
                            IO.println(key);
                            value.forEach(IO::println);
                        });
    }
}
