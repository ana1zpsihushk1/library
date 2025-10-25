package library.Additions;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import library.bookFiles.Book;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

public class StorageS {
    private static final String CATALOG_FILE = "catalog.json";

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Path.class, new pathAdapt())
            .setPrettyPrinting()
            .create();

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Path.class, (JsonSerializer<Path>) (src, t, c) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(Path.class, (JsonDeserializer<Path>) (json, t, c) -> Paths.get(json.getAsString()))
            .setPrettyPrinting()
            .create();

    public void saveCatalog(Collection<Book> books, Path folder) throws IOException {
        Files.createDirectories(folder);
        Path file = folder.resolve("catalog.json");
        try (Writer w = Files.newBufferedWriter(file)) {
            GSON.toJson(books, w);
        }
    }

    public List<Book> loadCatalog(Path folder) throws IOException {
        Path file = folder.resolve("catalog.json");
        if (!Files.exists(file)) return new ArrayList<>();
        try (Reader r = Files.newBufferedReader(file)) {
            Type type = TypeToken.getParameterized(List.class, Book.class).getType();
            List<Book> list = GSON.fromJson(r, type);
            return list == null ? new ArrayList<>() : list;
        }
    }
}
