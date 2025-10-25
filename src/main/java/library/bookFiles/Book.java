package library.bookFiles;

import java.nio.file.Path;
import java.util.*;

public class Book {
    private final int id;
    private String title;
    private Set<Genre> genres = new LinkedHashSet<>();
    private String author;
    private String translator;
    private String sourceUrl;
    private Path filePath;

    public Book(int id, String title, Set<Genre> genres, String author,
                String translator, String sourceUrl, Path filePath) {
        this.id = id;
        this.title = title;
        if (genres != null) {
            this.genres = new LinkedHashSet<>(genres);
        }
        this.author = author;
        this.translator = Objects.requireNonNullElse(translator, "original");
        this.sourceUrl = sourceUrl;
        this.filePath = filePath;
    }

    public int getId() {
        return this.id;
    }
    public String getTitle() {
        return this.title;
    }
    public Set<Genre> getGenres() {
        return Collections.unmodifiableSet(this.genres);
    }
    public String getAuthor() {
        return this.author;
    }
    public String getTranslator() {
        return this.translator;
    }
    public String getSourceUrl() {
        return this.sourceUrl;
    }
    public Path getFilePath() {
        return this.filePath;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setGenres(Set<Genre> genres) {
        this.genres = new LinkedHashSet<>(genres);
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public void setTranslator(String translator) {
        this.translator = Objects.requireNonNullElse(translator, "original");
    }
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public String displayTranslator() {
        if ((translator == null || translator.isBlank()) ||
                (author != null && author.equals(translator))) {
            return "Оригинал";
        }
        return translator;
    }

    @Override
    public String toString() {
        return "%d | %s | %s | автор: %s | перевод: %s | файл: %s".formatted(id, title, genres, author, displayTranslator(),
                filePath == null ? "-" : filePath.toString());
    }
}
