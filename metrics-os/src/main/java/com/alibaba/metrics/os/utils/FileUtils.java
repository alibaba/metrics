package com.alibaba.metrics.os.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    /**
     * Read the content of file
     * @param path the path to file
     * @return the file content as string
     */
    public static String readFile(String path) {
        BufferedReader reader = null;
        StringBuffer sb = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(new File(path)));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return sb.toString();
    }

    /**
     * Read the content of file
     * @param path the path to file
     * @return the file content as string
     */
    public static List<String> readFileAsStringArray(String path) throws IOException {
        BufferedReader reader = null;
        List<String> lines = new ArrayList<String>();
        try {
            File p = new File(path);
            if (!p.exists()) {
                return lines;
            }
            reader = new BufferedReader(new FileReader(new File(path)));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return lines;
    }
}
