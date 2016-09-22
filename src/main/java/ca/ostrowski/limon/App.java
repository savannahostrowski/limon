package ca.ostrowski.limon;

import org.apache.commons.io.FilenameUtils;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static spark.Spark.*;

public class App {
    static String url = "http://www.omdbapi.com/";

    public static class Movie {
        public String Title;
        public String Year;
    }

    public interface OMDb {
        @GET("/")
        Call<Movie> movie(
          @Query("t") String title
        );
    }
    public static void main(String[] args) throws IOException {
        //Root directory to search for movies
        List<String> moviesFound = App.directorySearch("/home/savannah/Documents/code/limon/");

        //Create REST adapter which points to OMDb API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //secret subclass of OMDb generated by retrofit
        OMDb omdb = retrofit.create(OMDb.class);

        getMovieInfo(omdb, moviesFound);
    }

    private static Set<String> movieExtensions = new HashSet<>(Arrays.asList("mp4", "avi", "mkv", "webm", "mov", "wmv",
            "m4p", "m4v", "mpg", "mp2", "mpeg", "mpe", "mpv", "m2v"));

    public static List<String> directorySearch(String rootDir) throws IOException {
        List<String> movies = new ArrayList<>();
        Files.walk(Paths.get(rootDir)).forEach(filePath -> {
            if (!Files.isRegularFile(filePath)) {
                return;
            }
            String ext = FilenameUtils.getExtension(filePath.toString());
            for (String extensionInstance: movieExtensions) {
                if (ext.equals(extensionInstance)) {
                    movies.add(FilenameUtils.getBaseName(filePath.toString()));
                }
            }
        });
        return movies;
    }

    public static List<Movie> getMovieInfo (OMDb omdb, List<String> movies) throws IOException {
        List<Movie> moviesJson = new ArrayList<>();
        for (String film: movies) {
            Movie m = omdb.movie(film).execute().body();
            moviesJson.add(m);
            System.out.println(m.Title + m.Year);
        }
        return moviesJson;
    }
};

