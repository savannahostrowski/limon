package ca.ostrowski.limon;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static spark.Spark.*;

public class App {
    public static void main( String[] args ) throws IOException {
        App.directorySearch("/home/savannah/Documents/code/limon/");

    }
    private static Set<String> movieExtensions = new HashSet<>(Arrays.asList("mp4", "avi", "mkv", "webm", "mov", "wmv",
            "m4p", "m4v", "mpg", "mp2", "mpeg", "mpe", "mpv", "m2v"));

    private static List<String> movies = new ArrayList<>();

    public static String directorySearch(String rootDir) throws IOException {
        Files.walk(Paths.get(rootDir)).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                String ext = FilenameUtils.getExtension(filePath.toString());
                for (String extensionInstance: movieExtensions) {
                    if (ext.equals(extensionInstance)) {
                        movies.add(FilenameUtils.getBaseName(filePath.toString()));
                    }
                }
            }
        });
        if (movies.size() != 0) {
            for (String movie: movies) {
                System.out.println(movie);
            }
        }
        return movies.toString();
    }
};

