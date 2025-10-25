package library;

import library.bookFiles.*;
import library.Additions.*;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class App {
    private static final Scanner sc = new Scanner(System.in);
    private static final LibraryS service = new LibraryS();
    private static final StorageS storage = new StorageS();
    private static final DocumentsS docWriter = new DocumentsS();

    public static void main(String[] args) {
        System.out.print("Укажите путь к ПАПКЕ, в которой создать \"Библиотека\": ");
        Path base = Paths.get(sc.nextLine().trim());

        if (!Files.exists(base)) {
            System.out.println("Папка не существует. Создать? (y/n)");
            if (!sc.nextLine().trim().equalsIgnoreCase("y")) return;
            try { Files.createDirectories(base); }
            catch (Exception e) { System.out.println("Не удалось создать: " + e.getMessage()); return; }
        }
        Path libraryFolder = base.resolve("Библиотека");
        try { Files.createDirectories(libraryFolder); }
        catch (Exception e) { System.out.println("Не удалось создать папку библиотеки: " + e.getMessage()); return; }

        try {
            storage.loadCatalog(libraryFolder).forEach(service::addOrUpdate);
            System.out.println("Загружено книг: " + service.all().size());
        } catch (Exception e) {
            System.out.println("Каталог не загружен: " + e.getMessage());
        }

        while (true) {
            System.out.println("""
                \n=== Меню ===
                1. Добавить книгу
                2. Редактировать книгу
                3. Список книг
                4. Найти по названию
                5. Фильтр по жанрам
                6. Сохранить каталог в файл
                0. Выход
                """);
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> addBook(libraryFolder);
                    case "2" -> editBook();
                    case "3" -> listBooks();
                    case "4" -> searchByTitle();
                    case "5" -> filterByGenres();
                    case "6" -> {
                        storage.saveCatalog(service.all(), libraryFolder);
                        System.out.println("Каталог сохранён.");
                    }
                    case "0" -> {
                        try {
                            storage.saveCatalog(service.all(), libraryFolder);
                            System.out.println("Каталог сохранён. Выход.");
                        } catch (Exception ex) {
                            System.out.println("Не удалось сохранить перед выходом: " + ex.getMessage());
                        }
                        return;
                    }
                    default -> System.out.println("Неверный пункт.");
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private static void addBook(Path libraryFolder) throws Exception {
        System.out.print("Название: "); String title = sc.nextLine().trim();
        System.out.print("Автор: "); String author = sc.nextLine().trim();
        System.out.print("Переводчик (пусто = нет): "); String translator = sc.nextLine().trim();
        System.out.print("Ссылка на страницу: "); String url = sc.nextLine().trim();
        System.out.print("Жанры через запятую (напр. fantasy, classic): ");
        LinkedHashSet<Genre> genres = parseGenres(sc.nextLine());

        System.out.print("Вы хотите ввести текст с консоли? (y/n): ");
        Path filePath;
        if (sc.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println("Вводите текст. Завершение — пустая строка:");
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = sc.nextLine();
                if (line.isEmpty()) break;
                sb.append(line).append("\n");
            }
            System.out.print("В каком формате сохранить (txt/pdf): ");
            String fmt = sc.nextLine().trim();
            String safeName = title.replaceAll("[\\\\/:*?\"<>|]", "_");
            filePath = docWriter.saveText(libraryFolder.resolve("books"), safeName, sb.toString(), fmt);
        } else {
            System.out.print("Путь к исходному файлу: ");
            Path src = Paths.get(sc.nextLine().trim());
            if (!Files.exists(src)) { System.out.println("Файл не найден."); return; }
            System.out.print("В каком формате должен быть итог (txt/pdf): ");
            String fmt = sc.nextLine().trim();
            String text = Files.readString(src);
            String safeName = title.replaceAll("[\\\\/:*?\"<>|]", "_");
            filePath = docWriter.saveText(libraryFolder.resolve("books"), safeName, text, fmt);
        }

        int id = service.generateId(title, author);
        Book b = service.create(id, title, genres, author, translator, url, filePath);
        service.addOrUpdate(b);
        System.out.println("Добавлено: " + b);
    }

    private static void editBook() {
        System.out.print("ID книги: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        service.get(id).ifPresentOrElse(b -> {
            System.out.print("Новое название (пусто — без изменений): ");
            String t = sc.nextLine().trim();
            if (!t.isEmpty()) b.setTitle(t);

            System.out.print("Новый автор (пусто — без изменений): ");
            String a = sc.nextLine().trim();
            if (!a.isEmpty()) b.setAuthor(a);

            System.out.print("Новые жанры через запятую (пусто — без изменений): ");
            String g = sc.nextLine().trim();
            if (!g.isEmpty()) {
                Set<Genre> genres = parseGenres(g);
                b.setGenres(genres);
            }

            System.out.print("Переводчик (пусто — без изменений): ");
            String tr = sc.nextLine().trim();
            if (!tr.isEmpty()) b.setTranslator(tr);

            System.out.println("Сохранено: " + b);
        }, () -> System.out.println("Книга не найдена"));
    }

    private static void listBooks() {
        if (service.all().isEmpty()) { System.out.println("Каталог пуст."); return; }
        service.all().forEach(System.out::println);
    }

    private static void searchByTitle() {
        System.out.print("Введите подстроку названия (KMP): ");
        String q = sc.nextLine().trim();
        var res = service.searchByTitle(q);
        if (res.isEmpty()) System.out.println("Ничего не найдено.");
        else res.forEach(System.out::println);
    }

    private static void filterByGenres() {
        System.out.print("Жанры через запятую: ");
        Set<Genre> genres = parseGenres(sc.nextLine());
        var res = service.filterByGenres(genres);
        if (res.isEmpty()) System.out.println("Пусто.");
        else res.forEach(System.out::println);
    }

    private static void removeBook() {
        System.out.print("ID книги: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        if (service.exists(id)) { service.remove(id); System.out.println("Удалено."); }
        else System.out.println("Не найдено.");
    }

    private static LinkedHashSet<Genre> parseGenres(String input) {
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Genre::fromString)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
