package com.ecommerce.project.service;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PathSeparatorTest {

    @Test
    void testPathSeparatorIssue() {
        String path = "/images";
        String fileName = "1f0c54a7-3750-4b43-b662-856108a7cd8d.jpg";
        
        // This is what the current code does
        String filePathWithSeparator = path + File.separator + fileName;
        String filePathWithPathSeparator = path + File.pathSeparator + fileName;

        System.out.println("File.separator: " + File.separator);
        System.out.println("File.pathSeparator: " + File.pathSeparator);
        System.out.println("filePathWithSeparator: " + filePathWithSeparator);
        System.out.println("filePathWithPathSeparator: " + filePathWithPathSeparator);

        // On Windows, File.pathSeparator is ';' and File.separator is '\'
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            assertTrue(filePathWithPathSeparator.contains(";"));
            assertFalse(filePathWithPathSeparator.contains("\\")); // In the context of concatenation
            assertTrue(filePathWithSeparator.contains("\\"));
        }
    }
}
