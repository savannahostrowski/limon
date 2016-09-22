package ca.ostrowski.limon;

import org.apache.commons.io.FilenameUtils;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

import static spark.Spark.*;

public class App {
    static String url = "http://www.omdbapi.com/";

    public static class Movie {
        public String imdbID;
        public String Title;
        public String Year;
        public String Genre;
        public String Director;
        public String Actors;
        public String Plot;
        public String Poster;
    }

    public interface OMDb {
        @GET("/")
        Call<Movie> movie(
          @Query("t") String title
        );
    }

    static OMDb omdb = null;
    static Connection connection = null;

    public static void main(String[] args) throws IOException {
        //Root directory to search for movies
        List<String> moviesFound = App.directorySearch("/home/savannah/Documents/code/limon/");

        //Create REST adapter which points to OMDb API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //secret subclass of OMDb generated by retrofit
        omdb = retrofit.create(OMDb.class);

        // get json for the movies found in the directories
        List<Movie> moviesJson = getMovieInfo(moviesFound);

        try {
            //open sqlite db connection
            connection = DriverManager.getConnection("jdbc:sqlite:/home/savannah/Documents/code/limon/movies.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            System.out.println("Connected to DB successfully");

            statement.executeUpdate("drop table if exists movies");
            statement.executeUpdate("create table movies (imdbID char(20), Title char(50), Year char(50)," +
            "Genre char(50), Director char(50), Actors char(255), Plot varchar(500), Poster char(100))");
            dbInsert(moviesJson);

        }
        catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
        finally {
            try {
                //if there already exists a connection
                if (connection != null) {
                    connection.close();
                }
            }
            catch (SQLException e) {
                //connection close failed
                System.err.println(e);
            }

        }

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

    public static List<Movie> getMovieInfo (List<String> movies) throws IOException {
        List<Movie> moviesJson = new ArrayList<>();
        for (String film: movies) {
            Movie m = omdb.movie(film).execute().body();
            moviesJson.add(m);
        }
        return moviesJson;
    }

    public static String quote (String s) throws IOException {
        return "'" + s +"'";
    }

    public static String createSQLtuple (Movie filmJson) throws IOException{
        return quote(filmJson.imdbID) + ", " + quote(filmJson.Title) + ", " + quote(filmJson.Year) + ", " +
                quote(filmJson.Genre) + ", " + quote(filmJson.Director) + ", " + quote(filmJson.Actors) + ", " +
                quote(filmJson.Plot) + ", " + quote(filmJson.Poster);
    }

    public static void dbInsert (List<Movie> moviesJson) throws IOException, SQLException {
        for (Movie filmJson: moviesJson) {
            String filmTuple = createSQLtuple(filmJson);
            String sqlStatement = "insert into movies values(?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sqlStatement);
            statement.setString(1, filmJson.imdbID);
            statement.setString(2, filmJson.Title);
            statement.setString(3, filmJson.Year);
            statement.setString(4, filmJson.Genre);
            statement.setString(5, filmJson.Director);
            statement.setString(6, filmJson.Actors);
            statement.setString(7, filmJson.Plot);
            statement.setString(8, filmJson.Poster);
            
            statement.executeUpdate();


        }
    }
}

