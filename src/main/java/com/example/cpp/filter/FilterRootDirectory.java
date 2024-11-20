package org.example.filter;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FilterRootDirectory {
    public Map<List<Path>, List<Path>> collectFileData(){
        Path rootDirectory = Paths.get("C:\\Users\\ADMIN\\CodeWalker\\Example");

        // Lấy danh sách các tệp trong thư mục
        Map<String, Path> inputFiles = new TreeMap<>();
        Map<String, Path> outputFiles = new TreeMap<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootDirectory)) {
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    String fileName = path.getFileName().toString();
                    if (fileName.startsWith("input")) {
                        inputFiles.put(fileName.replace("input", ""), path);
                    } else if (fileName.startsWith("output")) {
                        outputFiles.put(fileName.replace("output", ""), path);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Ánh xạ các tệp input với output
        List<Path> inputPaths = new ArrayList<>();
        List<Path> expectedOutputPaths = new ArrayList<>();

        for (String key : inputFiles.keySet()) {
            if (outputFiles.containsKey(key)) {
                inputPaths.add(inputFiles.get(key));
                expectedOutputPaths.add(outputFiles.get(key));
            }
        }

        Map<List<Path>, List<Path>> response = new HashMap<>();
        response.put(inputPaths, expectedOutputPaths);
//        for (int i = 0; i < inputPaths.size(); i++) {
//            System.out.println("Input: " + inputPaths.get(i));
//            System.out.println("Output: " + expectedOutputPaths.get(i));
//        }
        return response;
    }
}
