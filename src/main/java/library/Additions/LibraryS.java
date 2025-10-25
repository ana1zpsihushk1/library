package library.Additions;

import library.bookFiles.Book;
import library.bookFiles.Genre;

import java.nio.file.Path;
import java.util.*;

public class LibraryS {
    private static final int PRIME = 1_000_003;
    private final Map<Integer, Book> byId = new LinkedHashMap<>();

    public Collection<Book> all() {
        return Collections.unmodifiableCollection(byId.values());
    }
    public Optional<Book> get(int id) {
        return Optional.ofNullable(byId.get(id));
    }
    public boolean exists(int id) {
        return byId.containsKey(id);
    }
    public void remove(int id) {
        byId.remove(id);
    }

    public Book addOrUpdate(Book b) {
        byId.put(b.getId(), b);
        return b;
    }

    public int generateId(String title, String author) {
        String safeTitle = Objects.requireNonNullElse(title, "");
        String safeAuthor = Objects.requireNonNullElse(author, "");

        int base = Math.abs(Objects.hash(safeTitle, safeAuthor)) % PRIME;
        int id = base;
        int attempts = 0;

        while (byId.containsKey(id)) {
            attempts++;
            id = (base + attempts * attempts) % PRIME;
            if (attempts >= PRIME) {
                throw new IllegalStateException("No available IDs");
            }
        }
        return id;
    }

    public List<Book> searchByTitle(String pattern) {
        String p = pattern.toLowerCase(Locale.ROOT);
        List<Book> res = new ArrayList<>();
        for (Book b : byId.values()) {
            if (KmpSearcher.contains(b.getTitle().toLowerCase(Locale.ROOT), p)) res.add(b);
        }
        return res;
    }

    public List<Book> filterByGenres(Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) return List.copyOf(byId.values());
        List<Book> res = new ArrayList<>();
        for (Book b : byId.values()) {
            for (Genre g : genres) {
                if (b.getGenres().contains(g)) { res.add(b); break; }
            }
        }
        return res;
    }

    public Book create(int id, String title, LinkedHashSet<Genre> genres, String author,
                       String translator, String sourceUrl, Path filePath) {
        return new Book(id, title, genres, author, translator, sourceUrl, filePath);
    }
}
