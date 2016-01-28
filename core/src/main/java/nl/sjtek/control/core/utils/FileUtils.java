package nl.sjtek.control.core.utils;

import java.io.*;

/**
 * Created by wouter on 21-12-15.
 */
public class FileUtils {

    private FileUtils() {

    }

    public static String readFile(String path) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();

            while (line != null) {
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
                line = bufferedReader.readLine();
            }
            return stringBuilder.toString();
        }
    }

    public static void writeFile(String path, String data) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new PrintWriter(new FileWriter(path)))) {
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
        }
    }
}
